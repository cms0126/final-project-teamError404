<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <title>관리자 - 출판사 관리</title>
    <script>window.ctx = "${ctx}";</script>
    <!-- 공통/헤더 + 출판사 전용 CSS -->
    <link rel="stylesheet" href="${ctx}/css/00_common.css" />
    <link rel="stylesheet" href="${ctx}/css/header.css" />
    <link rel="stylesheet" href="${ctx}/css/admin/admin_base.css" />
    <link rel="stylesheet" href="${ctx}/css/admin/admin_publishers.css" />

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>

<body class="bg-main text-main admin-publishers has-bg">
<jsp:include page="/common/admin_page_header.jsp" />

<div class="page">
    <h1 class="mt-4 mb-4">출판사 관리</h1>

    <!-- 검색 -->
    <div class="search-wrapper">
        <form id="publisherSearchForm" method="get" action="${ctx}/admin/publishers" class="search-form">
            <input type="text" name="keyword" id="keyword" value="${param.keyword}" placeholder="출판사 ID/이름 검색" />
            <button type="submit" class="btn-search">검색</button>
        </form>
    </div>

    <!-- 상단 툴바 -->
    <div class="toolbar">
         <button type="button" class="btn btn-light btn--liquid-glass" id="btnAddPublisher">출판사 등록</button>
    </div>

    <!-- 출판사 목록 -->
    <div class="table-scroll">
        <table class="admin-table admin-publishers-table" id="publishersTable">
            <colgroup>
                <col class="col-id" />
                <col class="col-name" />
                <col class="col-desc" />
                <col class="col-created" />
                <col class="col-actions" />
            </colgroup>
            <thead>
            <tr>
                <th>ID</th>
                <th>이름</th>
                <th>설명</th>
                <th>생성일</th>
                <th>작업</th>
            </tr>
            </thead>
            <tbody id="publishersTableBody">
            <c:forEach var="publisher" items="${publishersPage.content}">
                <tr class="data-row"
                    data-id="${publisher.publisherId}"
                    data-name="${publisher.name}"
                    data-description="${publisher.description}">
                    <td>${publisher.publisherId}</td>
                    <td class="t-left publisher-name">${publisher.name}</td>
                    <td class="t-left publisher-description">${publisher.description}</td>
                    <td class="created-at-cell">${publisher.createdAtFormatted}</td>
                    <td>
                        <button type="button" class="btn btn-primary btn--liquid-glass btnEdit">수정</button>
                        <button type="button" class="btn btn-danger  btn--liquid-glass btnDelete">삭제</button>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>


    <!-- 페이지네이션 -->
    <c:if test="${publishersPage.totalPages > 0}">
        <div class="btn-toolbar pagination-toolbar" role="toolbar" aria-label="페이지네이션">
            <div class="btn-group" role="group" aria-label="페이지">
                <!-- 이전 « -->
                <c:choose>
                    <c:when test="${publishersPage.first}">
                        <a class="btn btn-secondary btn-nav" aria-disabled="true" aria-label="이전">&laquo;</a>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-secondary btn-nav"
                           href="?page=${publishersPage.number - 1}&keyword=${param.keyword}"
                           aria-label="이전">&laquo;</a>
                    </c:otherwise>
                </c:choose>

                <!-- 숫자 -->
                <c:forEach begin="0" end="${publishersPage.totalPages - 1}" var="i">
                    <a class="btn btn-secondary ${i == publishersPage.number ? 'active' : ''}"
                       href="?page=${i}&keyword=${param.keyword}"
                        ${i == publishersPage.number ? 'aria-current="page"' : ''}>
                            ${i + 1}
                    </a>
                </c:forEach>

                <!-- 다음 » -->
                <c:choose>
                    <c:when test="${publishersPage.last}">
                        <a class="btn btn-secondary btn-nav" aria-disabled="true" aria-label="다음">&raquo;</a>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-secondary btn-nav"
                           href="?page=${publishersPage.number + 1}&keyword=${param.keyword}"
                           aria-label="다음">&raquo;</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:if>
</div>

<p class="ht-footnote">© Geulbut Admin Publishers List</p>

<!-- 모달: 출판사 등록/수정 -->
<div id="publisherModal" aria-hidden="true" role="dialog" aria-modal="true" style="display:none;">
    <div class="modal__dialog" role="document">
        <div class="modal__header">
            <h3 id="modalTitle">출판사 등록</h3>
            <button type="button" class="modal__close btn--liquid is-circle" id="btnCloseModal" aria-label="닫기">×</button>
        </div>

        <form id="publisherForm" class="modal__form">
            <input type="hidden" id="modalPublisherId" />

            <label>출판사 이름
                <input type="text" id="modalPublisherName" placeholder="출판사명을 입력하세요" required />
            </label>

            <label style="grid-column:1 / -1;">설명
                <textarea id="modalPublisherDescription" rows="4" placeholder="간단한 설명을 입력하세요"></textarea>
            </label>

            <div class="modal__footer">
                <button type="submit" class="btn btn-primary btn--liquid-glass save-btn" id="modalSaveBtn">저장</button>
                <button type="button" class="btn btn-danger  btn--liquid-glass" id="btnCancel">닫기</button>
            </div>
        </form>
    </div>
</div>

<!-- 출판사 책 목록 모달 (클래스, 스타일, role 모두 제거) -->
<div id="publisherBooksModal" style="display:none;">
    <div>
        <!-- 모달 헤더 -->
        <div>
            <h3 id="booksModalTitle">출판사 - 등록된 책 목록</h3>
            <button type="button" id="btnCloseBooksModal">×</button>
        </div>

        <!-- 모달 본문 -->
        <div>
            <table id="publisherBooksTable">
                <colgroup>
                    <col />
                    <col />
                    <col />
                </colgroup>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>책 제목</th>
                    <th>저자</th>
                </tr>
                </thead>
                <tbody>
                <!-- JS로 데이터 삽입 -->
                </tbody>
            </table>
        </div>
    </div>
</div>


<!-- js -->
<script src="${ctx}/js/admin/admin_publishers.js?v=1"></script>
<script src="${ctx}/js/admin/admin_page_header.js" defer></script>
</body>
</html>
