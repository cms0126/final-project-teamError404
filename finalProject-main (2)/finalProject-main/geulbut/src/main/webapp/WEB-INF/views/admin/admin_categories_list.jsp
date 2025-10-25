<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <script>window.ctx = "${ctx}";</script>
    <title>관리자 - 카테고리 관리</title>

    <!-- 공통/헤더 + 관리자 통합 CSS -->
    <link rel="stylesheet" href="${ctx}/css/00_common.css" />
    <link rel="stylesheet" href="${ctx}/css/header.css" />
    <link rel="stylesheet" href="${ctx}/css/admin/admin_base.css" />
    <link rel="stylesheet" href="${ctx}/css/admin/admin_categories.css" />

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>

<body class="bg-main text-main admin-categories has-bg">
<jsp:include page="/common/admin_page_header.jsp" />

<div class="page">
    <h1 class="mt-4 mb-4">카테고리 관리</h1>

    <!-- 검색 (해시태그와 동일 컴포넌트) -->
    <div class="search-wrapper">
        <form id="searchForm" method="get" action="${ctx}/admin/categories" class="search-form">
            <input type="text" id="searchKeyword" name="keyword" placeholder="카테고리 이름 검색"
                   value="${param.keyword != null ? param.keyword : ''}">
            <button type="submit" class="btn-search">검색</button>
        </form>
    </div>

    <!-- 상단 툴바 -->
    <div class="toolbar">
        <button id="btnAddCategory" type="button" class="btn btn-light btn--liquid-glass">카테고리 등록</button>
    </div>
    <!-- 목록 테이블 -->
    <div class="table-scroll">
        <table class="admin-table admin-categories-table" id="categoriesTable">
            <colgroup>
                <col class="col-id" />
                <col class="col-name" />
                <col class="col-created" />
                <col class="col-actions" />
            </colgroup>
            <thead>
            <tr>
                <th>ID</th>
                <th>이름</th>
                <th>생성일</th>
                <th>작업</th>
            </tr>
            </thead>
            <tbody id="categoriesTableBody">
            <c:forEach var="category" items="${categoriesPage.content}">
                <tr data-id="${category.categoryId}">
                    <td class="category-id t-center">${category.categoryId}</td>
                    <td class="category-name t-left" title="${category.name}">${category.name}</td>
                    <td class="created-at-cell">${category.createdAtFormatted}</td>
                    <td class="actions-cell">
                        <button type="button" class="btn btn-secondary btn--liquid-glass btnEdit btn-edit">수정</button>
                        <button type="button" class="btn btn-danger btn--liquid-glass btnDelete btn-delete">삭제</button>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty categoriesPage.content}">
                <tr><td colspan="4" class="t-center">데이터가 없습니다.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>

    <!-- 페이징 (해시태그와 동일 구조/스타일) -->
        <div class="btn-toolbar pagination-toolbar" role="toolbar" aria-label="페이지네이션">
            <div class="btn-group" role="group" aria-label="페이지">

                <!-- 이전 («) -->
                <c:choose>
                    <c:when test="${categoriesPage.first}">
                        <a class="btn btn-secondary btn-nav" aria-label="이전" aria-disabled="true">&laquo;</a>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-secondary btn-nav"
                           href="?page=${categoriesPage.number - 1}&keyword=${param.keyword}"
                           aria-label="이전">&laquo;</a>
                    </c:otherwise>
                </c:choose>

                <!-- 숫자들 -->
                <c:forEach var="i" begin="0" end="${categoriesPage.totalPages > 0 ? categoriesPage.totalPages - 1 : 0}">
                    <a class="btn btn-secondary ${i == categoriesPage.number ? 'active' : ''}"
                       href="?page=${i}&keyword=${param.keyword}"
                        ${i == categoriesPage.number ? 'aria-current="page"' : ''}>
                            ${i + 1}
                    </a>
                </c:forEach>

                <!-- 다음 (») -->
                <c:choose>
                    <c:when test="${categoriesPage.last}">
                        <a class="btn btn-secondary btn-nav" aria-label="다음" aria-disabled="true">&raquo;</a>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-secondary btn-nav"
                           href="?page=${categoriesPage.number + 1}&keyword=${param.keyword}"
                           aria-label="다음">&raquo;</a>
                    </c:otherwise>
                </c:choose>

            </div>
        </div>

</div>

<p class="ht-footnote">© Geulbut Admin Categories List</p>

<!-- 등록/수정 모달 (공통 모달 톤) -->
    <div id="categoryModal" aria-hidden="true" role="dialog" aria-modal="true" style="display:none;">
        <div class="modal__dialog" role="document">
            <div class="modal__header">
                <h3 id="modalTitle" class="mt-3 mb-3 ml-3">카테고리 등록</h3>
                <button type="button" class="modal__close btn--liquid is-circle" id="modalCloseBtn" aria-label="닫기">×</button>
            </div>

            <form class="modal__form" id="categoryForm">
                <input type="hidden" id="modalCategoryId" />
                <label>카테고리 이름
                    <input type="text" id="modalCategoryName" />
                </label>
                <div class="modal__footer">
                    <button id="modalSaveBtn" type="submit" class="btn btn-secondary btn--liquid-glass save-btn">저장</button>
                    <button id="modalCancelBtn" type="button" class="btn btn-danger btn--liquid-glass">닫기</button>
                </div>
            </form>
        </div>
    </div>

<!-- 책 목록 모달 (읽기용) -->
<div id="booksModal" class="modal" aria-hidden="true" role="dialog" aria-modal="true">
    <div class="modal-content" role="document">
        <h2 id="booksModalTitle">카테고리 속 책 목록</h2>
           <!-- 스크롤 래퍼: 작을 땐 스크롤, 클 땐 넉넉한 폭으로 -->
           <div class="modal-table-scroll">
             <table class="admin-table admin-books-table" id="booksTable">
               <colgroup>
                 <col style="width: 84px;" />     <!-- ID -->
                 <col style="width: auto;" />     <!-- 제목 (가변) -->
                 <col style="width: 160px;" />    <!-- 저자 -->
                 <col style="width: 160px;" />    <!-- 출판사 -->
                 <col style="width: 110px;" />    <!-- 가격 -->
               </colgroup>
            <thead>
            <tr>
                <th>ID</th><th>제목</th><th>저자</th><th>출판사</th><th>가격</th>
            </tr>
            </thead>
            <tbody></tbody>
                      </table>
                  </div>
           <div class="modal-footer">
             <button id="booksModalCloseBtn" type="button" class="btn btn-cer-success delete-btn">닫기</button>
           </div>
    </div>
</div>

<!-- JS 분리 -->
<script src="${ctx}/js/admin/admin_categories.js?v=1"></script>
<!-- 관리자 헤더 드롭다운 스크립트 -->
<script src="${ctx}/js/admin/admin_page_header.js" defer></script>
</body>
</html>
