<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- CSS -->
<link rel="stylesheet" href="<c:url value='/css/admin/admin-header.css'/>">
<link rel="stylesheet" href="<c:url value='/css/api/dustApi.css'/>">

<!-- 추가 스타일 -->
<style>
    .site-header__nav--left {
        order: 1 !important;
    }

    .site-header__logo {
        order: 2 !important;
    }

    .site-header__nav--right {
        order: 3 !important;
    }

    .site-header__greeting {
        order: 4 !important;
        flex: 0 0 100% !important;
        display: flex !important;
        justify-content: center !important;
        margin: -8PX 0 4px 0 !important;
    }

    .site-header__search {
        order: 5 !important;
        margin-top: 2px !important;
    }

    .site-header__nav--bottom {
        order: 6 !important;
    }

    .greeting-message {
        font-size: 0.8rem !important;
        padding: 3px 12px !important;
        background: rgba(64, 71, 78, 0.07) !important;
        border-radius: 12px !important;
        border: 1px solid rgba(125, 133, 140, 0.17) !important;
        color: #5d666c !important;
    }
</style>

<header class="site-header">
    <div class="container site-header__inner">
        <!-- 좌측 네비(비움) -->
        <nav class="site-header__nav site-header__nav--left" aria-label="Primary"></nav>

        <!-- 가운데 로고 -->
        <h1 class="site-header__logo">
            <a href="${ctx}/" title="홈으로">
                <img src="${ctx}/images/logo.png" alt="글벗">
            </a>
        </h1>

        <!-- 우측 계정 메뉴 -->
        <nav class="site-header__nav site-header__nav--right" aria-label="Account">
            <ul class="site-header__menu site-header__menu--mobile">

                <!-- 로그인 상태 -->
                <sec:authorize access="isAuthenticated()">
                    <c:set var="userName" value=""/>

                    <!-- 비 OAuth2 -->
                    <sec:authorize access="!(principal instanceof T(org.springframework.security.oauth2.core.user.OAuth2User))">
                        <sec:authentication property="principal.username" var="u1"/>
                        <c:if test="${empty u1}">
                            <sec:authentication property="name" var="u1"/>
                        </c:if>
                        <c:if test="${not empty u1}">
                            <c:set var="userName" value="${u1}"/>
                        </c:if>
                    </sec:authorize>

                    <!-- OAuth2 -->
                    <sec:authorize access="principal instanceof T(org.springframework.security.oauth2.core.user.OAuth2User)">
                        <c:if test="${empty userName}">
                            <sec:authentication property="principal.attributes['name']" var="tmp"/>
                            <c:if test="${not empty tmp}"><c:set var="userName" value="${tmp}"/></c:if>
                        </c:if>
                        <c:if test="${empty userName}">
                            <sec:authentication property="principal.attributes['nickname']" var="tmp"/>
                            <c:if test="${not empty tmp}"><c:set var="userName" value="${tmp}"/></c:if>
                        </c:if>
                        <c:if test="${empty userName}">
                            <sec:authentication property="principal.attributes['email']" var="tmp"/>
                            <c:if test="${not empty tmp}"><c:set var="userName" value="${tmp}"/></c:if>
                        </c:if>

                        <c:if test="${empty userName}">
                            <sec:authentication property="principal.attributes['given_name']" var="gn"/>
                            <sec:authentication property="principal.attributes['family_name']" var="fn"/>
                            <c:if test="${not empty gn || not empty fn}">
                                <c:set var="userName" value="${fn} ${gn}"/>
                            </c:if>
                        </c:if>

                        <c:if test="${empty userName}">
                            <sec:authentication property="principal.attributes['response']" var="nv"/>
                            <c:if test="${not empty nv}">
                                <c:set var="userName" value="${nv.name}"/>
                                <c:if test="${empty userName}">
                                    <c:set var="userName" value="${nv.nickname}"/>
                                </c:if>
                            </c:if>
                        </c:if>

                        <c:if test="${empty userName}">
                            <sec:authentication property="principal.attributes['kakao_account']" var="ka"/>
                            <c:if test="${not empty ka && not empty ka.profile}"><c:set var="userName" value="${ka.profile.nickname}"/></c:if>
                            <c:if test="${empty userName}">
                                <sec:authentication property="principal.attributes['properties']" var="kp"/>
                                <c:if test="${not empty kp}"><c:set var="userName" value="${kp.nickname}"/></c:if>
                            </c:if>
                        </c:if>
                    </sec:authorize>

                    <c:if test="${empty userName}">
                        <sec:authentication property="name" var="u2"/>
                        <c:set var="userName" value="${u2}"/>
                    </c:if>

                    <c:if test="${not empty userName}">
                        <c:set var="displayName" value="${userName}" scope="session"/>
                    </c:if>

                    <!-- orderId 안전 추출 -->
                    <c:set var="currentOrderId"
                           value="${not empty param.orderId ? param.orderId
                                   : (not empty delivery && not empty delivery.ordersDto && not empty delivery.ordersDto.orderId
                                       ? delivery.ordersDto.orderId
                                       : sessionScope.lastOrderId)}"/>

                    <li><a href="${ctx}/notice">공지사항</a></li>
                    <li><a href="${ctx}/books">도서목록</a></li>
                    <li><a href="${ctx}/authors">작가목록</a></li>
                    <li><a href="${ctx}/mypage">마이페이지</a></li>
                    <li><a href="${ctx}/logout">로그아웃</a></li>

                </sec:authorize>

                <!-- 비로그인 -->
                <sec:authorize access="!isAuthenticated()">
                    <li><a href="${ctx}/notice">공지사항</a></li>
                    <li><a href="${ctx}/books">도서목록</a></li>
                    <li><a href="${ctx}/authors">작가목록</a></li>
                    <li><a href="${ctx}/login">로그인</a></li>
                    <li><a href="${ctx}/signup">회원가입</a></li>
                </sec:authorize>

            </ul>
        </nav>

        <!-- 모바일 햄버거 -->
        <button class="site-header__hamburger" aria-label="메뉴 열기">
            <span></span><span></span><span></span>
        </button>

        <!-- 인사말 영역 -->
        <div class="site-header__greeting">
            <sec:authorize access="isAuthenticated()">
                <span class="greeting-message">안녕하세요, ${fn:escapeXml(userName)} 님!</span>
            </sec:authorize>
        </div>

        <!-- 검색 -->
        <div class="site-header__search" role="search">
            <form action="${ctx}/search" method="get" class="search-form">
                <input type="hidden" name="type" value="all">
                <select id="type" class="search-select" disabled>
                    <option>통합검색</option>
                </select>
                <input id="q" name="keyword" type="text" placeholder="검색어를 입력하세요" value="${keyword}"/>
                <button type="submit" class="btn-search">검색</button>
            </form>
        </div>

        <!-- 날씨/미세먼지 ticker -->
        <div id="weather-dust-header">불러오는 중...</div>

        <!-- ADMIN 전용 -->
        <sec:authorize access="hasRole('ADMIN')">
            <button class="admin-toggle">관리자</button>
            <aside class="admin-panel">
                <h3>관리자 패널</h3>
                <div class="admin-group">
                    <div class="group-title">📚 책 관리</div>
                    <ul>
                        <li><a href="${ctx}/admin/books">도서 등록/수정/삭제</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/authors">작가 등록/수정/삭제</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/publishers">출판사 등록/수정/삭제</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/categories">카테고리 등록/수정/삭제</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/hashtags">해시태그 등록/수정/삭제</a></li>
                    </ul>
                </div>
                <div class="admin-group">
                    <div class="group-title">👥 회원 관리</div>
                    <ul>
                        <li><a href="${ctx}/admin/users-info">회원 조회 & 권한변경</a></li>
                        <li><a href="${ctx}/admin/orders">전체 배송 조회</a></li>
                    </ul>
                </div>
            </aside>
        </sec:authorize>
    </div>
</header>

<!-- JS 환경변수 -->
<script>
    const isLogin = ${pageContext.request.userPrincipal != null};
</script>
<script src="<c:url value='/common/js/header.js'/>" charset="UTF-8"></script>
<script src="<c:url value='/js/api/DustWeatherApi.js'/>"></script>
<script src="/common/js/search-validation.js"></script>
