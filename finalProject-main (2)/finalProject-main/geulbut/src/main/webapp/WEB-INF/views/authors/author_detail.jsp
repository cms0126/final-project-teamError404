<!-- src/main/webapp/WEB-INF/views/authors/author_detail.jsp -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${author.name}"/> - 작가 상세</title>

    <!-- 전역 CSS (요구사항) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/00_common.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css"/>

    <!-- 메타 -->
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>

<jsp:include page="/common/header.jsp"/>

<main class="container">

    <!-- 브레드크럼 -->
    <nav class="mt-24 mb-16">
        <a href="${pageContext.request.contextPath}/" class="link">홈</a>
        <span class="mx-8">›</span>
        <a href="${pageContext.request.contextPath}/authors" class="link">작가</a>
        <span class="mx-8">›</span>
        <strong><c:out value="${author.name}"/></strong>
    </nav>

    <!-- 작가 프로필 -->
    <section class="row gap-24 items-start mb-40">
        <div class="col-auto">
            <c:choose>
                <c:when test="${not empty author.imgUrl}">
                    <img src="<c:out value='${author.imgUrl}'/>"
                         alt="<c:out value='${author.name}'/> 작가 사진"
                         class="rounded shadow-sm"
                         style="width:160px;height:160px;object-fit:cover;">
                </c:when>
                <c:otherwise>
                    <div class="rounded shadow-sm bg-surface flex-center"
                         style="width:160px;height:160px;">
                        <span class="text-muted">No Image</span>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="col">
            <h1 class="h2 mb-8"><c:out value="${author.name}"/></h1>
            <p class="text-body mb-12" style="white-space:pre-line;">
                <c:out value="${author.description}"/>
            </p>
            <c:if test="${not empty author.createdAt}">
                <p class="text-muted">
                    등록일 :
                    <fmt:formatDate value="${author.createdAt}" pattern="yyyy.MM.dd HH:mm"/>
                </p>
            </c:if>
        </div>
    </section>

    <!-- 도서 목록 헤더 -->
    <section class="mb-16">
        <h2 class="h3">이 작가의 도서</h2>
        <c:if test="${empty books}">
            <p class="text-muted mt-8">등록된 도서가 없습니다.</p>
        </c:if>
    </section>

    <!-- 도서 그리드 -->
    <c:if test="${not empty books}">
        <section class="grid cols-5 gap-16">
            <c:forEach var="book" items="${books}">
                <article class="card bg-surface border rounded shadow-sm p-12">
                    <!-- 표지 -->
                    <a href="${pageContext.request.contextPath}/books/${book.bookId}" class="block mb-12">
                        <c:choose>
                            <c:when test="${not empty book.imgUrl}">
                                <img src="<c:out value='${book.imgUrl}'/>"
                                     alt="<c:out value='${book.title}'/> 표지"
                                     style="width:100%;height:220px;object-fit:cover;border-radius:8px;">
                            </c:when>
                            <c:otherwise>
                                <div class="bg-muted flex-center" style="width:100%;height:220px;border-radius:8px;">
                                    <span class="text-muted">No Cover</span>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </a>

                    <!-- 제목 -->
                    <h3 class="h6 mb-6">
                        <a href="${pageContext.request.contextPath}/books/${book.bookId}" class="link">
                            <c:out value="${book.title}"/>
                        </a>
                    </h3>

                    <!-- 가격 -->
                    <c:choose>
                        <c:when test="${not empty book.discountedPrice && book.discountedPrice gt 0 && book.discountedPrice lt book.price}">
                            <div class="mb-6">
                                <span class="text-danger fw-bold mr-8">
                                    <fmt:formatNumber value="${book.discountedPrice}" type="number"/>원
                                </span>
                                <span class="text-muted" style="text-decoration:line-through;">
                                    <fmt:formatNumber value="${book.price}" type="number"/>원
                                </span>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="mb-6">
                                <span class="fw-bold">
                                    <fmt:formatNumber value="${book.price}" type="number"/>원
                                </span>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <!-- 해시태그 -->
                    <c:if test="${not empty book.hashtags}">
                        <div class="mt-6">
                            <c:forEach var="tag" items="${book.hashtags}" varStatus="s">
                                <span class="badge mr-6 mb-6">#<c:out value="${tag}"/></span>
                            </c:forEach>
                        </div>
                    </c:if>

                    <!-- 출간/출판사 (있을 때만) -->
                    <div class="text-sm text-muted mt-8">
                        <c:if test="${not empty book.publisherName}">
                            <span class="mr-8"><c:out value="${book.publisherName}"/></span>
                        </c:if>
                        <c:if test="${not empty book.publishedDate}">
                            <span>
                                <fmt:formatDate value="${book.publishedDate}" pattern="yyyy.MM.dd"/>
                            </span>
                        </c:if>
                    </div>
                </article>
            </c:forEach>
        </section>
    </c:if>

</main>

</body>
</html>
