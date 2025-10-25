<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <script>window.ctx = "${ctx}";</script>
    <title>관리자 - 작가 관리</title>

    <link rel="stylesheet" href="${ctx}/css/00_common.css"/>
    <link rel="stylesheet" href="${ctx}/css/header.css"/>
    <link rel="stylesheet" href="${ctx}/css/admin/admin_base.css"/>
    <link rel="stylesheet" href="${ctx}/css/admin/admin_authors.css"/>

    <!-- (도서 페이지와 동일 버전) -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>

<body class="bg-main text-main admin-authors has-bg">
<jsp:include page="/common/admin_page_header.jsp" />

<div class="page">
    <h1 class="mt-4 mb-4">작가 관리</h1>

    <!-- 검색 -->
    <div class="search-wrapper">
        <form id="authorSearchForm" method="get" action="${ctx}/admin/authors" class="search-form">
            <input type="text" name="keyword" id="keyword" value="${param.keyword}" placeholder="작가명 검색" />
            <button type="submit" class="btn-search">검색</button>
        </form>
    </div>

    <!-- 상단 툴바 -->
    <div class="toolbar">
        <button type="button"
                class="btn btn-light btn--liquid-glass"
                id="btnAddAuthor">작가 등록</button>
    </div>

    <!-- 작가 목록 테이블 (도서 페이지와 동일한 table 카드/스크롤 래퍼 구조) -->
    <div class="table-scroll">
        <table class="admin-table admin-authors-table" id="authorsTable" data-ctx="${ctx}">
            <colgroup>
                <col class="col-id" />
                <col class="col-name" />
                <col class="col-img" />
                <col class="col-created" />
                <col class="col-desc" />
                <col class="col-actions" />
            </colgroup>
            <thead>
            <tr>
                <th>ID</th>
                <th>이름</th>
                <th>이미지</th>
                <th class="hide-lg">생성일</th>
                <th>설명</th>
                <th>작업</th>
            </tr>
            </thead>
            <tbody id="authorsTableBody">
            <c:forEach var="author" items="${authorsPage.content}">
                <tr class="data-row"
                    data-id="${author.authorId}"
                    data-name="${author.name}"
                    data-description="${author.description}"
                    data-imgurl="${author.imgUrl}"
                    data-createdat="${author.createdAt}">
                    <td>${author.authorId}</td>
                    <td class="t-left author-name">${author.name}</td>
                    <td>
                        <c:if test="${not empty author.imgUrl}">
                            <img src="${author.imgUrl}" class="author-thumb" alt="${author.name}" />
                        </c:if>
                    </td>
                    <td class="created-at-cell hide-lg">${author.createdAtFormatted}</td>
                    <td class="t-left author-description">${author.description}</td>
                    <td>
                        <button type="button"
                                class="btn btn-primary  btn--liquid-glass btnEdit">수정</button>
                        <button type="button"
                                class="btn btn-danger   btn--liquid-glass btnDelete">삭제</button>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- 페이징 (도서 페이지와 동일한 Materia 버튼그룹 UI) -->
    <c:if test="${authorsPage.totalPages > 0}">
        <div class="btn-toolbar pagination-toolbar" role="toolbar" aria-label="페이지네이션">
            <div class="btn-group" role="group" aria-label="페이지">

                <!-- 이전 («) -->
                <c:choose>
                    <c:when test="${authorsPage.first}">
                        <a class="btn btn-secondary btn-nav" aria-label="이전" aria-disabled="true">&laquo;</a>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-secondary btn-nav"
                           href="?page=${authorsPage.number - 1}&keyword=${param.keyword}"
                           aria-label="이전">&laquo;</a>
                    </c:otherwise>
                </c:choose>

                <!-- 숫자들 -->
                <c:forEach var="i" begin="0" end="${authorsPage.totalPages - 1}">
                    <a class="btn btn-secondary ${i == authorsPage.number ? 'active' : ''}"
                       href="?page=${i}&keyword=${param.keyword}"
                        ${i == authorsPage.number ? 'aria-current="page"' : ''}>
                            ${i + 1}
                    </a>
                </c:forEach>

                <!-- 다음 (») -->
                <c:choose>
                    <c:when test="${authorsPage.last}">
                        <a class="btn btn-secondary btn-nav" aria-label="다음" aria-disabled="true">&raquo;</a>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-secondary btn-nav"
                           href="?page=${authorsPage.number + 1}&keyword=${param.keyword}"
                           aria-label="다음">&raquo;</a>
                    </c:otherwise>
                </c:choose>

            </div>
        </div>
    </c:if>
</div>

<p class="ht-footnote">© Geulbut Admin Authors List</p>

<!-- 작가 등록/수정 모달 -->
<div id="authorModal" aria-hidden="true" role="dialog" aria-modal="true" style="display:none;">
    <div class="modal__dialog" role="document">
        <div class="modal__header">
            <h3 id="modalTitle" class="mt-3 mb-3 ml-3">작가 등록</h3>
            <button type="button" class="modal__close btn--liquid is-circle" id="btnCloseModal" aria-label="닫기">×</button>
        </div>

        <form id="authorForm" class="modal__form">
            <input type="hidden" id="modalAuthorId" />
            <label>이름<input type="text" id="modalAuthorName" placeholder="작가명을 입력하세요" required /></label>
            <label>이미지 URL<input type="text" id="modalAuthorImgUrl" placeholder="https://example.com/image.jpg" /></label>
            <label>생성일<input type="text" id="modalAuthorCreatedAt" placeholder="수정 시 자동 채움" readonly /></label>
            <label style="grid-column:1 / -1;">설명<textarea id="modalAuthorDescription" rows="4" placeholder="간단한 소개나 메모를 입력하세요"></textarea></label>
            <div style="grid-column:1 / -1;">
                <span style="display:block; margin-bottom:4px; font-size:.9rem;">미리보기</span>
                <img id="modalAuthorImgPreview" src="" alt="작가 이미지" />
            </div>
            <div class="modal__footer">
                <button type="button" class="btn btn-primary btn--liquid-glass save-btn" id="modalSaveBtn">저장</button>
                <button type="button" class="btn btn-danger  btn--liquid-glass"        id="modalCloseBtn2">닫기</button>
            </div>
        </form>
    </div>
</div>

<!-- 책 목록 전용 모달 -->
<div id="authorBooksModal" aria-hidden="true" role="dialog" style="display:none;">
    <div class="modal__dialog">
        <div class="modal__header">
            <h3 id="booksModalTitle">작가 책 목록</h3>
            <button type="button" class="modal__close" id="btnCloseBooksModal" aria-label="닫기">×</button>
        </div>
        <div class="modal__content">
            <ul id="booksList"></ul>
        </div>
    </div>
</div>

<script src="${ctx}/js/admin/admin_authors.js"></script>
<script src="${ctx}/js/admin/admin_page_header.js" defer></script>
</body>
</html>
