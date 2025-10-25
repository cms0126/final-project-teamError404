<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>리뷰 작성</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- (선택) CSRF 메타: header.jsp에 이미 있다면 중복 무시됨 -->
    <c:if test="${not empty _csrf}">
        <meta name="_csrf" content="${_csrf.token}">
        <meta name="_csrf_header" content="${_csrf.headerName}">
    </c:if>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/css/00_common.css">
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/footer.css">
    <link rel="stylesheet" href="/css/mypage/mypage.css">

    <style>
        .review-page {
            min-height: 100vh;
            background: var(--color-bg);
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: var(--space-6) 0;
        }
        .review-card {
            background: var(--color-surface);
            border: 1px solid var(--color-border);
            box-shadow: var(--shadow-sm);
            border-radius: var(--radius-lg);
            padding: var(--space-5);
            width: min(92%, 700px);
        }
        .review-header { text-align: center; margin-bottom: var(--space-4); }
        .book-info {
            display: flex; gap: var(--gap-3);
            align-items: flex-start; margin-bottom: var(--space-4);
        }
        .book-info img {
            width: 100px; height: 140px; object-fit: cover;
            border-radius: var(--radius-sm); border: 1px solid var(--color-border);
        }
        .book-info .book-title {
            font-weight: 700; font-size: 1.125rem; color: var(--color-text);
        }
        .star-rating {
            display: flex; justify-content: center; gap: var(--gap-2);
            margin-bottom: var(--space-3);
        }
        .star { font-size: 2rem; color: var(--color-border); cursor: pointer; transition: color var(--dur) var(--ease); }
        .star.active { color: gold; }
        textarea.review-text {
            width: 100%; height: 160px; padding: var(--space-3);
            border: 1px solid var(--color-border); border-radius: var(--radius);
            resize: none;
        }
        .btn-submit {
            display: block; width: 100%; margin-top: var(--space-4);
            padding: var(--space-3); background: var(--color-accent-dark);
            color: #fff; border-radius: var(--radius);
            transition: background var(--dur) var(--ease);
        }
        .btn-submit:hover { background: var(--color-accent); }
        .review-subtitle { color: var(--color-accent); font-size: 1rem; }
    </style>
</head>
<body>
<jsp:include page="/common/header.jsp" />

<div class="review-page">
    <!-- 데이터는 data-*로 안전하게 전달 -->
    <div class="review-card" id="reviewCard"
         data-book-id="${item.bookId}"
         data-ordered-item-id="${item.orderedItemId}">
        <div class="review-header">
            <h2>리뷰 작성</h2>
            <p class="review-subtitle">상품에 대한 솔직한 후기를 남겨주세요</p>
        </div>

        <!-- 책 정보 -->
        <div class="book-info">
            <img src="<c:out value='${item.imageUrl}'/>" alt="<c:out value='${item.title}'/>">
            <div class="book-title ml-5"><c:out value="${item.title}"/></div>
        </div>

        <!-- 별점 -->
        <div class="star-rating" id="starRating" aria-label="별점 선택">
            <span class="star" data-value="1" role="button" aria-label="1점">★</span>
            <span class="star" data-value="2" role="button" aria-label="2점">★</span>
            <span class="star" data-value="3" role="button" aria-label="3점">★</span>
            <span class="star" data-value="4" role="button" aria-label="4점">★</span>
            <span class="star" data-value="5" role="button" aria-label="5점">★</span>
        </div>

        <!-- 리뷰 내용 -->
        <textarea class="review-text" id="reviewText" placeholder="리뷰를 입력하세요" maxlength="2000"></textarea>

        <!-- 등록 버튼 -->
        <button class="btn-submit" id="submitReview" type="button">리뷰 등록</button>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

<script>
    (function () {
        // DOM 준비 후 실행
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', init);
        } else {
            init();
        }

        function init() {
            // 별점 클릭
            const stars = document.querySelectorAll('.star');
            let selectedRating = 0;
            stars.forEach(star => {
                star.addEventListener('click', () => {
                    selectedRating = parseInt(star.getAttribute('data-value'), 10);
                    stars.forEach(s => s.classList.toggle(
                        'active',
                        parseInt(s.getAttribute('data-value'), 10) <= selectedRating
                    ));
                });
            });

            // 데이터 읽기 (EL 비어도 안전)
            const card = document.getElementById('reviewCard');
            const bookId = Number(card?.dataset?.bookId || 0);
            const orderedItemId = Number(card?.dataset?.orderedItemId || 0);

            // CSRF 메타가 있으면 자동 주입
            const CSRF_TOKEN  = document.querySelector('meta[name="_csrf"]')?.content;
            const CSRF_HEADER = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';

            // 리뷰 등록
            const $btn = document.getElementById('submitReview');
            $btn?.addEventListener('click', async () => {
                const text = document.getElementById('reviewText').value.trim();
                if (!bookId || !orderedItemId) { alert('잘못된 접근입니다.'); return; }
                if (selectedRating === 0) { alert('별점을 선택해주세요!'); return; }
                if (text.length < 5) { alert('리뷰 내용을 5자 이상 입력해주세요.'); return; }

                try {
                    const res = await fetch('/reviews/save', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            ...(CSRF_TOKEN ? { [CSRF_HEADER]: CSRF_TOKEN } : {})
                        },
                        body: JSON.stringify({ bookId, orderedItemId, rating: selectedRating, content: text })
                    });

                    const body = (await res.text()).trim(); // 공백/개행 방지
                    if (res.status === 409 || body === 'duplicate') {
                        alert('해당 주문에 대해선 리뷰를 이미 작성하셨습니다');
                        return;
                    }
                    if (res.ok && body === 'success') {
                        alert('리뷰가 등록되었습니다.');
                        location.href = '/books/' + bookId + '#reviews';
                    } else {
                        // 서버가 HTML(로그인 페이지) 등을 준 경우 대비
                        alert('해당 주문에 대해선 리뷰를 이미 작성하셨습니다');
                    }
                } catch (e) {
                    alert('네트워크 오류가 발생했습니다.');
                }
            });
        }
    })();
</script>

</body>
</html>
