
(function () {
    'use strict';

    // ---- 전역 유틸 폴백 ----
    const fmtKR = (window.fmtKR) ? window.fmtKR : (n => {
        const v = Number(n); return Number.isFinite(v) ? v.toLocaleString('ko-KR') : '0';
    });
    const escapeHtml = (window.escapeHtml) ? window.escapeHtml : (s => String(s ?? '')
        .replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;')
        .replaceAll('"','&quot;').replaceAll("'","&#39;"));

    function addOnce(el, type, fn, opts){ if(!el) return; let used=false; el.addEventListener(type, e=>{ if(used) return; used=true; fn(e); }, opts||{once:true});}

    // ---- 상태 뱃지 클래스 매핑 ----
    const statusClassMap = {
        'DELIVERED': ['status','status--done'],
        'IN_TRANSIT': ['status','status--in'],
        'READY': ['status','status--ready']
    };

    function applyStatusPill() {
        const card = document.getElementById('currentCard');
        const pill = document.getElementById('statusPill');
        if (!card || !pill) return;

        const st = (card.dataset.status || '').toUpperCase();
        const classes = statusClassMap[st] || statusClassMap['READY'];
        pill.className = classes.join(' ');

        // 상태 텍스트 보정
        const label = (st === 'DELIVERED') ? '배송완료' : (st === 'IN_TRANSIT') ? '배송중' : '배송준비';
        pill.textContent = label;

        // 마이크로 모션: 배송완료 진입 시 살짝 강조
        if (st === 'DELIVERED') {
            card.classList.add('shadow-lg');
            setTimeout(()=>card.classList.remove('shadow-lg'), 1000);
            // 공통 confetti가 있으면 사용
            if (typeof window.confettiBurst === 'function') window.confettiBurst();
        }
    }

    // ---- 히스토리 행 상호작용(키보드/클릭) ----
    function bindHistoryList() {
        const list = document.getElementById('historyList');
        if (!list) return;

        list.querySelectorAll('.history-row').forEach(li => {
            const href = li.dataset.href;
            if (!href) return;

            // 행 어디를 눌러도 이동
            li.addEventListener('click', (e) => {
                // a 클릭은 기본 동작 유지, 행 빈 영역 클릭 시 링크로 이동
                if (e.target && e.target.closest('a')) return;
                window.location.href = href;
            });

            // 키보드 접근성
            li.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    window.location.href = href;
                }
            });

            // 총액 숫자 보정(혹시 서버 포맷 누락 대비)
            const sumEl = li.querySelector('.h-col.sum');
            if (sumEl && sumEl.dataset.sum) {
                sumEl.textContent = fmtKR(sumEl.dataset.sum) + '원';
            }
        });
    }

    // ---- 현재 주문 아이템 썸네일 폴백/숫자 포맷 안정화 ----
    function polishCurrentItems() {
        const ul = document.getElementById('currentItems');
        if (!ul) return;
        ul.querySelectorAll('.order-item').forEach(li => {
            // 썸네일 누락 시 placehoder가 이미 들어가지만, alt/클래스 보정
            const img = li.querySelector('img.oi-thumb');
            if (img && !img.getAttribute('alt')) img.setAttribute('alt', '상품 이미지');

            // 숫자 포맷 폴백(서버 포맷돼 오지만 혹시 대비)
            const u = li.querySelector('.oi-meta .u');
            const s = li.querySelector('.oi-meta .s');
            if (u && u.textContent && !u.textContent.includes(',')) {
                const n = Number(u.textContent.trim()); if (Number.isFinite(n)) u.textContent = fmtKR(n);
            }
            if (s && s.textContent && !s.textContent.includes(',')) {
                const n = Number(s.textContent.trim()); if (Number.isFinite(n)) s.textContent = fmtKR(n);
            }
        });
    }

    // ---- 섹션 포커스 스크롤 (?focus=history|current) ----
    function focusByQuery() {
        const q = new URLSearchParams(location.search).get('focus');
        const targetId = (q === 'history') ? 'historyCard' : (q === 'current') ? 'currentCard' : null;
        if (!targetId) return;
        const el = document.getElementById(targetId);
        if (!el) return;
        el.scrollIntoView({behavior:'smooth', block:'start'});
        addOnce(el, 'transitionend', ()=>{ try{ el.focus(); }catch(_){ }});
        // 시각 강조
        el.classList.add('focus-ring');
        setTimeout(()=>el.classList.remove('focus-ring'), 1200);
    }

    // ---- 초기화 ----
    function init() {
        applyStatusPill();
        bindHistoryList();
        polishCurrentItems();
        focusByQuery();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();

