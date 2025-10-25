<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- 관리자 전용 상단바 -->
<header class="ad-topbar" role="banner">
    <div class="ad-container">
        <div class="ad-brand">
            <img src="${ctx}/images/logo.png" alt="글벗" class="ad-logo" />
            <strong class="ad-title">Admin</strong>
        </div>

        <nav class="ad-nav" aria-label="Admin">
            <a class="ad-link" href="${ctx}/">Home</a>

            <!-- 드롭다운 트리거 -->
            <div class="ad-dropdown">
                <a class="ad-link ad-link--dropdown" href="${ctx}/admin/users-info"
                   aria-haspopup="true" aria-expanded="false">Member Management</a>

                <!-- 드롭다운 메뉴 (호버 시 표시) -->
                <ul class="ad-menu" role="menu" aria-label="Admin Menu">
                    <li role="none"><a role="menuitem" href="${ctx}/admin/books"       class="ad-menu__item">Book Management</a></li>
                    <li role="none"><a role="menuitem" href="${ctx}/admin/authors"     class="ad-menu__item">Author Management</a></li>
                    <li role="none"><a role="menuitem" href="${ctx}/admin/publishers"  class="ad-menu__item">Publisher Management</a></li>
                    <li role="none"><a role="menuitem" href="${ctx}/admin/categories"  class="ad-menu__item">Category Management</a></li>
                    <li role="none"><a role="menuitem" href="${ctx}/admin/hashtags"    class="ad-menu__item">Hashtag Management</a></li>
                    <li role="none"><a role="menuitem" href="${ctx}/admin/users-info"  class="ad-menu__item">Member Management</a></li>
                    <li role="none"><a role="menuitem" href="${ctx}/admin/orders"      class="ad-menu__item">Delivery Management</a></li>
                    <li role="none"><a role="menuitem" href="${ctx}/admin/events/new"  class="ad-menu__item">Event registration</a></li>
                    <li role="none"><a role="menuitem" href="${ctx}/admin/events"      class="ad-menu__item">Edit event</a></li>
                </ul>
            </div>
        </nav>
    </div>
</header>
