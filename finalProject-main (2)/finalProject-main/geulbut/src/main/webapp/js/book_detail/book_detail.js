// /js/book_detail/book_detail.wired.js
console.log('[book_detail] wired: wishlist + cart + buy-now (form-encoded + confirm)');



(function () {
    /* ===== 0) 엔드포인트 ===== */
    const CTX = (typeof window.CONTEXT_PATH !== 'undefined'
        ? window.CONTEXT_PATH
        : (typeof pageContext !== 'undefined' && pageContext?.request?.contextPath) || '');

    const URLS = {
        wishlistAdd: CTX + '/wishlist',
        cartAdd:     CTX + '/cart',
        buyNow:      CTX + '/orders/buy-now',
        checkout:    CTX + '/orders/checkout',
        login:       CTX + '/users/login'
    };

    /* ===== 1) CSRF ===== */
    const CSRF_TOKEN  = document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || null;
    const CSRF_HEADER = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-CSRF-TOKEN';
    function buildHeaders(isForm = true) {
        const h = new Headers();
        if (isForm) h.set('Content-Type', 'application/x-www-form-urlencoded;charset=UTF-8');
        if (CSRF_TOKEN) h.set(CSRF_HEADER, CSRF_TOKEN);
        return h;
    }

    async function postForm(url, paramsObj) {
        const body = new URLSearchParams();
        Object.entries(paramsObj || {}).forEach(([k, v]) => body.append(k, String(v)));
        const res = await fetch(url, { method: 'POST', headers: buildHeaders(true), body });

        if (res.status === 401) { location.href = URLS.login; return null; }

        let data = null;
        try { data = await res.clone().json(); } catch (_) {}
        if (!res.ok) {
            const msg = data?.message || data?.error || `요청 실패 (${res.status})`;
            throw new Error(msg);
        }
        return data;
    }

    /* ===== 2) 토스트 ===== */
    function toast(msg) {
        let t = document.getElementById('_toast');
        if (!t) {
            t = document.createElement('div');
            t.id = '_toast';
            Object.assign(t.style, {
                position: 'fixed', left: '50%', bottom: '28px', transform: 'translateX(-50%)',
                padding: '10px 14px', borderRadius: '10px', background: 'rgba(0,0,0,.78)',
                color: '#fff', fontWeight: '600', zIndex: '9999', transition: 'opacity .25s ease'
            });
            document.body.appendChild(t);
        }
        t.textContent = msg;
        t.style.opacity = '1';
        setTimeout(() => (t.style.opacity = '0'), 1400);
    }

    /* ===== 3) confirm 메세지 ===== */
    const CONFIRM_MSG = {
        like: '위시리스트에 담으시겠습니까?',
        cart: '장바구니에 담으시겠습니까?',
        buy:  '구매하시겠습니까?'
    };
    function ask(act) {
        const msg = CONFIRM_MSG[act];
        return msg ? window.confirm(msg) : true;
    }

    /* ===== 4) 도우미 ===== */
    function readQty(btn) {
        const dataQty = Number(btn?.dataset?.qty);
        if (Number.isFinite(dataQty) && dataQty > 0) return dataQty;
        const input = document.getElementById('qtyInput');
        const v = Number(input?.value);
        return Number.isFinite(v) && v > 0 ? v : 1;
    }
    function readBookId(btn) {
        const d = btn?.dataset?.id;
        if (d) return Number(d);
        if (window.PRODUCT?.id) return Number(window.PRODUCT.id);
        const hid = document.querySelector('input[type="hidden"][name="bookId"]');
        if (hid?.value) return Number(hid.value);
        return NaN;
    }

    /* ===== 5) 단건 액션: cart / like / buy ===== */
    document.addEventListener('click', async (e) => {
        const btn = e.target.closest('[data-act]');
        if (!btn) return;

        const act = btn.dataset.act;  // 'cart' | 'like' | 'buy'
        const id  = readBookId(btn);
        const qty = readQty(btn);
        if (!Number.isFinite(id)) { alert('도서 ID가 없습니다.'); return; }

        // ✅ 확인창
        if (!ask(act)) return;

        try {
            if (act === 'like') {
                await postForm(URLS.wishlistAdd, { bookId: id });
                toast('위시리스트에 담았습니다.');
            } else if (act === 'cart') {
                await postForm(URLS.cartAdd, { bookId: id, quantity: qty });
                toast('장바구니에 담았습니다.');
            } else if (act === 'buy') {
                try {
                    const data = await postForm(URLS.buyNow, { bookId: id, quantity: qty });
                    if (data === null) return; // 401
                    if (data?.redirectUrl)      location.href = data.redirectUrl;
                    else if (data?.orderId)     location.href = CTX + '/orders/' + data.orderId;
                    else                        location.href = URLS.checkout;
                } catch (err) {
                    console.warn('[buy-now] fallback:', err?.message);
                    location.href = URLS.checkout;
                }
            }
        } catch (err) {
            alert(err.message || '처리 중 오류가 발생했습니다.');
        }
    });

    /* ===== 6) 엔터키로 장바구니 담기 ===== */
    const qtyInput = document.getElementById('qtyInput');
    if (qtyInput) {
        qtyInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                document.querySelector('[data-act="cart"]')?.click();
            }
        });
    }
})();

document.addEventListener('DOMContentLoaded', () => {
    const buyBtn = document.getElementById('buyNowBtn');
    if (!buyBtn) return;

    buyBtn.addEventListener('click', () => {
        const f = document.getElementById('orderForm');
        if (!f) return;
        // (선택) 구매 확인창
        if (!window.confirm('구매하시겠습니까?')) return;

        f.querySelector('#oiMode').value = 'BUY_NOW';
        f.querySelector('#oiBookId').value = String(window.PRODUCT?.id || '');
        f.querySelector('#oiQty').value    = String(1);



        // ✅ JSP EL 대신 전역 값 사용
        const total = Number(
            (window.PRODUCT?.discountedPrice ?? window.PRODUCT?.price) ?? 0
        );

        if (Number.isNaN(total) || total <= 0) {
            alert('결제 금액을 계산할 수 없습니다.');
            return;
        }

        if (window.Orders?.openOrderInfoModal) {
            Orders.openOrderInfoModal(total);
        } else {
            console.error('Orders.openOrderInfoModal가 없습니다. orders.js 로드 순서를 확인하세요.');
            alert('결제 모듈이 아직 준비되지 않았습니다.');
        }
    });
});