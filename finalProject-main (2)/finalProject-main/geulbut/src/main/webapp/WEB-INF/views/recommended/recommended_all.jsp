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
    <link rel="stylesheet" href="/css/00_common.css">
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/footer.css">
    <link rel="stylesheet" href="/css/recommended/recommended.css">

</head>
<body>
<jsp:include page="/common/header.jsp"></jsp:include>
<div class="container">
    <!-- 통합 이벤트 섹션 -->
    <div class="event-container">
        <!-- 추천 이벤트 -->
        <div class="section-header">
            <div class="icon">📅</div>
            <span>추천 이벤트</span>
        </div>

        <div class="slider-container">
            <button class="slider-btn prev" onclick="slideLeft('events-slider')">‹</button>
            <div class="events-grid" id="events-slider">
                <c:forEach var="data" items="${eventcontentsA}">
                    <div class="event-card">
                        <div class="event-image">
                            <div class="event-badge">HOT</div>
                            📷
                        </div>
                        <div class="event-details">
                            <div class="event-title"><c:out value="${data.title}"/></div>
                            <div class="event-date"><c:out value="${data.days}"/></div>
                            <div class="event-location"><c:out value="${data.point}"/></div>
                            <div class="event-time"><c:out value="${data.timeInfo}"/></div>
                        </div>
                    </div>
                </c:forEach>


            </div>
            <button class="slider-btn next" onclick="slideRight('events-slider')">›</button>
        </div>

        <!-- 섹션 구분선 -->
        <div class="section-divider"></div>

        <!-- 이벤트 굿즈 -->
        <div class="section-header">
            <div class="icon">🎁</div>
            <span>이벤트 굿즈</span>
        </div>

        <div class="slider-container">
            <button class="slider-btn prev" onclick="slideLeft('goods-slider')">‹</button>
            <div class="goods-grid" id="goods-slider">
                <c:forEach var="data" items="${eventcontentsB}">
                    <div class="goods-card">
                        <div class="event-image">
                            <div class="goods-badge badge-limited">한정</div>
                            📷
                        </div>
                        <div class="goods-info">
                            <div class="goods-title"><c:out value="${data.title}"/></div>
                            <div class="goods-period"><c:out value="${data.days}"/></div>
                            <div class="goods-location"><c:out value="${data.press}"/></div>
                            <div class="goods-price">
                                <span class="price-amount"><c:out value="${data.price}"/></span>
                                <span class="discount-rate"><c:out value="${data.discount}"/>% 할인</span>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <button class="slider-btn next" onclick="slideRight('goods-slider')">›</button>
        </div>

        <script>
            function slideLeft(sliderId) {
                const slider = document.getElementById(sliderId);
                const cardWidth = 300; // 카드 너비 + 간격
                slider.scrollBy({left: -cardWidth, behavior: 'smooth'});
            }

            function slideRight(sliderId) {
                const slider = document.getElementById(sliderId);
                const cardWidth = 300; // 카드 너비 + 간격
                slider.scrollBy({left: cardWidth, behavior: 'smooth'});
            }

            // 스크롤 위치에 따른 버튼 상태 업데이트
            function updateButtons() {
                const sliders = ['events-slider', 'goods-slider'];

                sliders.forEach(sliderId => {
                    const slider = document.getElementById(sliderId);
                    const container = slider.parentElement;
                    const prevBtn = container.querySelector('.prev');
                    const nextBtn = container.querySelector('.next');

                    const isAtStart = slider.scrollLeft === 0;
                    const isAtEnd = slider.scrollLeft >= slider.scrollWidth - slider.clientWidth;

                    prevBtn.disabled = isAtStart;
                    nextBtn.disabled = isAtEnd;
                });
            }

            // 스크롤 이벤트 리스너 추가
            document.addEventListener('DOMContentLoaded', function () {
                const sliders = document.querySelectorAll('.events-grid, .goods-grid');
                sliders.forEach(slider => {
                    slider.addEventListener('scroll', updateButtons);
                });

                // 초기 버튼 상태 설정
                updateButtons();
            });
        </script>
    </div>
    <jsp:include page="/common/footer.jsp"></jsp:include>
</body>
</html>
