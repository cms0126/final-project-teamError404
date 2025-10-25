package com.error404.geulbut.jpa.wishlist.controller;

import com.error404.geulbut.jpa.wishlist.dto.WishlistDto;
import com.error404.geulbut.jpa.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    /**
     * 📌 위시리스트 조회 (GET /wishlist)
     */
    @GetMapping
    public ResponseEntity<List<WishlistDto>> getWishlist(Authentication authentication) {
        String userId = authentication.getName();
        log.info("📌 [GET] 위시리스트 조회 요청 - userId: {}", userId);

        List<WishlistDto> wishlist = wishlistService.getWishlist(userId);
        log.info("➡️ [결과 확인] {}건 반환", wishlist.size());

        return ResponseEntity.ok(wishlist);
    }

    /**
     * 위시리스트 추가 (POST /wishlist)
     */
    @PostMapping
    public ResponseEntity<?> addWishlist(Authentication authentication,
                                         @RequestParam Long bookId) {
        String userId = authentication.getName();
        log.info("[POST] 위시리스트 추가요청 - userID: {}, bookId: {}", userId, bookId);

        try {
            wishlistService.addWishlist(userId, bookId);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            log.error("위시리스트 추가 실패 -userId: {}, bookId: {}", userId, bookId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "fail", "message", e.getMessage()));
        }
    }

    @PostMapping("/move-to-cart")
    public ResponseEntity<?> moveToCart(Authentication authentication,
                                        @RequestParam Long bookId,
                                        @RequestParam(defaultValue = "1") int quantity) {
        String userId = authentication.getName();
        wishlistService.moveToCart(userId, bookId, quantity);
        return ResponseEntity.ok(Map.of("status", "ok", "message", "장바구니로 이동 완료"));
    }

    /** 📌 위시리스트 삭제 (DELETE /wishlist/{bookId}) */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> removeWishlist(Authentication authentication,
                                            @PathVariable Long bookId) {
        String userId = authentication.getName();
        log.info("[DELETE] 위시리스트 삭제 요청 - userId: {}, bookId: {}", userId, bookId);

        try{
            wishlistService.removeWishlist(userId, bookId);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            log.error("위시리스트 삭제 실패 - userId: {}, bookId: {}", userId, bookId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "fail", "message", e.getMessage()));
        }


    }
}