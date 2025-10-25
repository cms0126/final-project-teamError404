<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <title>추천 도서</title>

    <!-- 공통 스타일 -->
    <link rel="stylesheet" href="/css/00_common.css">
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/footer.css">

    <!-- 메인 페이지 스타일(수정 반영본) -->
    <link rel="stylesheet" href="/css/home.css">
    <link rel="stylesheet" href="/css/home_mood.css">

    <!-- CSRF -->
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
</head>
<body>
<jsp:include page="/common/header.jsp"/>

<section class="gb-hero" role="region" aria-label="오늘의 추천 하이라이트">
    <!-- 배경 플로팅 북 카드 -->
    <div class="gb-hero__bg" aria-hidden="true">
        <span class="gb-book b1"></span>
        <span class="gb-book b2"></span>
        <span class="gb-book b3"></span>
        <span class="gb-book b4"></span>
        <span class="gb-book b5"></span>
        <span class="gb-book b6"></span>
    </div>

    <!-- 카피/CTA -->
    <div class="gb-hero__inner">
        <p class="gb-eyebrow">오늘 뭐 읽지?</p>
        <h1 class="gb-title">취향저격 <span>큐레이션</span></h1>
        <p class="gb-lead">신간 · 화제의 책 · 이 주의 특가를 한 눈에</p>
        <div class="gb-cta">
            <a href="/books" class="gb-btn gb-btn--fill">지금 탐색하기</a>
        </div>
    </div>

    <!-- 하단 웨이브 디바이더 (배경과 자연스러운 연결) -->
    <svg class="gb-hero__wave" viewBox="0 0 1440 120" preserveAspectRatio="none" aria-hidden="true">
        <path d="M0,80 C240,120 480,0 720,40 C960,80 1200,120 1440,60 L1440,120 L0,120 Z" fill="#ffffff"></path>
    </svg>
</section>

