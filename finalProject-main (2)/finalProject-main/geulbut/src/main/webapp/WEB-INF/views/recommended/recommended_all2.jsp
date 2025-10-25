<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 25. 9. 24.
  Time: 오후 2:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="/css/home.css">
</head>
<body>
<!-- 추천 이벤트 컨텐츠 -->
<div class="tab-content" id="event-content">
    <div class="event-grid">
        <!-- 이벤트 카드 1 -->
        <div class="event-card">
            <div class="event-image">
                <img src="https://via.placeholder.com/100x100/667eea/ffffff?text=Event1" alt="감영하 작가 싸인회">
            </div>
            <div class="event-details">
                <h3 class="event-title">감영하 작가 싸인회</h3>
                <div class="event-info">
                    <div class="event-date">📅 2024년 9월 21일</div>
                    <div class="event-location">📍 고복문고 광화문점</div>
                    <div class="event-time">🕐 오후 2시</div>
                </div>
            </div>
        </div>

        <!-- 이벤트 카드 2 -->
        <div class="event-card">
            <div class="event-image">
                <img src="https://via.placeholder.com/100x100/764ba2/ffffff?text=Event2" alt="독서모임 '책과 함께'">
            </div>
            <div class="event-details">
                <h3 class="event-title">독서모임 '책과 함께'</h3>
                <div class="event-info">
                    <div class="event-date">📅 매주 토요일</div>
                    <div class="event-location">📍 양라단 서점 홍대점</div>
                    <div class="event-time">🕐 오후 7시</div>
                </div>
            </div>
        </div>

        <!-- 이벤트 카드 3 -->
        <div class="event-card">
            <div class="event-image">
                <img src="https://via.placeholder.com/100x100/f093fb/ffffff?text=Event3" alt="신간 출간기념 북토크">
            </div>
            <div class="event-details">
                <h3 class="event-title">신간 출간기념 북토크</h3>
                <div class="event-info">
                    <div class="event-date">📅 2024년 9월 25일</div>
                    <div class="event-location">📍 온라인 라이브</div>
                    <div class="event-time">🕐 오후 8시</div>
                </div>
            </div>
        </div>

        <!-- 이벤트 카드 4 -->
        <div class="event-card">
            <div class="event-image">
                <img src="https://via.placeholder.com/100x100/4facfe/ffffff?text=Event4" alt="출리소설 토론회">
            </div>
            <div class="event-details">
                <h3 class="event-title">출리소설 토론회</h3>
                <div class="event-info">
                    <div class="event-date">📅 2024년 9월 30일</div>
                    <div class="event-location">📍 YES24 강남점</div>
                    <div class="event-time">🕐 오후 3시</div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
