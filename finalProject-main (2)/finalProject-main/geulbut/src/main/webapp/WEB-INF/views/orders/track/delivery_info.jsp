<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>배송조회</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<c:url value='/css/00_common.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/orders/deliveryInfo.css'/>">
    <link rel="stylesheet" href="/css/header.css">
</head>
<body>
<jsp:include page="/common/header.jsp"></jsp:include>
<main class="page page--track" aria-labelledby="pageTitle">
    <h1 id="pageTitle" class="sr-only">배송조회</h1>

    <c:set var="o" value="${delivery.ordersDto}"/>
    <c:set var="vStatus" value="${delivery.viewDeliveryStatus}"/>

    <article class="card">
        <header class="card__header">
            <h2 class="card__title">주문 배송 현황</h2>
            <c:choose>
                <c:when test="${vStatus == 'DELIVERED'}"><span class="status status--done">배송완료</span></c:when>
                <c:when test="${vStatus == 'IN_TRANSIT'}"><span class="status status--in">배송중</span></c:when>
                <c:otherwise><span class="status status--ready">배송준비</span></c:otherwise>
            </c:choose>
        </header>

        <div class="grid grid--2">
            <div class="kv">
                <span class="k">도착시간/예정</span>
                <span class="v">
                    <c:choose>
                        <c:when test="${not empty o.deliveredAtFormatted}">
                            ${o.deliveredAtFormatted}
                        </c:when>
                        <c:when test="${vStatus == 'IN_TRANSIT'}">
                            출고 후 1~2일 내 도착 예상
                        </c:when>
                        <c:when test="${vStatus == 'READY'}">
                            준비중 (결제 완료 기준)
                        </c:when>
                        <c:otherwise>—</c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="kv">
                <span class="k">수취인</span>
                <span class="v">${empty o.recipient ? '—' : o.recipient}</span>
            </div>
            <div class="kv">
                <span class="k">배송지</span>
                <span class="v">${empty o.address ? '—' : o.address}</span>
            </div>
            <div class="kv">
                <span class="k">결제수단</span>
                <span class="v">${empty o.paymentMethod ? '—' : o.paymentMethod}</span>
            </div>
        </div>

        <c:if test="${not empty o.items}">
            <ul class="order-items">
                <c:forEach var="it" items="${o.items}">
                    <li class="order-item">
                        <div class="oi-wrap">
                            <c:if test="${not empty it.imageUrl}">
                                <img src="${it.imageUrl}" alt="${it.title}" class="oi-thumb"/>
                            </c:if>
                            <div class="oi-info">
                                <div class="oi-title">${it.title}</div>
                                <div class="oi-meta">
                                    수량: ${it.quantity}
                                    · 단가: <fmt:formatNumber value="${it.price}" type="number"/>원
                                    · 소계: <fmt:formatNumber value="${it.price * it.quantity}" type="number"/>원
                                </div>
                            </div>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </c:if>
    </article>

    <c:if test="${not empty history}">
        <article class="card">
            <header class="card__header">
                <h3 class="card__title">지난 배송완료 내역</h3>
            </header>

            <ul class="history-list compact">
                <c:forEach var="h" items="${history}">
                    <c:url var="detailUrl" value="/orders/${h.orderId}/delivery"/>
                    <li class="history-row">
                        <a class="row-link" href="${detailUrl}">
                            <span class="h-col no">#${h.orderId}</span>
                            <span class="h-col date">
                                <c:choose>
                                    <c:when test="${not empty h.deliveredAtFormatted}">${h.deliveredAtFormatted}</c:when>
                                    <c:when test="${not empty h.deliveredAt}">${h.deliveredAt}</c:when>
                                    <c:otherwise>—</c:otherwise>
                                </c:choose>
                            </span>
                            <span class="h-col sum"><fmt:formatNumber value="${h.totalPrice}" type="number"/>원</span>
                            <span class="h-col items">
                                <c:choose>
                                    <c:when test="${not empty h.items}">
                                        <c:set var="cnt" value="${fn:length(h.items)}"/>
                                        <c:set var="first" value="${h.items[0]}"/>
                                        ${first.title}<c:if test="${cnt > 1}"> 외 ${cnt - 1}건</c:if>
                                    </c:when>
                                    <c:otherwise>—</c:otherwise>
                                </c:choose>
                            </span>
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </article>
    </c:if>

</main>
</body>
</html>
