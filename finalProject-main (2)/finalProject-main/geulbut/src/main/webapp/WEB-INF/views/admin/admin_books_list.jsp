<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <script>window.ctx = "${ctx}";</script>
    <title>관리자 - 도서 관리</title>

    <link rel="stylesheet" href="${ctx}/css/00_common.css"/>
    <link rel="stylesheet" href="${ctx}/css/header.css"/>
    <link rel="stylesheet" href="${ctx}/css/admin/admin_base.css" />
    <link rel="stylesheet" href="${ctx}/css/admin/admin_books.css" />

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>

<body class="bg-main text-main admin-books has-bg">
<jsp:include page="/common/admin_page_header.jsp" />

<div class="page">
    <h1 class="mt-4 mb-4">도서 관리</h1>

    <!-- 검색 -->
    <div class="search-wrapper">
        <form id="bookSearchForm" method="get" action="${ctx}/admin/books" class="search-form">
            <input type="text" name="keyword" id="keyword" value="${param.keyword}" placeholder="제목, ISBN 검색"/>
            <button type="submit" class="btn-search">검색</button>
        </form>
    </div>

    <!-- 상단 툴바 -->
    <div class="toolbar">
        <button type="button" class="btn btn-light btn--liquid-glass" id="btnAddBook">도서 등록</button>
    </div>

    <!-- 도서 목록 테이블 -->
    <div class="table-scroll">
        <table class="admin-table admin-books-table" id="booksTable" data-ctx="${ctx}">
            <colgroup>
                <col class="col-id"/>
                <col class="col-title"/>
                <col class="col-img"/>
                <col class="col-isbn"/>
                <col class="col-author"/>
                <col class="col-publisher"/>
                <col class="col-category"/>
                <col class="col-price"/>
                <col class="col-discount"/>
                <col class="col-stock"/>
                <col class="col-order"/>
                <col class="col-wish"/>
                <col class="col-rating"/>
                <col class="col-review"/>
                <col class="col-created"/>
                <col class="col-actions"/>
            </colgroup>
            <thead>
            <tr>
                <th>책 ID</th>
                <th>제목</th>
                <th>이미지</th>
                <th class="hide-md">ISBN</th>
                <th>저자</th>
                <th class="hide-lg">출판사</th>
                <th class="hide-lg">카테고리</th>
                <th>가격</th>
                <th class="hide-lg">할인가</th>
                <th>재고</th>
                <th>주문 수</th>
                <th>찜수</th>
                <th>평점</th>
                <th>리뷰</th>
                <th class="hide-lg">생성일</th>
                <th>작업</th>
            </tr>
            </thead>
            <tbody id="booksTableBody">
            <c:set var="matchCount" value="0"/>
            <c:forEach var="book" items="${booksPage.content}">
                <c:set var="matchCount" value="${matchCount + 1}"/>
                <tr class="data-row" data-id="${book.bookId}" data-order="${book.orderCount}" data-wish="${book.wishCount}">
                    <td>${book.bookId}</td>
                    <td class="t-left">
                        <div class="title-ellipsis" title="${book.title}">${book.title}</div>
                    </td>
                    <td>
                        <img src="${empty book.imgUrl ? '/images/thumb_ing.gif' : book.imgUrl}"
                             alt="${fn:escapeXml(book.title)}" class="book-thumb"/>
                    </td>
                    <td class="hide-md"><span class="isbn-mono">${book.isbn}</span></td>
                    <td>${book.authorName}</td>
                    <td class="hide-lg">${book.publisherName}</td>
                    <td class="hide-lg">${book.categoryName}</td>
                    <td class="t-center"><c:out value="${book.price}"/></td>
                    <td class="t-center hide-lg"><c:out value="${book.discountedPrice}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${book.stock gt 0}">
                                <span class="stock-chip ok">${book.stock}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="stock-chip out">품절</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="t-right">${book.orderCount}</td>
                    <td class="t-right">${book.wishCount}</td>
                    <td class="col-rating t-right">
                        <c:out value="${book.rating != null ? book.rating : 0.0}"/>
                    </td>
                    <td class="col-review t-right">
                        <c:out value="${book.reviewCount != null ? book.reviewCount : 0}"/>
                    </td>
                    <td class="hide-lg" data-created="${book.createdAtFormatted}">
                            ${book.createdAtFormatted}
                    </td>
                    <td>
                        <button type="button" class="btn btn-secondary btn--liquid-glass btnView">상세보기</button>
                        <button type="button" class="btn btn-primary btn--liquid-glass btnEdit">수정</button>
                        <button type="button" class="btn btn-danger btn--liquid-glass btnDelete">삭제</button>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${matchCount == 0}">
                <tr>
                    <td colspan="16" class="t-center text-light">검색 결과가 없습니다.</td>
                </tr>
            </c:if>
            </tbody>

        </table>
    </div>

    <!-- 페이징 -->
    <div class="btn-toolbar pagination-toolbar" role="toolbar" aria-label="페이지네이션">
        <div class="btn-group" role="group" aria-label="페이지">
            <c:choose>
                <c:when test="${booksPage.first}">
                    <a class="btn btn-secondary btn-nav" aria-label="이전" aria-disabled="true">&laquo;</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-secondary btn-nav"
                       href="?page=${booksPage.number - 1}&keyword=${param.keyword}"
                       aria-label="이전">&laquo;</a>
                </c:otherwise>
            </c:choose>
            <c:forEach var="i" begin="0" end="${booksPage.totalPages - 1}">
                <a class="btn btn-secondary ${i == booksPage.number ? 'active' : ''}"
                   href="?page=${i}&keyword=${param.keyword}"
                    ${i == booksPage.number ? 'aria-current="page"' : ''}>
                        ${i + 1}
                </a>
            </c:forEach>
            <c:choose>
                <c:when test="${booksPage.last}">
                    <a class="btn btn-secondary btn-nav" aria-label="다음" aria-disabled="true">&raquo;</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-secondary btn-nav"
                       href="?page=${booksPage.number + 1}&keyword=${param.keyword}"
                       aria-label="다음">&raquo;</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

