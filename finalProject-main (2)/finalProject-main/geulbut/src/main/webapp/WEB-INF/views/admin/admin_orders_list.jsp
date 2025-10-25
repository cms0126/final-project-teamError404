<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>관리자 - 주문 관리</title>

    <link rel="stylesheet" href="${ctx}/css/00_common.css"/>
    <link rel="stylesheet" href="${ctx}/css/admin-header.css"/>
    <link rel="stylesheet" href="${ctx}/css/admin/admin_base.css"/>
    <link rel="stylesheet" href="${ctx}/css/admin/admin_orders.css"/>


    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>window.ctx = "${ctx}";</script>
</head>

<body class="bg-main text-main admin-orders has-bg">
<jsp:include page="/common/admin_page_header.jsp" />

<div class="page">
    <h1 class="mt-4 mb-4">주문 관리</h1>

    <!-- 검색 (공통 UI) -->
    <div class="search-wrapper">
        <form id="orderSearchForm" method="get" action="${ctx}/admin/orders" class="search-form">
            <select name="status" id="status">
                <option value="">상태: 전체</option>
                <option value="CREATED"   ${status=='CREATED'?'selected':''}>생성됨</option>
                <option value="PAID"      ${status=='PAID'?'selected':''}>결제 완료</option>
                <option value="SHIPPED"   ${status=='SHIPPED'?'selected':''}>배송중</option>
                <option value="DELIVERED" ${status=='DELIVERED'?'selected':''}>배송완료</option>
                <option value="CANCELLED" ${status=='CANCELLED'?'selected':''}>취소</option>
                <option value="PENDING"   ${status=='PENDING'?'selected':''}>대기</option>
            </select>

            <input type="text" name="userId" id="userId"
                   value="${userId != null ? userId : ''}" placeholder="사용자ID 검색"/>

            <button type="submit" class="btn-search">검색</button>
        </form>
    </div>

    <!-- 목록 테이블 (공통 UI) -->
    <div class="table-scroll">
        <table class="admin-table admin-orders-table" id="ordersTable" data-ctx="${ctx}">
            <colgroup>
                <col class="col-id"/>
                <col class="col-userid"/>
                <col class="col-username"/>
                <col class="col-total"/>
                <col class="col-status"/>
                <col class="col-paymethod"/>
                <col class="col-merchant"/>
                <col class="col-recipient"/>
                <col class="col-address"/>
                <col class="col-created"/>
                <col class="col-paid"/>
                <col class="col-delivered"/>
                <col class="col-actions"/>
            </colgroup>
            <thead>
            <tr>
                <th>주문ID</th>
                <th>사용자ID</th>
                <th>사용자 이름</th>
                <th>총액</th>
                <th>상태</th>
                <th>결제수단</th>
                <th>주문번호</th>
                <th>수령인</th>
                <th>주소</th>
                <th>주문일</th>
                <th>결제일시</th>
                <th>배송일시</th>
                <th>작업</th>
            </tr>
            </thead>
            <tbody id="ordersTableBody">
            <c:choose>
                <c:when test="${not empty ordersPage and not empty ordersPage.content}">
                    <c:forEach var="order" items="${ordersPage.content}">
                        <tr class="data-row" data-id="${order.orderId}">
                            <td>${order.orderId}</td>
                            <td>${order.userId}</td>
                            <td>${order.userName != null ? order.userName : '-'}</td>
                            <td class="t-right">${order.totalPrice != null ? order.totalPrice : '-'}</td>
                            <td>
                                <select class="status-select"
                                        data-id="${order.orderId}"
                                        data-current-status="${order.status}">
                                    <option value="PENDING"   ${order.status=='PENDING'?'selected':''}>대기</option>
                                    <option value="PAID"      ${order.status=='PAID'?'selected':''}>결제 완료</option>
                                    <option value="SHIPPED"   ${order.status=='SHIPPED'?'selected':''}>배송중</option>
                                    <option value="DELIVERED" ${order.status=='DELIVERED'?'selected':''}>배송완료</option>
                                    <option value="CANCELLED" ${order.status=='CANCELLED'?'selected':''}>취소</option>
                                </select>
                            </td>
                            <td>${order.paymentMethod != null ? order.paymentMethod : '-'}</td>
                            <td>${order.merchantUid != null ? order.merchantUid : '-'}</td>
                            <td>${order.recipient != null ? order.recipient : '-'}</td>
                            <td class="t-left">${order.address != null ? order.address : '-'}</td>
                            <td><c:out value="${order.createdAtFormatted}" /></td>
                            <td><c:out value="${order.paidAtFormatted != null ? order.paidAtFormatted : '-'}" /></td>
                            <td><c:out value="${order.deliveredAt != null ? order.deliveredAtFormattedShort : '-'}" /></td>
                            <td>
                                <button class="btn btn-primary btn--liquid-glass btn-detail" data-id="${order.orderId}">상세보기</button>                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="13" class="t-center">조회된 주문이 없습니다.</td>
                    </tr>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>

    <!-- 페이지네이션 (공통 UI와 동일) -->
    <c:if test="${not empty ordersPage and ordersPage.totalPages > 0}">
        <div class="btn-toolbar pagination-toolbar" role="toolbar" aria-label="페이지네이션">
            <div class="btn-group" role="group" aria-label="페이지">
                <!-- 이전 («) -->
                <c:choose>
                    <c:when test="${ordersPage.first}">
                        <a class="btn btn-secondary btn-nav" aria-label="이전" aria-disabled="true">&laquo;</a>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-secondary btn-nav"
                           href="?page=${ordersPage.number - 1}&size=${ordersPage.size}&status=${status}"
                           aria-label="이전">&laquo;</a>
                    </c:otherwise>
                </c:choose>

                <!-- 숫자 -->
                <c:forEach begin="0" end="${ordersPage.totalPages - 1}" var="i">
                    <a class="btn btn-secondary ${i==ordersPage.number?'active':''}"
                       href="?page=${i}&size=${ordersPage.size}&status=${status}"
                        ${i==ordersPage.number?'aria-current="page"':''}>${i+1}</a>
                </c:forEach>

                <!-- 다음 (») -->
                <c:choose>
                    <c:when test="${ordersPage.last}">
                        <a class="btn btn-secondary btn-nav" aria-label="다음" aria-disabled="true">&raquo;</a>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-secondary btn-nav"
                           href="?page=${ordersPage.number + 1}&size=${ordersPage.size}&status=${status}"
                           aria-label="다음">&raquo;</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:if>
</div>

<p class="ht-footnote">© Geulbut Admin Orders</p>

<!-- 주문 상세 모달 (공통 모달 톤 사용) -->
<div id="orderModal" aria-hidden="true" role="dialog" aria-modal="true" aria-labelledby="orderModalTitle" style="display:none;">
    <div class="modal__dialog" role="document">
        <div class="modal__header">
            <h3 id="orderModalTitle" class="mt-3 mb-3 ml-3">주문 상세</h3>
            <button type="button" class="modal__close btn--liquid is-circle" id="btnCloseOrderModal" aria-label="닫기">×</button>
        </div>
        <div class="modal__form" id="orderDetailContent" style="grid-template-columns:1fr;">
            <!-- ajax 로드 -->
        </div>
        <div class="modal__footer">
            <button type="button" class="btn btn-danger btn--liquid-glass" id="btnOrderModalClose">닫기</button>
        </div>
    </div>
</div>

<script src="${ctx}/js/admin/admin_orders.js"></script>
<script src="${ctx}/js/admin/admin_page_header.js" defer></script>
</body>
</html>
