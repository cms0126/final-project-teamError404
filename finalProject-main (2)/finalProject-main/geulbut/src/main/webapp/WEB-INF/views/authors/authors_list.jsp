<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
<head>
  <title>작가 목록</title>
  <!-- 전역 공통 -->
  <link rel="stylesheet" href="/css/00_common.css">
  <link rel="stylesheet" href="/css/header.css">
  <link rel="stylesheet" href="/css/authors/authors_list.css">
</head>
<body class="bg-main">
<jsp:include page="/common/header.jsp"/>

<div class="page py-4">
  <!-- 상단 헤더 -->
  <section class="row items-center justify-between mb-4">
    <h1 class="m-0">작가 목록</h1>
    <p class="text-light m-0">총 <strong>${page.totalElements}</strong>명</p>
  </section>

  <!-- 비어있을 때 -->
  <c:if test="${empty author}">
    <article class="bg-surface border rounded shadow-sm p-4">
      <p class="m-0">등록된 작가가 없습니다.</p>
    </article>
  </c:if>

  <!-- 카드 그리드 -->
  <section class="author-grid">
    <c:forEach var="a" items="${author}">
      <article class="author-card bg-surface border rounded shadow-sm p-3">
        <!-- 상단: 프로필 -->
        <div class="row gap-3 items-center mb-2">
          <div class="avatar size-14 rounded border overflow-hidden bg-muted">
            <c:choose>
              <c:when test="${not empty a.imgUrl}">
                <img src="${a.imgUrl}" alt="${a.name}" class="w-full h-auto block"/>
              </c:when>
              <c:otherwise>
                <img src="/images/thumb_ing.gif" class="w-full h-auto block"/>
              </c:otherwise>
            </c:choose>
          </div>
          <div class="grow">
            <h3 class="m-0">
              <a href="/authors/${a.authorId}" class="text-main underline-hover">
                  ${a.name}
              </a>
            </h3>
            <p class="text-light m-0">
              등록일:
              <c:choose>
                <c:when test="${not empty a.createdAt}">
                  <!-- 날짜 포맷 없이 그대로 출력 (문자열/LocalDateTime 모두 안전) -->
                  <c:out value="${a.createdAt}"/>
                </c:when>
                <c:otherwise>-</c:otherwise>
              </c:choose>
            </p>
          </div>
        </div>

        <!-- 본문: 소개 요약 -->
        <div class="mb-2">
          <c:choose>
            <c:when test="${not empty a.description}">
              <p class="m-0">
                <c:set var="desc" value="${a.description}" />
                <c:out value="${fn:length(desc) > 130 ? fn:substring(desc,0,130) : desc}"/>
                <c:if test="${fn:length(desc) > 130}">…</c:if>
              </p>
            </c:when>
            <c:otherwise>
              <p class="text-light m-0">소개가 등록되지 않았습니다.</p>
            </c:otherwise>
          </c:choose>
        </div>

        <!-- 하단: 액션 -->
        <div class="row justify-end mt-3">
          <a href="/books/search?q=${a.name}" class="btn bg-accent text-invert rounded px-3 py-2">
            작품 목록
          </a>
        </div>
      </article>
    </c:forEach>
  </section>

  <!-- 페이지네이션 (하단 고정) -->
  <c:if test="${page.totalPages > 1}">
    <nav class="mt-5">
      <ul class="row gap-1 m-0 p-0">
        <!-- 이전 -->
        <li>
          <c:choose>
            <c:when test="${page.hasPrevious()}">
              <a class="border rounded p-2 bg-surface" href="?page=${page.number - 1}&size=${page.size}">이전</a>
            </c:when>
            <c:otherwise>
              <span class="border rounded p-2 text-light">이전</span>
            </c:otherwise>
          </c:choose>
        </li>

        <!-- 번호 -->
        <c:forEach var="i" begin="0" end="${page.totalPages - 1}">
          <li>
            <c:choose>
              <c:when test="${i == page.number}">
                <span class="border rounded p-2 bg-accent text-invert">${i + 1}</span>
              </c:when>
              <c:otherwise>
                <a class="border rounded p-2 bg-surface" href="?page=${i}&size=${page.size}">${i + 1}</a>
              </c:otherwise>
            </c:choose>
          </li>
        </c:forEach>

        <!-- 다음 -->
        <li>
          <c:choose>
            <c:when test="${page.hasNext()}">
              <a class="border rounded p-2 bg-surface" href="?page=${page.number + 1}&size=${page.size}">다음</a>
            </c:when>
            <c:otherwise>
              <span class="border rounded p-2 text-light">다음</span>
            </c:otherwise>
          </c:choose>
        </li>
      </ul>
    </nav>
  </c:if>
</div>

</body>
</html>