</div>

<p class="ht-footnote">© Geulbut Admin Books List</p>

<!-- 도서 등록/수정 모달 -->
<div id="bookModal" aria-hidden="true" role="dialog" aria-modal="true" aria-labelledby="modalTitle" style="display:none;">
    <div class="modal__dialog" role="document">
        <div class="modal__header">
            <h3 id="modalTitle" class="mt-3 mb-3 ml-3">도서 등록</h3>
            <button type="button" class="modal__close btn--liquid is-circle" id="btnCloseModal" aria-label="닫기">×</button>
        </div>

        <form id="bookForm" class="modal__form">
            <input type="hidden" name="bookId" id="bookId"/>

            <div class="form-grid">
                <label>제목 <input type="text" name="title" id="title" required/></label>
                <label>ISBN <input type="text" name="isbn" id="isbn" required/></label>
                <label>가격 <input type="number" name="price" id="price" min="0" step="1" required/></label>
                <label>할인가 <input type="number" name="discountedPrice" id="discountedPrice" min="0" step="1"/></label>
                <label>재고 <input type="number" name="stock" id="stock" min="0" step="1" required/></label>
                <label>저자 <select id="authorId" name="authorId">
                    <option value="">선택</option>
                </select></label>
                <label>출판사 <select id="publisherId" name="publisherId">
                    <option value="">선택</option>
                </select></label>
                <label>카테고리 <select id="categoryId" name="categoryId">
                    <option value="">선택</option>
                </select></label>
                <label>이미지 URL <input type="text" name="imgUrl" id="imgUrl"/></label>
                <div id="imgPreviewWrapper">
                    <img id="imgPreview" src="" alt="이미지 미리보기"
                         style="max-width:200px; max-height:300px; display:none;"/>
                </div>
                <label>주문 수 <input type="number" name="orderCount" id="orderCount" min="0" step="1"/></label>
                <label>찜 수 <input type="number" name="wishCount" id="wishCount" min="0" step="1"/></label>
                <label>평점
                    <input type="number" name="rating" id="rating" step="0.1" min="0" max="5" value="0.0"/>
                </label>
                <label>리뷰 수
                    <input type="number" name="reviewCount" id="reviewCount" min="0" step="1" value="0"/>
                </label>
            </div>
            <div class="modal__footer">
                <button type="submit" class="btn btn-secondary btn--liquid-glass save-btn mt-3">저장</button>
                <button type="button" class="btn btn-danger btn--liquid-glass mt-3" id="btnCancel">닫기</button>
            </div>
        </form>
    </div>
</div>

<script src="${ctx}/js/admin/admin_books_list.js"></script>
<script src="${ctx}/js/admin/admin_page_header.js" defer></script>
</body>
</html>
