<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Book Hashtags 조회</title>

    <!-- 폰트(선택) / 부트스트랩(선택: 기존 화면과 톤 맞추기용) -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>

    <!-- 외부 CSS (절대 style 태그 금지) -->
    <link rel="stylesheet" href="<%=ctx%>/css/00_common.css" />
    <link rel="stylesheet" href="<%=ctx%>/css/book/hashtags-mono.css" />
</head>
<body>
<!-- 상단바 -->
<jsp:include page="/common/admin_page_header.jsp" />


<main id="ht-app" class="ht-container" role="main">
    <!-- breadcrumb -->
    <nav class="ht-breadcrumb" aria-label="breadcrumb">
        <a href="<%=ctx%>/">Home</a><span class="sep">/</span>
        <a href="<%=ctx%>/shop/main.do">Shop</a><span class="sep">/</span>
        <span class="current" aria-current="page">Book Hashtags</span>
    </nav>

    <!-- 검색 패널 -->
    <section id="ht-filter" class="ht-panel" aria-labelledby="ht-filter-title">
        <div class="ht-panel__head">
            <h2 id="ht-filter-title" class="ht-panel__title">검색</h2>
        </div>

        <form class="ht-searchgrid" action="/book-hashtags/search" method="get" novalidate>
            <div class="ht-field">
                <label class="ht-label" for="qBook">책 검색</label>
                <input id="qBook" class="ht-input" type="text" name="bookQuery" value="${queryBook}" placeholder="제목 등으로 검색" />
            </div>

            <div class="ht-actions">
                <button class="ht-btn" type="submit" name="target" value="book">검색</button>
            </div>

            <div class="ht-divider" role="separator" aria-hidden="true"></div>

            <div class="ht-field">
                <label class="ht-label" for="qTag">해시태그 검색</label>
                <input id="qTag" class="ht-input" type="text" name="hashtagQuery" value="${queryHashtag}" placeholder="#태그명으로 검색" />
            </div>

            <div class="ht-actions">
                <button class="ht-btn" type="submit" name="target" value="tag">검색</button>
            </div>
        </form>
    </section>

    <!-- 책 기준 해시태그 목록 -->
    <section class="ht-panel" aria-labelledby="ht-book-title">
        <div class="ht-panel__head">
            <h2 id="ht-book-title" class="ht-panel__title">책 검색 결과
                <span class="ht-badge" id="ht-count-book">0</span>
            </h2>
        </div>

        <c:choose>
            <c:when test="${not empty bookSearchResult}">
                <div class="ht-tablewrap">
                    <table class="ht-table" id="ht-table-book">
                        <thead>
                        <tr>
                            <th>책 ID</th>
                            <th>책 제목</th>
                            <th>해시태그 ID</th>
                            <th>해시태그 이름</th>
                            <th>연결 생성일</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="bh" items="${bookSearchResult}">
                            <tr>
                                <td>${bh.book.bookId}</td>
                                <td class="ht-ellipsis" title="${bh.book.title}">${bh.book.title}</td>
                                <td>${bh.hashtag.hashtagId}</td>
                                <td>#${bh.hashtag.name}</td>
                                <td>${bh.createdAt}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <p class="ht-empty">검색된 책이 없습니다.</p>
            </c:otherwise>
        </c:choose>
    </section>

    <!-- 해시태그 기준 책 목록 -->
    <section class="ht-panel" aria-labelledby="ht-tag-title">
        <div class="ht-panel__head">
            <h2 id="ht-tag-title" class="ht-panel__title">해시태그 검색 결과
                <span class="ht-badge" id="ht-count-tag">0</span>
            </h2>
        </div>

        <c:choose>
            <c:when test="${not empty hashtagSearchResult}">
                <div class="ht-tablewrap">
                    <table class="ht-table" id="ht-table-tag">
                        <thead>
                        <tr>
                            <th>해시태그 ID</th>
                            <th>해시태그 이름</th>
                            <th>책 ID</th>
                            <th>책 제목</th>
                            <th>연결 생성일</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="bh" items="${hashtagSearchResult}">
                            <tr>
                                <td>${bh.hashtag.hashtagId}</td>
                                <td>#${bh.hashtag.name}</td>
                                <td>${bh.book.bookId}</td>
                                <td class="ht-ellipsis" title="${bh.book.title}">${bh.book.title}</td>
                                <td>${bh.createdAt}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <p class="ht-empty">검색된 해시태그가 없습니다.</p>
            </c:otherwise>
        </c:choose>
    </section>

    <p class="ht-footnote">© Geulbut Book Hashtags</p>
</main>

<!-- 외부 JS (최소 침습, 기존 서버 검색 유지) -->
<script src="<%=ctx%>/js/book/hashtags-mono.js" defer></script>
<!-- 관리자 헤더 드롭다운 스크립트 -->
<script src="<%=ctx%>/js/admin/admin_page_header.js" defer></script>
</body>
</html>
