// /js/book_detail/book_detail.enhanced.js
console.log('[book_detail] enhanced: qty/total, sticky bar, image skeleton, reveal');

(() => {
    /* ===== 공통 유틸 ===== */
    const fmtKR = (n) => new Intl.NumberFormat('ko-KR').format(Number(n || 0));
    const priceUnit = (window.PRODUCT?.discountedPrice ?? window.PRODUCT?.price) ?? 0;

    /* ===== 1) 표지 스켈레톤/로드 ===== */
    const cover = document.querySelector('.book-cover');
    if (cover) {
        cover.classList.add('loading');
        const img = cover.querySelector('img');
        if (img?.complete) onCoverLoad();
        else img?.addEventListener('load', onCoverLoad);
        function onCoverLoad() {
            cover.classList.remove('loading');
            cover.classList.add('loaded');
        }
    }

    /* ===== 2) 수량 & 합계 ===== */
    // DOM 캐시
    const qtyInput   = document.getElementById('qtyInput');
    const totalEl    = document.getElementById('totalPrice');
    const stickyBar  = document.getElementById('stickyBar');
    const sQty       = document.getElementById('stickyQty');
    const sTot       = document.getElementById('stickyTotal');
    const earnEl     = document.getElementById('earnPoint');
    const stickyEarn = document.getElementById('stickyEarn');

    if (stickyBar && stickyBar.parentElement !== document.body) {
        document.body.appendChild(stickyBar);
        document.body.classList.add('has-sticky-bottom'); // 본문 하단 여백 확보
    }

    // 수량 초기화 (세션 저장된 값 있으면 복원)
    const key = 'BOOK_QTY_' + (window.PRODUCT?.id || '');
    let saved = 1;
    try {
        const raw = sessionStorage.getItem(key);
        saved = Math.max(1, Number(raw) || 1);
    } catch (_) { /* storage 불가 환경 보호 */ }
    if (qtyInput) qtyInput.value = String(saved);

    function getQty() {
        const n = Number(qtyInput?.value);
        return Number.isFinite(n) && n > 0 ? Math.floor(n) : 1;
    }

    function setQty(n) {
        const v = Math.max(1, Math.min(99, Number(n) || 1));
        if (qtyInput) qtyInput.value = String(v);
        try { sessionStorage.setItem(key, String(v)); } catch (_) {}
        updateTotals();
    }

    // ✅ 변수명 q로 통일 (이 파일에서 qty 변수명 사용 금지)
    function updateTotals() {
        const q = getQty();
        const tot = priceUnit * q;

        if (totalEl) totalEl.textContent = fmtKR(tot) + '원';
        if (sQty) sQty.textContent = `수량 ${q}`;
        if (sTot) sTot.textContent = fmtKR(tot) + '원';

        // 적립 포인트(1%) 계산 및 반영
        const earnPerUnit = Math.floor(priceUnit * 0.01);
        const earn = earnPerUnit * q;
        if (earnEl)     earnEl.textContent     = fmtKR(earn) + 'P';
        if (stickyEarn) stickyEarn.textContent = '+' + fmtKR(earn) + 'P 적립';

        // cart 버튼의 data-qty 동기화
        document.querySelectorAll('[data-act="cart"]').forEach((b) => (b.dataset.qty = String(q)));
    }

    // 최초 렌더
    updateTotals();

    // 이벤트 바인딩
    document.querySelector('[data-qty-dec]')?.addEventListener('click', () => setQty(getQty() - 1));
    document.querySelector('[data-qty-inc]')?.addEventListener('click', () => setQty(getQty() + 1));
    qtyInput?.addEventListener('change', () => setQty(qtyInput.value));
    qtyInput?.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') { e.preventDefault(); updateTotals(); }
    });

    /* ===== 3) 스크롤 리빌 ===== */
    const toReveal = Array.from(document.querySelectorAll('.book-detail, .info-card, .price-box, .tag-list, .accordion .acc'));
    toReveal.forEach((el) => el.classList.add('reveal'));
    const io = new IntersectionObserver(
        (ents) => {
            ents.forEach((ent) => {
                if (ent.isIntersecting) {
                    ent.target.classList.add('is-in');
                    io.unobserve(ent.target);
                }
            });
        },
        { threshold: 0.08 }
    );
    toReveal.forEach((el) => io.observe(el));

    /* ===== 4) 하단 고정 구매바 표시 조건 ===== */
    if (stickyBar) {
        const anchor = document.querySelector('.actions-grid') || document.querySelector('.price-box');
        if (anchor) {
            const io2 = new IntersectionObserver(
                (ents) => {
                    const visible = ents[0].isIntersecting;
                    stickyBar.hidden = visible; // 액션 영역 보이면 숨김
                },
                { threshold: 0.05 }
            );
            io2.observe(anchor);
        } else {
            stickyBar.hidden = false;
        }
    }

    /* ===== 5) Sticky 구매 버튼 -> 기존 buyNowBtn 클릭 재사용 ===== */
    document.getElementById('buyNowBtnSticky')?.addEventListener('click', () => {
        document.getElementById('buyNowBtn')?.click();
    });

    /* ===== 6) 위시리스트 미세 애니메이션 ===== */
    const wishBtn = document.getElementById('btnWishlist');
    wishBtn?.addEventListener('click', () => {
        wishBtn.classList.add('wiggle');
        setTimeout(() => wishBtn.classList.remove('wiggle'), 500);
    });

    /* ===== 7) 키보드 접근성 ===== */
    document.querySelectorAll('[data-act], .qty-btn').forEach((el) => {
        el.setAttribute('tabindex', '0');
        el.addEventListener('keydown', (e) => {
            if (e.key === ' ' || e.key === 'Enter') {
                e.preventDefault();
                el.click();
            }
        });
    });
})();

/* ===== 8) 통계 바 애니메이션 ===== */
const statSec = document.querySelector('.stats-section');
if (statSec) {
    const bars = statSec.querySelectorAll('.bar-fill');
    bars.forEach(b => (b.style.width = '0'));  // 초기 0%

    const io3 = new IntersectionObserver((ents) => {
        if (!ents[0].isIntersecting) return;
        bars.forEach(b => {
            const p = b.dataset.p || getComputedStyle(b).getPropertyValue('--w') || '0%';
            b.style.width = p.trim();
        });
        io3.disconnect();
    }, { threshold: 0.2 });
    io3.observe(statSec);
}

