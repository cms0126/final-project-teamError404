// orders.js (권장: mypage-common.js 다음, cart.js 이전에 로드)
window.Orders = (() => {

    const fmtKR = window.fmtKR ?? (window.fmtKR = function (n) {
        const num = Number(n || 0);
        try {
            return new Intl.NumberFormat('ko-KR').format(num);
        } catch {
            return String(num);
        }
    });

    const DEFAULT_ORDERS_URL = "/mypage?tab=orders";
    let _afterPay = null;

    function setAfterPayRedirect(fnOrUrl) { _afterPay = fnOrUrl; }

    function goAfterPay() {
        if (typeof _afterPay === "function") { _afterPay(); return; }
        if (typeof _afterPay === "string" && _afterPay) { location.href = _afterPay; return; }

        const attr = document.querySelector("[data-after-pay]")?.getAttribute("data-after-pay")
            || document.body?.dataset?.afterPay || "";

        if (attr === "stay") return;
        if (attr && (/^https?:|^\//.test(attr))) { location.href = attr; return; }

        const ordersTabBtn = document.querySelector("#v-pills-orders-tab, #orders-tab");
        if (ordersTabBtn && window.bootstrap?.Tab) { new bootstrap.Tab(ordersTabBtn).show(); return; }

        location.href = DEFAULT_ORDERS_URL;
    }

        // ===== 페이징 상태 =====
        let _page = 0;          // 0-based
        let _size = 4;          // 페이지당 개수 (원하면 바꿔)
        let _totalPages = 1;
        let _cacheAll = null;   // 서버가 배열만 주면 여기에 캐싱해서 클라 페이징

    /** ========== 1) 주문 내역 SPA 렌더 ========== */
    async function refreshOrders(page = _page) {
        _page = page; // 최신페이지 유지
        const ordersContainer = document.querySelector('#orders-root');
        try {
            const url = `/orders/user?page=${encodeURIComponent(_page)}&size=${encodeURIComponent(_size)}`;
            const res = await fetch(url, {
                method: 'GET',
                headers: window.getCsrfHeaders?.(false) || ({'X-CSRF-TOKEN': window.csrfToken}),
                credentials: 'include'
            });
            const raw = await res.json();

            window.hideById && window.hideById('orders-skeleton');

            let data = [];
            if (raw && Array.isArray(raw.content)){
                data = raw.content;
                _totalPages = Math.max(1, raw.totalPages ?? 1);
                _page = raw.number ?? _page;
                _size = raw.size ?? _size;
                _cacheAll = null;
            } else if (Array.isArray(raw)){
                _cacheAll = raw;
                _totalPages = Math.max(1, Math.ceil(raw.length / _size));
                const  start = _page * _size;
                data = raw.slice(start, start + _size);
            }else{
                console.warn('[orders] unexpected response shape:', raw);
                data = [];
                _totalPages =1;
                _page = 0;
            }

            if (!data || !data.length) {
                ordersContainer.innerHTML = `<div class="alert alert-info">주문 내역이 없습니다.</div>`;
                renderPagination();
                return;
            }

            let html = ``;

            data.forEach(order => {
                // 👉 아이템 배열 안전화
                const items = Array.isArray(order.items) ? order.items : [];
                // 👉 대표 도서(첫 번째 아이템) 추출 (없으면 빈 객체)
                const firstItem = items[0] || {};
                const firstTitle = window.escapeHtml(firstItem.title || '주문');
                const thumbUrl = window.escapeHtml(firstItem.imageUrl || '');
                const totalItems = items.length;
                const createdAt = (order.createdAt || '').substring(0, 10);
                // 👉 총액 필드 안전 처리
                const grand = (order.totalPrice ?? order.paidAmount ?? order.itemsTotal ?? 0);

                // 카드 요약 + 아코디언
                html += `
                <div class="card mb-3 shadow-sm">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <img src="${thumbUrl}" alt="${firstTitle}"
                                 style="width:60px; height:85px; object-fit:cover; border-radius:4px; margin-right:10px;">
                            <div class="flex-grow-1">
                                <div class="fw-bold">${firstTitle}${totalItems > 1 ? ` 외 ${totalItems - 1}권` : ''}</div>
                                <div class="small text-muted">주문일: ${window.escapeHtml((order.createdAt || '').substring(0, 10))}</div>
                               <div class="fw-bold text-primary">결제 금액: ${window.fmtKR(grand)} 원</div>
                            </div>
                            <div>
                                ${renderOrderStatus(order)}
                            </div>
                        </div>

                        <!-- 아코디언 상세 -->
                        <div class="accordion mt-3" id="orderAccordion-${order.orderId}">
                            <div class="accordion-item">
                                <h2 class="accordion-header" id="heading-${order.orderId}">
                                    <button class="accordion-button collapsed" type="button"
                                            data-bs-toggle="collapse"
                                            data-bs-target="#collapse-${order.orderId}"
                                            aria-expanded="false"
                                            aria-controls="collapse-${order.orderId}">
                                        📦 주문 상세 보기
                                    </button>
                                </h2>
                                <div id="collapse-${order.orderId}" class="accordion-collapse collapse"
                                     aria-labelledby="heading-${order.orderId}"
                                     data-bs-parent="#orderAccordion-${order.orderId}">
                                    <div class="accordion-body">
                                        <div class="text-muted small mb-2">
                                            주문번호: ${window.escapeHtml(order.merchantUid)} <br>
                                            주문일: ${window.escapeHtml((order.createdAt || '').substring(0, 10))}
                                        </div>
                                           ${items.map(item => {
                    const discountRate = item.price && item.discountedPrice
                        ? Math.round((item.price - item.discountedPrice) / item.price * 100)
                        : 0;
                    return `
                                                <div class="mb-3">
                                                    <div>
                                                         <strong>${window.escapeHtml(item.title)}</strong>
                                                        <span class="text-muted ms-1">(${item.quantity}권)</span>
                                                    </div>
                                                    <div class="small text-muted">
                                                        ${discountRate > 0
                        ? `정가: <span class="text-decoration-line-through">${window.fmtKR(item.price)}원</span>
                                                               <span class="badge bg-danger ms-2">${discountRate}% 할인</span>`
                        : `정가: ${window.fmtKR(item.price)}원`}
                                                    </div>
                                                    <div class="fw-bold text-success">
                                                        가격: ${window.fmtKR(item.discountedPrice || item.price)}원
                                                    </div>
                                                </div>
                                            `;
                }).join("")}
                                        <div class="fw-bold text-primary mt-2">
                                            결제 금액: ${window.fmtKR(grand)} 원
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            `;
            });

            ordersContainer.innerHTML = html;
            renderPagination();

        } catch (err) {
            console.error("❌ 주문내역 갱신 실패:", err);
            window.hideById && window.hideById('orders-skeleton');
                        const root = document.querySelector('#orders-root');
                        if (root) root.innerHTML = `<div class="alert alert-danger">주문 내역을 불러오지 못했습니다.</div>`;
                        renderPagination();
        }
    }


    /** 상태별 뱃지/버튼 렌더 (HTML 문자열 반환) */
    function renderOrderStatus(order) {
        const id = order.orderId;
        switch (order.status) {
            case 'PAID':
                return `
          <span class="badge bg-primary">결제 완료</span>
            <button class="btn btn-sm btn-outline-danger ms-2"
              onclick="Orders.cancelPay(${id})">결제 취소</button>`;
            case 'CANCELLED':
                return `<span class="badge bg-secondary">취소됨</span>`;
            case 'SHIPPED':
                return `<span class="badge bg-info text-dark">배송중</span>`;
            case 'DELIVERED':
                return `
          <span class="badge bg-success">배송완료</span>
          <button class="btn btn-sm btn-outline-primary ms-2"
            onclick="writeReview(${id})">리뷰쓰기</button>`;
            default:
                return `<span class="badge bg-light text-dark">알 수 없음</span>`;
        }
    }

    /** ========== 2) 주문 상태 변경/삭제 ========== */
    async function updateOrderStatus(orderId, newStatus) {
        let confirmMsg = "";
        let successMsg = "주문 상태가 변경되었습니다.";

        if (newStatus === 'REFUND_REQUEST') {
            confirmMsg = "환불을 신청하시겠습니까?";
            successMsg = "환불 신청이 접수되었습니다.";
        }

        if (confirmMsg && !confirm(confirmMsg)) return;

        try {
            const res = await fetch(`/orders/${orderId}/status?status=${encodeURIComponent(newStatus)}`, {
                method: 'PATCH',
                headers: {
                    'X-CSRF-TOKEN': window.csrfToken,
                    'Content-Type': 'application/json'
                }
            });
            if (!res.ok) throw new Error("상태 변경 실패");
            const data = await res.json();
            console.log("✅ 주문상태 변경 성공:", data);
            if (successMsg) alert(successMsg);
            await refreshOrders();
        } catch (err) {
            console.error("❌ 상태 변경 오류:", err);
        }
    }

    async function removeOrder(orderId) {
        if (!confirm("정말 주문을 취소하시겠습니까?")) return;

        try {
            const res = await fetch(`/orders/${orderId}`, {
                method: 'DELETE',
                headers: {'X-CSRF-TOKEN': window.csrfToken}
            });
            const data = await res.json();
            if (res.ok && data.status === "ok") {
                alert(data.message || "주문이 취소되었습니다.");
                await refreshOrders();
            } else {
                alert("❌ 취소 실패: " + (data.message || ''));
            }
        } catch (err) {
            console.error("취소 요청 실패:", err);
        }
    }

    async function cancelPay(orderId) {
        if (!confirm("정말 결제를 취소하시겠습니까?")) return;

        const reason = prompt("취소 사유(선택):") || "";

        const headers = Object.assign({ 'Accept': 'application/json' }, window.getCsrfHeaders?.(false) || {});

        try {
            const res = await fetch(`/payments/cancel/${orderId}?reason=${encodeURIComponent(reason)}`, {
                method: 'POST',
                headers,
                credentials: 'include', // 로그인 세션 쿠키 포함!
            });

            const data = await res.json().catch(() => ({}));

            if (!res.ok) {
                alert(data.message || '취소 실패');
                return;
            }
            alert(data.message || '결제가 정상적으로 취소되었습니다.');
            await refreshOrders();
        } catch (e) {
            console.error('❌ 결제 취소 요청 실패:', e);
            alert('네트워크 오류로 취소에 실패했습니다.');
        }
    }

    /** ========== 3) 결제 모달 → prepare → 결제 → verify ========== */

    /** 모달 열기: 총액 세팅, 확인버튼 바인딩 */
    function openOrderInfoModal(total) {

        const latestEl = document.querySelector("#cart-total");
        if (latestEl) {
            const latestAmount = parseInt(latestEl.textContent.replace(/[^0-9]/g, ""))
            total = latestAmount;
        }

        const totalEl = document.getElementById('oiTotal');
        if (totalEl) totalEl.textContent = fmtKR(total);

        const orderIdView = document.getElementById('f-orderId');
        if (orderIdView) orderIdView.value = '';

        const modal = new bootstrap.Modal('#orderInfoModal');
        modal.show();

        const confirmBtn = document.getElementById('oi-confirm');
        confirmBtn.onclick = async () => {
            confirmBtn.disabled = true;
            try {
                const prep = await preparePay(total);
                if (orderIdView) orderIdView.value = prep.merchantUid || '';
                await requestDemoPay(total, prep);
            } catch (e) {
                console.error(e);
                alert('결제 준비 중 오류가 발생했습니다.');
            } finally {
                confirmBtn.disabled = false;
            }
        };
    }

    /** 폼 값 수집 (모달 입력/hidden 기준) */
    function getOrderFormValues() {
        const f = document.getElementById('orderForm');
        return {
            userId: (f.userId?.value || '').trim(),
            userName: (f.userName?.value || '').trim(),
            email: (f.email?.value || '').trim(),
            phone: (f.phone?.value || '').trim(),
            recipient: (f.recipient?.value || '').trim(),
            address: (f.address?.value || '').trim(),
            memo: (f.memo?.value || '').trim(),
            paymentMethod: (f.paymentMethod?.value || 'card'),
            mode: (f.mode?.value || 'CART'),
            bookId: (f.bookId?.value || '').trim(),
            quantity: Number(f.quantity?.value || '1')
        };
    }

    /** prepare: merchantUid 발급 */
    async function preparePay(total) {
        const headers = {'Content-Type': 'application/json'};
        if (window.csrfToken) headers['X-CSRF-TOKEN'] = window.csrfToken;

        const res = await fetch('/payments/prepare', {
            method: 'POST',
            headers,
            body: JSON.stringify({amount: Number(total)})
        });
        if (!res.ok) throw new Error('prepare 실패');
        return res.json(); // { merchantUid }
    }

    /** 결제/검증 */
    async function requestDemoPay(total, prep /* { merchantUid } */) {
        const o = getOrderFormValues();

        const oiTotalEl = document.getElementById("oiTotal");
        if (oiTotalEl) {
            total = parseInt(oiTotalEl.textContent.replace(/[^0-9]/g, ""), 10);
        }
        const IMP = window.IMP;
        if (!IMP) {
            alert('결제 모듈 초기화에 실패했습니다. 새로고침 후 다시 시도해 주세요.');
            return;
        }

        const payReq = {
            pg: 'html5_inicis.INIpayTest',
            pay_method: 'card',
            merchant_uid: prep.merchantUid,
            name: '장바구니 결제',
            amount: Number(total),
            buyer_name: o.recipient,
            buyer_tel: o.phone,
            buyer_email: o.email,
            buyer_addr: o.address,
            custom_data: JSON.stringify({memo: o.memo, address: o.address}),
            m_redirect_url: location.origin + '/payments/verify-redirect'
        };

        IMP.request_pay(payReq, async (rsp) => {
            if (!rsp.success) {
                alert('결제 실패: ' + (rsp.error_msg || ''));
                return;
            }

            const payload = {
                impUid: rsp.imp_uid,
                merchantUid: rsp.merchant_uid,
                ordersInfo: {
                    userId: o.userId,
                    recipient: o.recipient,
                    phone: o.phone,
                    address: o.address,
                    memo: o.memo,
                    payMethod: 'card',
                    amount: Number(total),
                    mode: o.mode,
                    bookId: o.bookId ? Number(o.bookId) : null,
                    quantity: Number(o.quantity || 1)
                }
            };

            const headers = {'Content-Type': 'application/json'};
            if (window.csrfToken) headers['X-CSRF-TOKEN'] = window.csrfToken;

            const res = await fetch('/payments/verify', {
                method: 'POST',
                headers,
                body: JSON.stringify(payload)
            });

            const raw = await res.text();
            try {
                const data = JSON.parse(raw);
                if (data.status === 'PAID') {
                    alert('결제 완료');
                    if (window.refreshCart) window.refreshCart();
                    await refreshOrders();

                    //  모달 닫기
                    const modalEl = document.getElementById("orderInfoModal");
                    if (modalEl) {
                        const modal = bootstrap.Modal.getInstance(modalEl);
                        if (modal) modal.hide();
                    }

                    goAfterPay();

                    // 주문 탭으로 자동 전환(선택)
                    const ordersTabBtn = document.querySelector('#v-pills-orders-tab');
                    if (ordersTabBtn && window.bootstrap?.Tab) new bootstrap.Tab(ordersTabBtn).show();
                } else {
                    alert('검증 실패: ' + (data.message || ''));
                }
            } catch {
                console.error('VERIFY RAW:', raw);
                alert('서버 응답 파싱 실패(verify). 콘솔 확인');
            }
        });
    }

    /** 선택: 초기 마운트(필요 시 호출) */
    function mount() {
        const tab = document.getElementById('v-pills-orders-tab');
        if (tab) tab.addEventListener('shown.bs.tab', refreshOrders);
    }

    // 페이지네이션 렌더
    function renderPagination() {
        let pager = document.getElementById('orders-pagination');
        if (!pager){
            pager = document.createElement('ul');
            pager.id = 'orders-pagination';
            pager.className = 'pagination pagination-sm justify-content-center mt-3';
            const root = document.getElementById('orders-root');
            if (root && root.parentNode) root.parentNode.insertBefore(pager, root.nextSibling);
        }
        // totalPages 1이면 비움
                if (_totalPages <= 1) { pager.innerHTML = ''; return; }

                    const mk = (label, targetPage, active = false, disabled = false, aria = '') => {
                        const li = document.createElement('li');
                        li.className = `page-item${active ? ' active' : ''}${disabled ? ' disabled' : ''}`;
                        const a = document.createElement('a');
                        a.className = 'page-link';
                        a.href = '#';
                        a.textContent = label;
                        if (aria) a.setAttribute('aria-label', aria);
                        a.onclick = (e) => {
                                e.preventDefault();
                                if (disabled || targetPage === _page) return;
                                refreshOrders(targetPage);
                                // 스크롤 살짝 올려주기(선택)
                                    document.getElementById('v-pills-orders')?.scrollIntoView({behavior:'smooth', block:'start'});
                            };
                        li.appendChild(a);
                        return li;
                    };

                    // 간단한 « ‹ 1 2 3 › »
                        const ul = document.createDocumentFragment();
                const isFirst = (_page === 0);
                const isLast = (_page >= _totalPages - 1);
                ul.appendChild(mk('«', 0, false, isFirst, 'First'));
                ul.appendChild(mk('‹', Math.max(0, _page - 1), false, isFirst, 'Previous'));

                    // 최대 5개 윈도우
                        const span = 2;
                let start = Math.max(0, _page - span);
                let end = Math.min(_totalPages - 1, _page + span);
                // 채우기
                    while ((end - start) < 4 && end < _totalPages - 1) end++;
                while ((end - start) < 4 && start > 0) start--;
                for (let p = start; p <= end; p++) {
                        ul.appendChild(mk(String(p + 1), p, p === _page, false, 'page'));
                    }

                    ul.appendChild(mk('›', Math.min(_totalPages - 1, _page + 1), false, isLast, 'Next'));
                ul.appendChild(mk('»', _totalPages - 1, false, isLast, 'Last'));
                pager.innerHTML = '';
                pager.appendChild(ul);
            }

    // 공개 API
    return {
        refreshOrders,
        renderOrderStatus,
        updateOrderStatus,
        removeOrder,
        getOrderFormValues,
        openOrderInfoModal,
        requestDemoPay,
        preparePay,
        cancelPay,
        mount,
        setAfterPayRedirect
    };
})();

// 무조건 js 한번은 노출
document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('orders-root')) Orders.refreshOrders();
});
