<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
  <title>작가 작품 검색 결과</title>
  <link rel="stylesheet" href="/css/00_common.css">
  <link rel="stylesheet" href="/css/header.css">
  <link rel="stylesheet" href="/css/book_all/book_all.css"> <!-- 기존 카드 CSS 재사용 -->
</head>
<body class="bg-main">
<jsp:include page="/common/header.jsp"/>

<div class="page py-4">
  <c:choose>
    <c:when test="${empty books}">
      <div class="card p-3 border rounded bg-surface">검색 결과가 없습니다.</div>
    </c:when>

    <c:otherwise>
      <ol class="grid gap-3">
        <c:forEach var="book" items="${books}">
          <li class="srch-item bg-surface border rounded shadow-sm p-3">

            <!-- 체크박스 -->
            <div class="srch-col-check row">
              <input type="checkbox" name="selected" value="${book.bookId}">
            </div>

            <!-- 썸네일 -->
            <a class="srch-thumb rounded-sm border bg-main"
               href="${pageContext.request.contextPath}/book/${book.bookId}">
              <img src="${empty book.imgUrl ? '/images/thumb_ing.gif' : book.imgUrl}"
                   alt="${fn:escapeXml(book.title)} 표지">
            </a>

            <!-- 정보 -->
            <div class="srch-info">
              <h3 class="mb-1 srch-title">
                <a href="${pageContext.request.contextPath}/book/${book.bookId}">
                    ${book.title}
                </a>
              </h3>

              <p class="mb-2 text-light">
                <c:if test="${not empty book.authorName}">${book.authorName}</c:if>
                <c:if test="${not empty book.publisherName}"> | ${book.publisherName}</c:if>
              </p>

              <!-- 가격 -->
              <div class="row gap-2 mb-2">
                <span class="text-light strike">
                  <fmt:formatNumber value="${book.price}" type="number"/>원
                </span>
                <c:choose>
                  <c:when test="${book.discountedPrice != null && book.discountedPrice < book.price}">
                    <span class="accent-strong">
                      <fmt:formatNumber value="${book.discountedPrice}" type="number"/>원
                    </span>
                    <span class="accent-strong">
                      <fmt:formatNumber value="${(1 - (book.discountedPrice * 1.0 / book.price)) * 100}" maxFractionDigits="0"/>%할인
                    </span>
                  </c:when>
                  <c:otherwise>
                    <span class="text-light">할인 없음</span>
                  </c:otherwise>
                </c:choose>
              </div>

              <!-- 액션 버튼 -->
              <div class="row gap-2">
                <button type="button" class="px-3 py-2 rounded bg-accent text-invert" data-act="cart" data-id="${book.bookId}">장바구니</button>
                <button type="button" class="px-3 py-2 border rounded bg-surface" data-act="buy" data-id="${book.bookId}">바로구매</button>
                <button type="button" class="px-3 py-2 border rounded bg-surface" data-act="like" data-id="${book.bookId}">위시리스트</button>
              </div>

            </div>
          </li>
        </c:forEach>
      </ol>
    </c:otherwise>
  </c:choose>
</div>

<script src="/js/book_all/book_all.js"></script>
</body>
</html>
