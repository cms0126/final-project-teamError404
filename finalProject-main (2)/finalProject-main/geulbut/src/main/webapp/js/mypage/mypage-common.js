/* =========================
 * mypage-common.js (공통)
 * ========================= */

/** HTML 이스케이프 (전역) */
window.escapeHtml = function (s) {
    return String(s ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
};

/** 한국어 통화/숫자 포맷 (전역) */
window.fmtKR = function (n) {
    const v = Number(n);
    return Number.isFinite(v) ? v.toLocaleString('ko-KR') : '0';
};

/** 공통: CSRF 헤더 빌더 (json=true면 Content-Type 추가) */
    window.getCsrfHeaders = function (json = false) {
        const headers = {};
        if (json) headers['Content-Type'] = 'application/json';
        // JSP 전역값 우선
            if (window.csrfHeaderName && window.csrfToken) {
                headers[window.csrfHeaderName] = window.csrfToken;
            } else {
                // 메타태그 fallback
                const tk = document.querySelector('meta[name="_csrf"]')?.content;
                const hn = document.querySelector('meta[name="_csrf_header"]')?.content;
                if (tk && hn) headers[hn] = tk;
            }
        return headers;
    };

    /** 공통: 엘리먼트 숨기기 */
        window.hideById = function (id) {
        const el = document.getElementById(id);
        if (el) el.style.display = 'none';
    };

/** PortOne(아임포트) init (전역) — SDK 지연 대비, 1회 보장 */
(function initPortOneOnce() {
    let tried = false;
    function tryInit() {
        if (tried) return true;
        const root = document.getElementById('imp-root');
        const impCode = (window.IMP_CODE) || (root && root.dataset.impCode);
        const IMP = window.IMP;

        if (!IMP) return false;                 // SDK 아직
        if (!impCode) {                         // 코드 없음 → 재시도 의미 없음
            console.warn('[IMP] imp_code missing. JSP에서 imp_code 주입 필요');
            return true;
        }
        try {
            IMP.init(impCode);
            console.log('[IMP:init] OK with', impCode);
            tried = true;
            return true;
        } catch (e) {
            console.error('[IMP:init] failed', e);
            return true;
        }
    }

    if (tryInit()) return;
    let count = 0;
    const t = setInterval(() => {
        count++;
        if (tryInit() || count >= 20) clearInterval(t); // 최대 2초 대기
    }, 100);
})();


/* 페이지 로드 시 탭 초기화 & 훅 */
document.addEventListener("DOMContentLoaded", function () {
    const tabMap = {
        wishlist: "#v-pills-wishlist",
        cart: "#v-pills-cart",
        orders: "#v-pills-orders",
        info: "#v-pills-info"
    };

    const params = new URLSearchParams(window.location.search);
    let targetId = null;

    if (params.has("tab") && tabMap[params.get("tab")]) targetId = tabMap[params.get("tab")];
    if (!targetId && window.location.hash) targetId = window.location.hash;

    if (targetId) {
        const triggerEl = document.querySelector(`button[data-bs-target="${targetId}"]`);
        if (triggerEl) new bootstrap.Tab(triggerEl).show();
    }

    // 장바구니 탭 전환 시 항상 최신화
    const cartTabBtn = document.querySelector('#v-pills-cart-tab');
    if (cartTabBtn && window.refreshCart) {
        cartTabBtn.addEventListener('shown.bs.tab', () => {
            console.log("📌 [DEBUG] 장바구니 탭 전환 → refreshCart 실행");
            window.refreshCart();
        });
    }

    // 주문 탭 전환 시 최신화
    const ordersTabBtn = document.querySelector('#v-pills-orders-tab');
    if (ordersTabBtn && window.Orders?.refreshOrders) {
        ordersTabBtn.addEventListener('shown.bs.tab', () => {
            console.log("📌 [DEBUG] 주문 탭 전환 → Orders.refreshOrders 실행");
            window.Orders.refreshOrders();
        });
    }
        const activePane = document.querySelector('.tab-pane.show.active');
        if (activePane?.id === 'v-pills-orders' && window.Orders?.refreshOrders) {
                window.Orders.refreshOrders();
        }
});


    document.addEventListener('DOMContentLoaded', () => {
    // 1) "내 정보" 탭 강제 활성화
        if (window.forceChangePw !== true) return;

        const focusPw = () => {
            const cur = document.getElementById('currentPw');
            if (cur) { cur.scrollIntoView({behavior:'smooth', block:'center'}); cur.focus(); }
            const form = document.querySelector('form[action$="/mypage/change-password"]');
            if (form) {
                form.classList.add('border','border-warning','rounded-3');
                setTimeout(() => form.classList.remove('border','border-warning','rounded-3'), 3000);
            }
        };

    // 2) 비밀번호 변경 섹션으로 스크롤 + 현재 비번 입력창 포커스
        const btn = document.getElementById('v-pills-info-tab');
        if (btn && window.bootstrap?.Tab && !btn.classList.contains('active')) {
            btn.addEventListener('shown.bs.tab', focusPw, {once:true});
            new bootstrap.Tab(btn).show();
        } else {
            focusPw();
        }
    });

(function () {
    'use strict';

    // 로컬스토리지 키
    const LS_KEY_TOTAL = 'mypage.lastTotalPurchase';
    const LS_KEY_PCT   = 'mypage.lastProgressPct';

    // ---- 유틸 ----
    function formatKRW(n) {
        return (n || 0).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    }

    function countUp(el, from, to, ms) {
        if (!el) return;
        const start = performance.now();
        const dur = Math.max(400, ms || 900);
        const diff = (to - from) || 0;

        function frame(t) {
            const p = Math.min(1, (t - start) / dur);
            const ease = 1 - Math.pow(1 - p, 3);
            el.textContent = formatKRW(Math.round(from + diff * ease));
            if (p < 1) requestAnimationFrame(frame);
        }
        requestAnimationFrame(frame);
    }

    function animateBar(fillEl, fromPct, toPct) {
        if (!fillEl) return;
        fillEl.style.transition = 'none';
        fillEl.style.width = (fromPct || 0) + '%';
        // reflow
        void fillEl.offsetWidth;
        fillEl.style.transition = 'width .9s ease-in-out';
        fillEl.style.width = Math.max(0, Math.min(100, toPct || 0)) + '%';
    }

    function tierOf(total) {
        if (total >= 300000) return 'GOLD';
        if (total >= 100000) return 'SILVER';
        return 'BRONZE';
    }

    function confettiBurst() {
        const N = 80;
        for (let i = 0; i < N; i++) {
            const c = document.createElement('div');
            c.className = 'confetti';
            c.style.left = (Math.random() * 100) + 'vw';
            c.style.background = `hsl(${Math.random() * 360},90%,60%)`;
            const rot = (Math.random() * 360 | 0);
            c.style.transform = `rotate(${rot}deg)`;
            document.body.appendChild(c);

            const fall = (8 + Math.random() * 8) * 1000;
            const sx = (Math.random() * 2 - 1) * 200; // 좌우 흔들

            c.animate([
                { transform: `translate(0,0) rotate(${rot}deg)`, opacity: .95 },
                { transform: `translate(${sx}px, 100vh) rotate(${rot + 720}deg)`, opacity: .1 }
            ], { duration: fall, easing: 'cubic-bezier(.2,.9,.2,1)', fill: 'forwards' })
                .onfinish = () => c.remove();
        }
    }

    // URL ?tab=wishlist|cart|orders|info 로 탭 열기 (옵션)
    function initTabFromQuery() {
        const param = new URLSearchParams(location.search).get('tab');
        if (!param) return;
        const btn = document.getElementById(`v-pills-${param}-tab`);
        if (btn && typeof btn.click === 'function') btn.click();
    }

    // 메인: 게이미피케이션 카드 초기화
    function initGamifyCard() {
        const card = document.getElementById('gp-card');
        if (!card) return; // 카드 없으면 그냥 종료

        const total = Number(card.dataset.total || 0);
        const pct   = Number(card.dataset.progress || 0);

        const totalEl = document.getElementById('gp-total');
        const fillEl  = document.getElementById('gp-fill');

        // 이전 값(비교용)
        const prevTotal = Number(localStorage.getItem(LS_KEY_TOTAL) || 0);
        const prevPct   = Number(localStorage.getItem(LS_KEY_PCT) || 0);

        // 애니메이션
        countUp(totalEl, prevTotal || 0, total, 900);
        animateBar(fillEl, prevPct || 0, pct);

        // 레벨업 감지 → 콘페티
        const beforeTier = tierOf(prevTotal);
        const afterTier  = tierOf(total);
        if (afterTier !== beforeTier && (afterTier === 'SILVER' || afterTier === 'GOLD')) {
            card.classList.add('shadow-lg');
            confettiBurst();
            setTimeout(() => card.classList.remove('shadow-lg'), 1200);
        }

        // 저장
        localStorage.setItem(LS_KEY_TOTAL, String(total));
        localStorage.setItem(LS_KEY_PCT,   String(pct));
    }

    function init() {
        initTabFromQuery();  // 옵션: URL 파라미터로 탭 열기
        initGamifyCard();    // 게이미피케이션 카드
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();

