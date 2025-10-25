/**
 * book_ani.js — 도서 검색/목록 페이지 애니메이션 전용
 * -----------------------------------------------------
 * - 각 검색 결과 카드(.srch-item)에 페이드업 효과 적용
 * - 탭 버튼(주간/월간 베스트) 전환 시 페이드 스위칭
 * - DOMContentLoaded 이후 실행
 */

document.addEventListener('DOMContentLoaded', () => {
    const items = document.querySelectorAll('.srch-item');
    const observer = new IntersectionObserver(
        (entries, obs) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('soft-fade-up');
                    obs.unobserve(entry.target);
                }
            });
        },
        { threshold: 0.2 }
    );

    items.forEach(item => observer.observe(item));

    // 베스트셀러 탭 전환 애니메이션
    const container = document.querySelector('.bestseller-container');
    const tabButtons = document.querySelectorAll('.tab-btn');
    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            tabButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            if (!container) return;
            const showMonthly = btn.textContent.includes('월간');
            container.classList.toggle('show-monthly', showMonthly);
        });
    });
});
