package com.error404.geulbut.jpa.carts.service;

import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.books.repository.BooksRepository;
import com.error404.geulbut.jpa.orderitem.entity.OrderItem;
import com.error404.geulbut.jpa.orderitem.repository.OrderItemRepository;
import com.error404.geulbut.jpa.orders.entity.Orders;
import com.error404.geulbut.jpa.orders.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BooksRepository booksRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrdersRepository ordersRepository;


    @Value("${portone.api_key}")
    private String apikey;

    @Value("${portone.api_secret}")
    private String apiSecret;

    // ---- RestTemplate with logging interceptor ----
    private RestTemplate restTemplate;

    {
        // 바디를 여러 번 읽을 수 있도록 버퍼링 팩토리 사용
        SimpleClientHttpRequestFactory simple = new SimpleClientHttpRequestFactory();
        BufferingClientHttpRequestFactory buffering = new BufferingClientHttpRequestFactory(simple);
        restTemplate = new RestTemplate(buffering);
        restTemplate.getInterceptors().add(new HttpLoggingInterceptor());
    }

    // Access Token 캐싱
    private String cachedToken;
    private Instant tokenExpiry;

    // 만료시간(백오프)
    private static final long EXPIRY_DURATION_SECONDS = 5000;

    public String getAccessToken() {
        try {
            if (cachedToken != null && tokenExpiry != null && Instant.now().isBefore(tokenExpiry)) {
                log.debug("[PORTONE] reuse cached token (exp={})", tokenExpiry);
                return cachedToken;
            }

            String url = "https://api.iamport.kr/users/getToken";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of(
                    "imp_key", apikey,
                    "imp_secret", apiSecret
            );

            log.info("[PORTONE] getToken -> url={}, body(secure)={{imp_key:{}, imp_secret:***masked***}}", url, apikey);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            log.info("[PORTONE] getToken <- status={}, body={}", response.getStatusCode(), response.getBody());

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("토큰 발급 실패: " + response.getStatusCode());
            }

            Map<String, Object> bodyMap = response.getBody();
            if (bodyMap == null || bodyMap.get("response") == null) {
                throw new IllegalStateException("API 응답이 비어있음");
            }

            Map<String, Object> res = (Map<String, Object>) bodyMap.get("response");
            String accessToken = (String) res.get("access_token");

            Object expiredAtObj = res.get("expired_at");
            Instant expiry;
            if (expiredAtObj instanceof Number) {
                long epochSec = ((Number) expiredAtObj).longValue();
                expiry = Instant.ofEpochSecond(epochSec).minusSeconds(60);
            } else {
                expiry = Instant.now().plusSeconds(EXPIRY_DURATION_SECONDS);
            }

            this.cachedToken = accessToken;
            this.tokenExpiry = expiry;

            log.info("[PORTONE] token issued. exp={}", expiry);
            return accessToken;
        } catch (HttpClientErrorException e) {
            log.error("[PORTONE] getToken 4xx body={}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (HttpServerErrorException e) {
            log.error("[PORTONE] getToken 5xx body={}", e.getResponseBodyAsString(), e);
            throw e;
        }
    }

    public Map<String, Object> verifyPayment(String token, String impUid) {
        try {
            String url = "https://api.iamport.kr/payments/" + impUid;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            log.info("[PORTONE] verify -> url={}, imp_uid={}", url, impUid);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            log.info("[PORTONE] verify <- status={}, body={}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> bodyMap = response.getBody();
                if (bodyMap == null || bodyMap.get("response") == null) {
                    throw new IllegalStateException("API 응답이 비어있음");
                }
                return (Map<String, Object>) bodyMap.get("response");
            }

            throw new IllegalStateException("결제 검증 실패: " + response.getStatusCode());
        } catch (HttpClientErrorException e) {
            log.error("[PORTONE] verify 4xx body={}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (HttpServerErrorException e) {
            log.error("[PORTONE] verify 5xx body={}", e.getResponseBodyAsString(), e);
            throw e;
        }
    }

    public Map<String, Object> cancelPayment(String token, String impUid, String reason) {
        try {
            String url = "https://api.iamport.kr/payments/cancel";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token);

            Map<String, Object> body = Map.of(
                    "imp_uid", impUid,
                    "reason", (reason != null && !reason.isBlank()) ? reason : "사용자 요청 취소"
            );

            log.info("[PORTONE] cancel -> url={}, body={}", url, body);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            log.info("[PORTONE] cancel <- status={}, body={}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> bodyMap = response.getBody();
                if (bodyMap == null) {
                    throw new IllegalStateException("API 응답이 비어있음");
                }
                Object resp = bodyMap.get("response");
                Object code = bodyMap.get("code");
                Object message = bodyMap.get("message");

                // PortOne은 200이라도 code != 0 이면 실패로 볼 수 있음
                if (resp != null) {
                    return (Map<String, Object>) resp;
                } else {
                    throw new IllegalStateException("취소 실패(code=" + code + "): " + message);
                }
            }

            log.error("[PORTONE] 취소 실패 HTTP: {}, body={}", response.getStatusCode(), response.getBody());
            throw new IllegalStateException("결제 취소 실패: " + response.getStatusCode());
        } catch (HttpClientErrorException e) {
            log.error("[PORTONE] cancel 4xx body={}", e.getResponseBodyAsString(), e);
            throw new IllegalStateException("취소 실패(4xx): " + e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            log.error("[PORTONE] cancel 5xx body={}", e.getResponseBodyAsString(), e);
            throw new IllegalStateException("취소 실패(5xx): " + e.getResponseBodyAsString(), e);
        }
    }

    /**
     * 모든 HTTP 요청/응답을 로깅하는 인터셉터
     */
    static class HttpLoggingInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(
                HttpRequest request, byte[] body, ClientHttpRequestExecution execution
        ) throws IOException {

            // Request Log
            log.debug(">>> HTTP {} {}", request.getMethod(), request.getURI());
            log.debug(">>> Headers: {}", request.getHeaders());
            if (body != null && body.length > 0) {
                log.debug(">>> Body: {}", new String(body, StandardCharsets.UTF_8));
            }

            ClientHttpResponse response = execution.execute(request, body);

            // Response Log
            byte[] respBody = StreamUtils.copyToByteArray(response.getBody());
            String respText = new String(respBody, StandardCharsets.UTF_8);

            log.debug("<<< Status: {} {}", response.getStatusCode(), response.getStatusText());
            log.debug("<<< Headers: {}", response.getHeaders());
            log.debug("<<< Body: {}", respText);

            return response;

        }
    }

    @Transactional
    public void processOrder(String impUid) {
        String token = getAccessToken();
        Map<String, Object> paymentInfo = verifyPayment(token, impUid);

        String merchantUid = (String) paymentInfo.get("merchant_uid");
        Orders order = ordersRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보 없음"));

        // 주문에 속한 모든 아이템 조회
        List<OrderItem> items = orderItemRepository.findByOrderOrderId(order.getOrderId());

        for (OrderItem item : items) {
            Books book = booksRepository.findById(item.getBook().getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("책 없음"));
            book.setOrderCount(book.getOrderCount() + item.getQuantity());
            booksRepository.save(book);
        }
    }

}