<div class="page">
    <!-- ================= 편집장의 선택 ================= -->
    <section class="editor-choice-section">
        <div class="section-header">
            <h2 class="section-title" id="section-title">편집장의 선택</h2>
            <button class="play-button playing" aria-label="재생/정지"></button>
        </div>

        <!-- 편집장의 선택 -->
        <div class="tab-content editor-choice active" id="editor-choice-content">
            <div class="books-grid grid-4">
                <c:forEach var="data" items="${choice}">
                    <a href="${pageContext.request.contextPath}/book/${data.bookId}" class="weekly-info-link">
                        <div class="book-card">
                            <div class="book-image">
                                <img src="${empty data.imgUrl ? '/images/thumb_ing.gif' : data.imgUrl}"
                                     alt="${fn:escapeXml(data.title)}"
                                     loading="lazy" decoding="async" referrerpolicy="no-referrer">
                                <div class="book-number">1</div>
                            </div>

                            <h3 class="book-title">
                                <c:choose>
                                    <c:when test="${fn:length(data.title) > 25}">
                                        ${fn:substring(data.title, 0, 25)}...
                                    </c:when>
                                    <c:otherwise>${data.title}</c:otherwise>
                                </c:choose>
                            </h3>

                            <p class="book-author my-3"><c:out value="${data.name}"/></p>

                            <div class="editor-comment">
                                <h4 class="new-book-description">
                                        ${empty data.description ? '설명 준비중' : data.description}
                                </h4>
                            </div>

                            <div class="book-rating" aria-label="평점">
                                <c:set var="avg" value="${choiceRatingMap[data.bookId] != null ? choiceRatingMap[data.bookId] : 0}" />
                                <c:forEach begin="1" end="5" var="i">
                                    <c:choose>
                                        <c:when test="${i <= avg}">★</c:when>
                                        <c:otherwise>☆</c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <span class="rating-number">
        (<fmt:formatNumber value="${avg}" minFractionDigits="1" maxFractionDigits="1" />)
    </span>
                            </div>
                        </div>
                    </a>
                </c:forEach>
            </div>
        </div>

        <!-- 신간 소개 -->
        <div class="tab-content" id="new-books-content">
            <div class="new-books-grid grid-4">
                <c:forEach var="data" items="${introductions}">
                    <div class="new-book-card">
                        <div class="new-book-badge">NEW</div>

                        <a href="${pageContext.request.contextPath}/book/${data.bookId}" class="new-book-link">
                            <div class="new-book-image">
                                <img src="${empty data.imgUrl ? '/images/thumb_ing.gif' : data.imgUrl}"
                                     alt="${fn:escapeXml(data.title)}"
                                     loading="lazy" decoding="async" referrerpolicy="no-referrer">
                            </div>
                        </a>

                        <h3 class="new-book-title">
                            <c:choose>
                                <c:when test="${fn:length(data.title) > 15}">
                                    ${fn:substring(data.title, 0, 15)}...
                                </c:when>
                                <c:otherwise><c:out value="${data.title}"/></c:otherwise>
                            </c:choose>
                        </h3>

                        <p class="new-book-author"><c:out value="${data.name}"/></p>
                        <div class="new-book-date"><c:out value="${data.publishedDate}"/></div>

                        <p class="new-book-description">
                            <c:choose>
                                <c:when test="${not empty data.description}">
                                    <c:out value="${data.description}"/>
                                </c:when>
                                <c:otherwise>설명 준비중</c:otherwise>
                            </c:choose>
                        </p>

                        <button class="new-book-button">예약구매</button>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- 화제의 책 -->
        <div class="tab-content" id="trending-content">
            <div class="trending-grid grid-4">
                <c:forEach var="data" items="${randomBooks}">
                    <c:url var="detailUrl" value="/book/${data.bookId}"/>
                    <div class="trending-card">
                        <div class="trending-badge hot">HOT</div>

                        <a href="${detailUrl}" class="trending-book-link" aria-label="${fn:escapeXml(data.title)} 상세보기">
                            <div class="trending-image">
                                <img src="${empty data.imgUrl ? pageContext.request.contextPath.concat('/images/thumb_ing.gif') : data.imgUrl}"
                                     alt="${fn:escapeXml(data.title)}"
                                     onerror="this.src='${pageContext.request.contextPath}/images/thumb_ing.gif'"
                                     loading="lazy" decoding="async">
                                <div class="trending-rank">-</div>
                            </div>
                        </a>

                        <h3 class="trending-title">
                            <a href="${detailUrl}"><c:out value="${data.title}"/></a>
                        </h3>

                        <p class="trending-author">
                            <c:out value="${data.authorName != null ? data.authorName : '인기작가'}"/>
                        </p>

                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- 지금 핫딜중 -->
        <div class="tab-content" id="hotdeal-content">
            <div class="hotdeal-grid grid-4">
                <c:forEach var="data" items="${hotdeal}">
                    <c:url var="detailUrl" value="/book/${data.bookId}"/>
                    <div class="hotdeal-card" data-book-id="${data.bookId}" data-detail-url="${detailUrl}">
                        <div class="hotdeal-badge discount-30">
                            <c:set var="discountRate" value="${(data.price - data.discounted_price) * 100 / data.price}"/>
                            할인율: <fmt:formatNumber value="${discountRate}" maxFractionDigits="0"/>%
                        </div>

                        <div class="hotdeal-image">
                            <a href="${detailUrl}" aria-label="${fn:escapeXml(data.title)} 상세보기">
                                <img src="${empty data.imgUrl ? pageContext.request.contextPath.concat('/images/thumb_ing.gif') : data.imgUrl}"
                                     alt="<c:out value='${data.title}'/>"
                                     onerror="this.src='${pageContext.request.contextPath}/images/thumb_ing.gif'"
                                     loading="lazy" decoding="async">
                            </a>
                        </div>

                        <h3 class="hotdeal-title">
                            <a href="${detailUrl}"><c:out value="${data.title}"/></a>
                        </h3>

                        <p class="hotdeal-author"><c:out value="${data.name}"/></p>

                        <div class="hotdeal-prices">
                            <span class="original-price"><fmt:formatNumber value="${data.price}" pattern="#,##0"/></span>
                            <span class="sale-price"><fmt:formatNumber value="${data.discounted_price}" pattern="#,##0"/></span>
                        </div>
                        <button class="hotdeal-time" onclick="event.stopPropagation(); addToCart(${data.bookId}, 1)">장바구니🛒</button>
                        <button class="hotdeal-button buy-now" data-book-id="${data.bookId}">구매하기</button>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- 이 주의 책 -->
        <div class="tab-content" id="weekly-content">
            <div class="weekly-grid grid-4">
                <c:forEach var="book" items="${weeklyBooks}">
                    <div class="weekly-card">
                        <div class="weekly-badge">이주의책</div>

                        <a href="${pageContext.request.contextPath}/book/${book.bookId}" class="weekly-image-link">
                            <div class="weekly-image">
                                <img src="${empty book.imgUrl ? '/images/thumb_ing.gif' : book.imgUrl}"
                                     alt="${fn:escapeXml(book.title)}"
                                     class="book-thumb"
                                     loading="lazy" decoding="async">
                            </div>
                        </a>

                        <a href="${pageContext.request.contextPath}/book/${book.bookId}" class="weekly-info-link">
                            <div class="weekly-info">
                                <h3 class="weekly-title clamp-2">
                                    <c:choose>
                                        <c:when test="${fn:length(book.title) > 30}">
                                            ${fn:substring(book.title, 0, 30)}...
                                        </c:when>
                                        <c:otherwise>${book.title}</c:otherwise>
                                    </c:choose>
                                </h3>
                                <p class="weekly-author"><c:out value="${book.authorName}"/></p>
                                <div class="weekly-rating" aria-label="평점">
                                    <c:set var="avg" value="${weeklyRatingMap[book.bookId] != null ? weeklyRatingMap[book.bookId] : 0}" />
                                    <span class="stars">
        <c:forEach begin="1" end="5" var="i">
            <c:choose>
                <c:when test="${i <= avg}">★</c:when>
                <c:otherwise>☆</c:otherwise>
            </c:choose>
        </c:forEach>
    </span>
                                    <span class="rating-score">
        <fmt:formatNumber value="${avg}" minFractionDigits="1" maxFractionDigits="1" />
    </span>
                                    <span class="rating-text">평점</span>
                                </div>

                                <div class="weekly-comment">
                                    <p class="comment-text clamp-2">
                                            ${empty book.description ? '설명 준비중' : book.description}
                                    </p>
                                </div>
                            </div>
                        </a>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- 탭 메뉴 -->
        <div class="tab-menu" role="tablist" aria-label="콘텐츠 탭">
            <button class="tab-item active" onclick="showTab('editor-choice-content','편집장의 선택')">편집장의 선택</button>
            <button class="tab-item" onclick="showTab('weekly-content','이 주의 책')">이 주의 책</button>
            <button class="tab-item" onclick="showTab('new-books-content','신간 소개')">신간 소개</button>
            <button class="tab-item" onclick="showTab('trending-content','화제의 책')">화제의 책</button>
            <button class="tab-item" onclick="showTab('hotdeal-content','지금 핫딜중')">지금 핫딜중</button>
        </div>
    </section>

    <!-- 광고창(마키) -->
    <section class="ad-marquee" aria-live="polite" aria-label="프로모션 알림">
        <div class="marquee-content" role="status">
            📢 특별 할인! 9월 한정, 인기 도서 최대 30% 할인 중! 🎁 | 신규 회원은 첫 구매 시 추가 쿠폰 지급! ✨ | 이번 주 이벤트: 베스트셀러 1+1!
        </div>
    </section>

    <section class="mood-hero night" aria-label="감성 문장 배너">
        <div class="mood-hero__bg">
            <div class="mood-hero__sky" aria-hidden="true">
                <span class="st"></span><span class="st"></span><span class="st"></span><span class="st"></span><span class="st"></span>
                <span class="st"></span><span class="st"></span><span class="st"></span><span class="st"></span><span class="st"></span>
            </div>
            <div class="mood-hero__aurora" aria-hidden="true"></div>
            <div class="mood-hero__grain" aria-hidden="true"></div>
        </div>

        <div class="mood-hero__inner">
            <p class="mood-hero__eyebrow">오늘의 문장</p>
            <h2 class="mood-hero__quote" id="moodQuote" aria-live="polite">
                “괜찮아, 천천히 가도 돼.”
            </h2>
            <p class="mood-hero__meta" id="moodMeta">밤은 늘 너를 쉬게 하려고 온다</p>
        </div>
    </section>

    <!-- 이달의 주목도서 -->
    <section class="featured-books">
        <div class="featured-header">
            <div class="featured-title-area">
                <div class="bookmark-icon">📑</div>
                <div class="featured-title-text">
                    <h2 class="featured-main-title">이달의 주목도서</h2>
                    <p class="featured-subtitle">
                        <c:choose>
                            <c:when test="${not empty featuredBooks}">
                                편집부가 엄선한 ${fn:length(featuredBooks)}권
                            </c:when>
                            <c:otherwise>데이터 준비 중</c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </div>
            <div class="hot-indicator">🔥 Hot 5</div>
        </div>

        <c:if test="${empty featuredBooks}">
            <div class="featured-empty">등록된 주목도서가 없습니다. 곧 업데이트됩니다.</div>
        </c:if>

        <c:if test="${not empty featuredBooks}">
            <div class="featured-books-grid">
                <c:forEach var="b" items="${featuredBooks}">
                    <div class="featured-book-card">
                        <div class="book-info-top"></div>

                        <a href="${pageContext.request.contextPath}/book/${b.bookId}" class="featured-book-link">
                            <div class="featured-book-image">
                                <img
                                        src="${empty b.imgUrl ? 'https://via.placeholder.com/160x220/cccccc/000000?text=No+Image' : b.imgUrl}"
                                        alt="${fn:escapeXml(b.title)}"
                                        onerror="this.src='https://via.placeholder.com/160x220/cccccc/000000?text=No+Image'"
                                        loading="lazy" decoding="async"/>
                            </div>
                        </a>

                        <div class="featured-book-info">
                            <h3 class="featured-book-title"><c:out value="${b.title}"/></h3>
                            <p class="featured-book-author"><c:out value="${b.authorName}"/></p>
                            <div class="book-rating"></div>
                            <div class="book-price"></div>
                            <p class="book-description"><c:out value="${b.description}"/></p>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <div class="view-all-link">
            </div>
        </c:if>
    </section>

    <div class="moon-divider" aria-hidden="true">
        <span class="moon"></span>
        <span class="stars"></span>
    </div>

    <!-- 베스트셀러 TOP 10 -->
    <section class="bestseller-section">
        <h2 class="bestseller-title">
            어제 베스트셀러 TOP 10
            <small style="font-size:12px;color:#888;">(누적 판매 기준 v1)</small>
        </h2>

        <c:if test="${empty bestSellers}">
            <div class="featured-empty">데이터 준비 중</div>
        </c:if>

        <c:if test="${not empty bestSellers}">
            <div class="bestseller-grid">
                <c:forEach var="b" items="${bestSellers}" varStatus="s">
                    <c:url var="detailUrl" value="/book/${b.bookId}"/>

                    <a class="bestseller-item" href="${detailUrl}">
                        <!-- rank-number 색상 클래스 버그 수정 -->
                        <div class="rank-number ${s.index lt 3 ? 'rank-' : ''}${s.index lt 3 ? (s.index + 1) : ''}">
                                ${s.index + 1}
                        </div>

                        <img class="bestseller-thumb"
                             src="${empty b.imgUrl ? 'https://via.placeholder.com/60x80/cccccc/000000?text=No+Image' : b.imgUrl}"
                             alt="${fn:escapeXml(b.title)}"
                             onerror="this.src='https://via.placeholder.com/60x80/cccccc/000000?text=No+Image'"
                             loading="lazy" decoding="async"/>

                        <div class="bestseller-info">
                            <div class="bestseller-title-line">
                                <h3 class="bestseller-book-title">${b.title}</h3>
                            </div>
                            <p class="bestseller-author">${b.authorName}</p>
                            <div class="count">판매: ${b.orderCount}권</div>
                        </div>
                    </a>
                </c:forEach>
            </div>
        </c:if>
    </section>

    <!-- 전폭 슬라이더 광고 배너 -->
    <section class="slider-ad-container">
        <div class="slider-ad">
            <button class="slider-nav prev" onclick="prevBanner()">‹</button>
            <button class="slider-nav next" onclick="nextBanner()">›</button>
            <div class="slider-track" id="sliderTrack">
                <div class="slider-item">
                    <div class="slider-content">
                        <h2 class="slider-title">🍂 가을 독서 페스티벌 🍂</h2>
                        <p class="slider-subtitle">9월 한정! 모든 문학도서 25% 할인 + 무료배송</p>
                        <a href="/autumn-event" class="slider-button" fetchpriority="high">지금 구매하기</a>
                    </div>
                </div>
                <div class="slider-item">
                    <div class="slider-content">
                        <h2 class="slider-title">✨ VIP 멤버십 특가 ✨</h2>
                        <p class="slider-subtitle">프리미엄 회원 가입시 전자책 무제한 이용권 증정!</p>
                        <a href="/membership" class="slider-button">멤버십 가입</a>
                    </div>
                </div>
                <div class="slider-item">
                    <div class="slider-content">
                        <h2 class="slider-title">🔥 이달의 신간 베스트 🔥</h2>
                        <p class="slider-subtitle">화제의 신간도서 예약 주문시 15% 할인 + 굿즈 증정</p>
                        <a href="/new-books" class="slider-button">신간 보러가기</a>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- 화제의 책 소식 -->
    <section class="hot-news-section">
        <h2 class="hot-news-title">화제의 책 소식</h2>

        <c:choose>
            <c:when test="${empty hotNews}">
                <div class="featured-empty">데이터 준비 중</div>
            </c:when>

            <c:otherwise>
                <div class="hot-news-slider">
                    <div class="hot-news-container">
                        <div class="hot-news-page active">
                            <div class="hot-news-grid">
                                <c:forEach var="b" items="${hotNews}" varStatus="s">
                                    <c:url var="detailUrl" value="/book/${b.bookId}"/>

                                    <div class="hot-news-card">
                                        <div class="card-header">
                                            <span class="rank-badge rank-${s.index + 1}">#${s.index + 1}</span>
                                            <span class="status-badge ${s.index==0 ? 'hot' : (s.index==1 ? 'best' : 'new')}">
                                                    ${s.index==0 ? 'HOT' : (s.index==1 ? 'BEST' : 'NEW')}
                                            </span>
                                        </div>

                                        <div class="book-cover">
                                            <a href="${detailUrl}" aria-label="${fn:escapeXml(b.title)} 상세보기">
                                                <img
                                                        src="${empty b.imgUrl ? 'https://via.placeholder.com/200x280/cccccc/000000?text=No+Image' : b.imgUrl}"
                                                        alt="${fn:escapeXml(b.title)}"
                                                        onerror="this.src='https://via.placeholder.com/200x280/cccccc/000000?text=No+Image'"
                                                        loading="lazy" decoding="async">
                                            </a>
                                        </div>

                                        <div class="book-content">
                                            <h3 class="book-title">
                                                <a href="${detailUrl}"><c:out value="${b.title}"/></a>
                                            </h3>
                                            <p class="book-author"><c:out value="${b.authorName}"/></p>
                                            <p class="book-description"><c:out value="${b.description}"/></p>
                                            <div class="book-meta">
                                                <span>장르</span>
                                                <span>뉴스</span>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </section>

    <!-- 프로모션 2칸 슬라이더 -->
    <section class="promotion-section">
        <div class="promotion-slider">
            <button class="promo-slider-btn prev" id="promoPrevBtn"><</button>
            <button class="promo-slider-btn next" id="promoNextBtn">></button>

            <div class="promotion-container">
                <!-- 페이지 1 -->
                <div class="promotion-page active">
                    <div class="promotion-grid">
                        <c:forEach var="p" items="${promoBooks}" varStatus="st">
                            <c:if test="${st.index lt 2}">
                                <c:url var="detailUrl" value="/book/${p.bookId}"/>
                                <div class="promotion-card
                                    ${st.index == 0 ? 'bestseller-promo' : ''}
                                    ${st.index == 1 ? 'md-promo' : ''}">

                                    <div class="promo-icon">
                                        <c:choose>
                                            <c:when test="${st.index == 0}">
                                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                                                     stroke="currentColor" stroke-width="2">
                                                    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87
                                                     1.18 6.88L12 17.77l-6.18 3.25L7 14.14
                                                     2 9.27l6.91-1.01L12 2z"></path>
                                                </svg>
                                                <span>선간</span>
                                            </c:when>
                                            <c:otherwise>
                                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                                                     stroke="currentColor" stroke-width="2">
                                                    <path d="M9 11H3v8h6m11-8h-6v8h6m-7-14v8m-5-5 5 5 5-5"></path>
                                                </svg>
                                                <span>MD추천</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="promo-content">
                                        <h3 class="promo-title"><c:out value="${p.title}"/></h3>
                                        <h4 class="promo-subtitle">
                                            <c:out value="${p.authorName}"/> · <c:out value="${p.publisherName}"/>
                                        </h4>
                                        <p class="promo-description">
                                            <c:out value="${fn:length(p.description) > 60
                                                ? fn:substring(p.description,0,60).concat('...')
                                                : p.description}"/>
                                        </p>
                                        <a class="promo-button" href="${detailUrl}">자세히 보기 ></a>
                                    </div>

                                    <div class="promo-image">
                                        <a href="${detailUrl}">
                                            <img
                                                    src="${empty p.imgUrl ? 'https://via.placeholder.com/120x160/cccccc/000000?text=No+Image' : p.imgUrl}"
                                                    alt="${fn:escapeXml(p.title)}"
                                                    onerror="this.src='https://via.placeholder.com/120x160/cccccc/000000?text=No+Image'"
                                                    loading="lazy" decoding="async">
                                        </a>
                                    </div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>

                <!-- 페이지 2 -->
                <div class="promotion-page">
                    <div class="promotion-grid">
                        <c:forEach var="p" items="${promoBooks}" varStatus="st">
                            <c:if test="${st.index ge 2}">
                                <c:url var="detailUrl" value="/book/${p.bookId}"/>
                                <div class="promotion-card
                                    ${st.index == 2 ? 'new-book-promo' : ''}
                                    ${st.index == 3 ? 'audiobook-promo' : ''}">

                                    <div class="promo-icon">
                                        <c:choose>
                                            <c:when test="${st.index == 2}">
                                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                                                     stroke="currentColor" stroke-width="2">
                                                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                                                    <polyline points="14,2 14,8 20,8"></polyline>
                                                    <line x1="16" y1="13" x2="8" y2="13"></line>
                                                    <line x1="16" y1="17" x2="8" y2="17"></line>
                                                </svg>
                                                <span>신간</span>
                                            </c:when>
                                            <c:otherwise>
                                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                                                     stroke="currentColor" stroke-width="2">
                                                    <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon>
                                                    <path d="m19.07 4.93-1.4 1.4A6.5 6.5 0 0 1 19.5 12
                                                     a6.5 6.5 0 0 1-1.83 5.67l1.4 1.4A8.5 8.5 0 0 0
                                                     21.5 12a8.5 8.5 0 0 0-2.43-7.07z"></path>
                                                </svg>
                                                <span>오디오</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="promo-content">
                                        <h3 class="promo-title"><c:out value="${p.title}"/></h3>
                                        <h4 class="promo-subtitle">
                                            <c:out value="${p.authorName}"/> · <c:out value="${p.publisherName}"/>
                                        </h4>
                                        <p class="promo-description">
                                            <c:out value="${fn:length(p.description) > 60
                                                ? fn:substring(p.description,0,60).concat('...')
                                                : p.description}"/>
                                        </p>
                                        <a class="promo-button" href="${detailUrl}">자세히 보기 ></a>
                                    </div>

                                    <div class="promo-image">
                                        <a href="${detailUrl}">
                                            <img
                                                    src="${empty p.imgUrl ? 'https://via.placeholder.com/120x160/cccccc/000000?text=No+Image' : p.imgUrl}"
                                                    alt="${fn:escapeXml(p.title)}"
                                                    onerror="this.src='https://via.placeholder.com/120x160/cccccc/000000?text=No+Image'"
                                                    loading="lazy" decoding="async">
                                        </a>
                                    </div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>

        <div class="promotion-footer">
            <p class="promotion-notice">
                <span class="notice-dot">●</span>
                매주 새로운 책 프로모션, 독자 여러분을 위한 특별한 혜택
            </p>
        </div>
    </section>

    <!-- 이 주의 특가 -->
    <section class="weekly-special-section">
        <div class="special-header">
            <div class="special-title-area">
                <span class="special-icon">🏷️</span>
                <div class="special-title-text">
                    <h2 class="special-main-title">이 주의 특가</h2>
                    <p class="special-subtitle">최대 80% 할인</p>
                </div>
            </div>
        </div>

        <div class="special-books-grid">
            <c:forEach var="b" items="${weeklySpecials}">
                <c:url var="detailUrl" value="/book/${b.bookId}"/>
                <div class="special-book-card">
                    <div class="special-badges">
                        <c:if test="${b.discountRate > 0}">
                            <div class="discount-badge">${b.discountRate}% 할인</div>
                        </c:if>
                        <div class="days-left">2일 남음</div>
                    </div>

                    <div class="special-book-image">
                        <a href="${detailUrl}">
                            <img src="${b.imgUrl}" alt="${fn:escapeXml(b.title)}" loading="lazy" decoding="async">
                        </a>
                    </div>

                    <div class="special-book-info">
                        <div class="book-category">${fn:escapeXml(b.categoryName)}</div>
                        <h3 class="special-book-title">
                            <a href="${detailUrl}">${fn:escapeXml(b.title)}</a>
                        </h3>
                        <p class="special-book-author">${fn:escapeXml(b.authorName)}</p>
                        <div class="special-price-info">
                            <span class="original-price"><fmt:formatNumber value="${b.price}" pattern="#,##0"/>원</span>
                            <span class="special-price"><fmt:formatNumber value="${b.discountedPrice}" pattern="#,##0"/>원</span>
                            <c:if test="${b.discountRate > 0}">
                                <span class="discount-rate">${b.discountRate}% 할인</span>
                            </c:if>
                            <span class="price-label">적립</span>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>


        <div class="special-notice">
            <div class="notice-text">
                <span class="notice-icon">⚠️</span>
                특가 할인은 매주 일요일 자정에 종료됩니다
            </div>

        </div>
    </section>

    <!-- 오디오북 -->
    <section class="audiobook-section">
        <div class="audiobook-header">
            <div class="audiobook-title-area">
                <div class="audiobook-icon">🎧</div>
                <div class="audiobook-title-text">
                    <h2 class="audiobook-main-title">글벗 오디오북</h2>
                    <p class="audiobook-subtitle">언제 어디서나 듣는 독서의 새로운 경험</p>
                </div>
            </div>
            <div class="audiobook-actions">
                <button class="free-trial-btn">▶ 무료 체험 가능</button>
                <a href="/audiobooks" class="audiobook-more-link">전체보기 ></a>
            </div>
        </div>

        <div class="audiobook-grid">
            <c:forEach var="book" items="${audiobooks}" varStatus="status">
                <a href="/book/${book.bookId}" class="audiobook-card-link">
                    <div class="audiobook-card">
                        <div class="audiobook-badge">
                            <c:choose>
                                <c:when test="${status.index == 0}">NEW</c:when>
                                <c:when test="${status.index == 1}">인기</c:when>
                                <c:otherwise>BEST</c:otherwise>
                            </c:choose>
                        </div>

                        <div class="audiobook-cover">
                            <img src="${book.imgUrl != null && !book.imgUrl.isEmpty() ? book.imgUrl : '/images/thumb_ing.gif'}"
                                 alt="${fn:escapeXml(book.title)}" loading="lazy" decoding="async">
                            <div class="audio-icon">🎧</div>
                            <div class="play-time">
                                <c:choose>
                                    <c:when test="${status.index == 0}">7시간 32분</c:when>
                                    <c:when test="${status.index == 1}">5시간 15분</c:when>
                                    <c:when test="${status.index == 2}">6시간</c:when>
                                    <c:otherwise>6시간 40분</c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <div class="audiobook-info">
                            <div class="audiobook-rating">
                                <span class="rating-stars">
                                    <c:choose>
                                        <c:when test="${status.index == 0}">⭐ 4.8</c:when>
                                        <c:when test="${status.index == 1}">⭐ 4.5</c:when>
                                        <c:when test="${status.index == 2}">⭐ 4.9</c:when>
                                        <c:otherwise>⭐ 4.7</c:otherwise>
                                    </c:choose>
                                </span>
                                <span class="audiobook-category">${book.categoryName}</span>
                            </div>
                            <h3 class="audiobook-title">${book.title}</h3>
                            <p class="audiobook-author">저자: ${book.authorName}</p>
                            <p class="audiobook-narrator">
                                <c:choose>
                                    <c:when test="${status.index == 0}">낭독: 최종일</c:when>
                                    <c:when test="${status.index == 1}">낭독: 서덕규</c:when>
                                    <c:when test="${status.index == 2}">낭독: 신승화</c:when>
                                    <c:otherwise>낭독: 문려경</c:otherwise>
                                </c:choose>
                            </p>
                        </div>
                    </div>
                </a>
            </c:forEach>
        </div>

        <div class="audiobook-promotion">
            <div class="promo-content-box">
                <div class="promo-icon-large">🎧</div>
                <div class="promo-text">
                    <h3 class="promo-main-title">첫 달 무료체험</h3>
                    <p class="promo-description">매월 1권 무료 + 30% 할인혜택</p>
                </div>
                <button class="start-trial-btn">무료체험 시작하기</button>
            </div>
            <a href="/audiobooks-all" class="more-audiobooks-link">더 많은 오디오북 보기</a>
        </div>
    </section>

    <!-- 수상 섹션 -->
    <section class="awards-section">
        <div class="awards-header">
            <h2>수상 및 인증</h2>
            <p>고객님께 더 나은 서비스를 제공하기 위한 저희의 노력이 다양한 기관으로부터 인정받고 있습니다.</p>
        </div>

        <div class="awards-cards">
            <div class="award-card yellow">
                <div class="icon">🏆</div>
                <div class="year">2024년</div>
                <div class="title">대한민국 우수서점상</div>
                <div class="subtitle">문화체육관광부 장관상</div>
                <div class="desc">고객 서비스 및 도서 큐레이션 부문</div>
            </div>
            <div class="award-card lightblue">
                <div class="icon">🥇</div>
                <div class="year">2023년</div>
                <div class="title">온라인 서점 대상</div>
                <div class="subtitle">한국서점협회</div>
                <div class="desc">디지털 혁신 및 사용자 경험</div>
            </div>
            <div class="award-card orange">
                <div class="icon">🎖️</div>
                <div class="year">2023년</div>
                <div class="title">베스트 북커머스</div>
                <div class="subtitle">온라인쇼핑몰협회</div>
                <div class="desc">고객 만족도 최우수</div>
            </div>
            <div class="award-card blue">
                <div class="icon">⭐</div>
                <div class="year">2022년</div>
                <div class="title">독서문화진흥 공로상</div>
                <div class="subtitle">국립중앙도서관</div>
                <div class="desc">지역 독서문화 확산 기여</div>
            </div>
        </div>

        <div class="awards-footer">
            <p>신뢰할 수 있는 온라인 서점</p>
            <p>2020년부터 지금까지 누적 고객 만족도 98.5%를 달성하며, 독자 여러분께 사랑받는 서점으로 성장해왔습니다. 앞으로도 더 나은 환경을 만들어 나가겠습니다.</p>
            <div class="features">
                <span class="feature red">● 안전한 결제 시스템</span>
                <span class="feature green">● 신속한 배송 서비스</span>
                <span class="feature navy">● 전문 큐레이션</span>
                <span class="feature yellow">● 24시간 고객지원</span>
            </div>
        </div>
    </section>
</div>

<jsp:include page="/common/footer.jsp"/>

<!-- 메인 페이지 스크립트(수정 반영본) -->
<script src="/js/home.js"></script>
<script src="/js/home_mood.js"></script>
</body>
</html>
