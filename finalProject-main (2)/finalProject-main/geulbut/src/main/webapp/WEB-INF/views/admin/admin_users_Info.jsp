<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>관리자 - 회원조회</title>
    <link rel="stylesheet" href="${ctx}/css/00_common.css" />
    <link rel="stylesheet" href="${ctx}/css/header.css" />
    <link rel="stylesheet" href="${ctx}/css/admin/admin_base.css" />
    <link rel="stylesheet" href="${ctx}/css/admin/admin_users_Info.css" />
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="/js/admin/admin_users_Info.js"></script>
    <script src="/js/theme.js"></script>
</head>
<body class="bg-main text-main admin-users has-bg">
<jsp:include page="/common/admin_page_header.jsp"></jsp:include>

<div class="page">
    <h1 class="mt-4 mb-4">회원 관리</h1>

    <!-- 검색창 통합 -->
    <div class="search-wrapper admin-search-form">
        <form method="get" action="/admin/users-info" class="search-form">
            <input id="q" name="keyword" type="text" placeholder="회원ID, 이름, 이메일 검색" value="${keyword}"/>
            <button type="submit" class="btn-search">검색</button>
            <button type="button" id="toggleAdvancedSearch" class="btn btn-light btn--liquid-glass btn-adv">
                조건검색 ▼
            </button>
        </form>

        <div id="advancedSearch" class="advanced-search">
            <label for="startDate">가입일:</label>
            <input type="date" id="startDate" name="startDate" value="${startDate}">
            <span>~</span>
            <input type="date" id="endDate" name="endDate" value="${endDate}">

            <label for="roleFilter">권한:</label>
            <select id="roleFilter" name="roleFilter">
                <option value="">전체</option>
                <option value="USER" ${roleFilter == 'USER' ? 'selected' : ''}>USER</option>
                <option value="ADMIN" ${roleFilter == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                <option value="MANAGER" ${roleFilter == 'MANAGER' ? 'selected' : ''}>MANAGER</option>
            </select>

            <label for="statusFilter">계정 상태:</label>
            <select id="statusFilter" name="statusFilter">
                <option value="">전체</option>
                <option value="ACTIVE" ${statusFilter == 'ACTIVE' ? 'selected' : ''}>활성</option>
                <option value="INACTIVE" ${statusFilter == 'INACTIVE' ? 'selected' : ''}>비활성</option>
                <option value="DELETED" ${statusFilter == 'DELETED' ? 'selected' : ''}>삭제</option>
                <option value="SUSPENDED" ${statusFilter == 'SUSPENDED' ? 'selected' : ''}>정지</option>
            </select>
        </div>
    </div>

    <!-- 통계 -->
    <div class="stats mt-3">
        <p>총 회원수: <span id="totalUsers">...</span></p>
        <p>오늘 가입자수: <span id="todayNewUsers">...</span></p>
    </div>

    <!-- 회원 테이블 -->
    <div class="table-scroll">
        <table class="admin-table admin-users-table">
            <colgroup>
                <col class="col-id">
                <col class="col-name">
                <col class="col-email">
                <col class="col-phone">
                <col class="col-address">
                <col class="col-role">
                <col class="col-joindate">
                <col class="col-status">
                <col class="col-point">
                <col class="col-grade">
                <col class="col-actions">
            </colgroup>
            <thead>
            <tr>
                <th>회원ID</th>
                <th>이름</th>
                <th>이메일</th>
                <th>전화번호</th>
                <th>기본주소</th>
                <th>권한</th>
                <th>가입일</th>
                <th>계정상태</th>
                <th>포인트</th>
                <th>등급</th>
                <th>액션</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="user" items="${usersPage.content}">
                <tr class="data-row">
                    <td>${user.userId}</td>
                    <td>${user.name}</td>
                    <td>${user.email != null ? user.email : '-'}</td>
                    <td>${user.phone != null ? user.phone : '-'}</td>
                    <td>${user.address != null ? user.address : '-'}</td>
                    <td>
                        <select class="role-select" data-userid="${user.userId}">
                            <option value="USER" ${user.role == 'USER' ? 'selected' : ''}>USER</option>
                            <option value="ADMIN" ${user.role == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                            <option value="MANAGER" ${user.role == 'MANAGER' ? 'selected' : ''}>MANAGER</option>
                        </select>
                    </td>
                    <td>${user.joinDate}</td>
                    <td>
                        <select class="status-select" data-userid="${user.userId}">
                            <option value="ACTIVE" ${user.status == 'ACTIVE' ? 'selected' : ''}>활성</option>
                            <option value="INACTIVE" ${user.status == 'INACTIVE' ? 'selected' : ''}>비활성</option>
                            <option value="DELETED" ${user.status == 'DELETED' ? 'selected' : ''}>삭제</option>
                            <option value="SUSPENDED" ${user.status == 'SUSPENDED' ? 'selected' : ''}>정지</option>
                        </select>
                    </td>
                    <td>
                        <input type="number" class="point-input" value="${user.point != null ? user.point : 0}" min="0" style="width:80px;"/>
                    </td>
                    <td>
                        <select class="grade-select">
                            <option value="BRONZE" ${user.grade == 'BRONZE' ? 'selected' : ''}>BRONZE</option>
                            <option value="SILVER" ${user.grade == 'SILVER' ? 'selected' : ''}>SILVER</option>
                            <option value="GOLD" ${user.grade == 'GOLD' ? 'selected' : ''}>GOLD</option>
                        </select>
                    </td>
                    <td>
                        <button class="btn btn-secondary btn--liquid-glass save-btn" data-userid="${user.userId}">저장</button>
                        <button class="btn btn-danger btn--liquid-glass delete-btn" data-userid="${user.userId}">삭제</button>
                    </td>
                </tr>

                <tr class="detail-row" style="display:none;">
                    <td colspan="11">
                        <div class="detail-content">
                            <p><strong>회원ID:</strong> ${user.userId}</p>
                            <p><strong>이름:</strong> ${user.name}</p>
                            <p><strong>이메일:</strong> ${user.email != null ? user.email : '-'}</p>
                            <p><strong>전화번호:</strong> ${user.phone != null ? user.phone : '-'}</p>
                            <p><strong>주소:</strong> ${user.address != null ? user.address : '-'}</p>
                            <p><strong>권한:</strong> ${user.role}</p>
                            <p><strong>가입일:</strong> ${user.joinDate}</p>
                            <p><strong>계정상태:</strong> ${user.status}</p>
                            <p><strong>포인트:</strong> <input type="number" class="point-input" value="${user.point != null ? user.point : 0}" min="0" style="width:80px;"/></p>
                            <p><strong>등급:</strong>
                                <select class="grade-select">
                                    <option value="BRONZE" ${user.grade == 'BRONZE' ? 'selected' : ''}>BRONZE</option>
                                    <option value="SILVER" ${user.grade == 'SILVER' ? 'selected' : ''}>SILVER</option>
                                    <option value="GOLD" ${user.grade == 'GOLD' ? 'selected' : ''}>GOLD</option>
                                </select>
                            </p>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- 페이지네이션 -->
    <c:if test="${usersPage.totalPages > 0}">
        <div class="btn-toolbar pagination-toolbar" role="toolbar" aria-label="페이지네이션">
            <div class="btn-group" role="group" aria-label="페이지">
                <!-- 이전 («) -->
                <c:choose>
                    <c:when test="${usersPage.first}">
                        <a class="btn btn-secondary btn-nav" aria-label="이전" aria-disabled="true">&laquo;</a>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-secondary btn-nav"
                           href="?keyword=${keyword}&startDate=${startDate}&endDate=${endDate}&roleFilter=${roleFilter}&statusFilter=${statusFilter}&page=${usersPage.number - 1}&size=${usersPage.size}"
                           aria-label="이전">&laquo;</a>
                    </c:otherwise>
                </c:choose>

                <!-- 숫자들 -->
                <c:forEach begin="0" end="${usersPage.totalPages - 1}" var="i">
                    <a class="btn btn-secondary ${i == usersPage.number ? 'active' : ''}"
                       href="?keyword=${keyword}&startDate=${startDate}&endDate=${endDate}&roleFilter=${roleFilter}&statusFilter=${statusFilter}&page=${i}&size=${usersPage.size}"
                        ${i == usersPage.number ? 'aria-current="page"' : ''}>${i + 1}</a>
                </c:forEach>

                <!-- 다음 (») -->
                <c:choose>
                    <c:when test="${usersPage.last}">
                        <a class="btn btn-secondary btn-nav" aria-label="다음" aria-disabled="true">&raquo;</a>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-secondary btn-nav"
                           href="?keyword=${keyword}&startDate=${startDate}&endDate=${endDate}&roleFilter=${roleFilter}&statusFilter=${statusFilter}&page=${usersPage.number + 1}&size=${usersPage.size}"
                           aria-label="다음">&raquo;</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:if>
</div>

<p class="ht-footnote">© Geulbut Admin Users Info</p>
<script src="/js/admin/bs_quartz_actions.js"></script>
</body>
</html>
