<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <script>window.ctx = "${ctx}";</script>
    <title>관리자 - 해시태그 관리</title>

    <!-- 공통/헤더 + 관리자 통합 CSS -->
    <link rel="stylesheet" href="${ctx}/css/00_common.css"/>
    <link rel="stylesheet" href="${ctx}/css/header.css"/>
    <link rel="stylesheet" href="${ctx}/css/admin/admin_base.css"/>
    <link rel="stylesheet" href="${ctx}/css/admin/admin_hashtags.css"/>
</head>

<body class="bg-main text-main admin-hashtags has-bg" data-admin-skin="v2">
<jsp:include page="/common/admin_page_header.jsp" />

<div class="page">
    <h1 class="mt-4 mb-4">해시태그 관리</h1>

    <!-- 검색 -->
    <div class="search-wrapper">
        <form id="searchForm" method="get" action="${ctx}/admin/hashtags" class="search-form">
            <input type="text" id="keyword" name="keyword" placeholder="해시태그 검색..." value="${param.keyword}" />
            <button type="submit" class="btn-search">검색</button>
        </form>
    </div>

    <!-- 상단 툴바 -->
    <div class="toolbar">
        <button id="btnAddHashtag" type="button" class="btn btn-light btn--liquid-glass">해시태그 추가</button>
    </div>

    <!-- 테이블 -->
    <div class="table-scroll">
        <table class="admin-table admin-hashtags-table" id="hashtagsTable">
            <colgroup>
                <col class="col-id" />
                <col class="col-name" />
                <col class="col-created" />
                <col class="col-books" />
                <col class="col-actions" />
            </colgroup>
            <thead>
            <tr>
                <th>ID</th>
                <th>이름</th>
                <th>생성일</th>
                <th>등록 도서</th>
                <th>작업</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="h" items="${hashtagsPage.content}">
                <tr class="data-row" data-id="${h.hashtagId}">
                    <!-- ID는 클릭 제거, 그냥 표시 -->
                    <td class="hashtag-id">${h.hashtagId}</td>

                    <!-- 이름만 클릭 가능 -->
                    <td class="t-left hashtag-name">${h.name}</td>

                    <td>${h.createdAtFormatted}</td>
                    <td class="t-left">
                        <c:choose>
                            <c:when test="${not empty h.books}">
                                <ul>
                                    <c:forEach var="b" items="${h.books}">
                                        <li>${b.title} (${b.isbn})</li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                등록된 도서 없음
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="actions-cell">
                        <button type="button" class="btn btn-primary btn--liquid-glass btnEdit">수정</button>
                        <button type="button" class="btn btn-danger btn--liquid-glass btnDelete">삭제</button>
                        <button type="button" class="btn btn-secondary btn--liquid-glass btn-manage-books">도서 관리</button>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- 페이지네이션 -->
    <c:set var="hp" value="${hashtagsPage}" />
    <c:set var="isFirst" value="${not empty hp and hp.first}" />
    <c:set var="isLast"  value="${not empty hp and hp.last}" />
    <c:set var="pageNo"  value="${not empty hp and hp.number >= 0 ? hp.number : 0}" />
    <c:set var="totalPages" value="${not empty hp and hp.totalPages >= 0 ? hp.totalPages : 0}" />

    <div class="btn-toolbar pagination-toolbar" role="toolbar" aria-label="페이지네이션">
        <div class="btn-group" role="group" aria-label="페이지">
            <c:choose>
                <c:when test="${isFirst or totalPages == 0}">
                    <a class="btn btn-secondary btn-nav" aria-label="이전" aria-disabled="true">&laquo;</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-secondary btn-nav"
                       href="?page=${pageNo - 1}&keyword=${param.keyword}"
                       aria-label="이전">&laquo;</a>
                </c:otherwise>
            </c:choose>

            <c:if test="${totalPages > 0}">
                <c:forEach var="i" begin="0" end="${totalPages - 1}">
                    <a class="btn btn-secondary ${i == pageNo ? 'active' : ''}"
                       href="?page=${i}&keyword=${param.keyword}"
                        ${i == pageNo ? 'aria-current="page"' : ''}>${i + 1}</a>
                </c:forEach>
            </c:if>

            <c:choose>
                <c:when test="${isLast or totalPages == 0}">
                    <a class="btn btn-secondary btn-nav" aria-label="다음" aria-disabled="true">&raquo;</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-secondary btn-nav"
                       href="?page=${pageNo + 1}&keyword=${param.keyword}"
                       aria-label="다음">&raquo;</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<p class="ht-footnote">© Geulbut Admin Hashtags List</p>

<!-- 해시태그 등록/수정 모달 -->
<div class="modal" id="hashtagModal" aria-hidden="true" role="dialog" aria-modal="true">
    <div class="modal-content" role="document">
        <h2 id="modalTitle" class="modal-title">해시태그 등록</h2>
        <label>
            <span class="field-label t-left">이름</span>
            <input type="text" id="hashtagName" placeholder="해시태그 이름" />
        </label>
        <div class="modal-actions t-right">
            <button id="modalSaveBtn" type="button" class="btn btn-primary btn--liquid-glass save-btn">저장</button>
            <button id="modalCloseBtn" type="button" class="btn btn-danger btn--liquid-glass delete-btn">닫기</button>
        </div>
    </div>
</div>

<!-- 책 목록 모달 -->
<div class="modal" id="booksModal" aria-hidden="true" role="dialog" aria-modal="true">
    <div class="modal-content" role="document">
        <h2 id="booksModalTitle" class="modal-title">해시태그 등록 도서</h2>

        <!-- 검색창 -->
        <div class="search-wrapper">
            <form id="bookSearchForm" class="search-form">
                <input type="text" id="bookKeyword" name="bookKeyword" placeholder="도서 검색..." />
                <button type="submit" class="btn-search">검색</button>
            </form>
        </div>

        <!-- 검색 결과 리스트 -->
        <div id="booksList" class="modal__scroll"></div>

        <!-- 모달 페이지네이션 -->
        <div id="booksPager" class="pagination modal-pagination"></div>

        <div class="modal__footer" id="booksModalFooter">
            <button id="booksModalClose" type="button" class="btn btn-danger btn--liquid-glass delete-btn">닫기</button>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="${ctx}/js/admin/admin_hashtags.js"></script>
<script src="${ctx}/js/admin/admin_page_header.js" defer></script>
</body>
</html>
