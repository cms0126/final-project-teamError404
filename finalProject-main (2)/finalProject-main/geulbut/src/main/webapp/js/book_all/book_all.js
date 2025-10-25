// ================= Book All: wired actions =================
// - 장바구니/위시리스트 단건 & 일괄
// - 정렬/페이징 보조
// - 최초 진입 카드 페이드업
// ===========================================================

(function () {
    const CTX = (typeof window.CONTEXT_PATH !== 'undefined'
        ? window.CONTEXT_PATH
        : (typeof pageContext !== 'undefined' && pageContext?.request?.contextPath) || '');

    const URLS = {
        wishlistAdd: CTX + '/wishlist',
        cartAdd:     CTX + '/cart',
        login:       CTX + '/users/login'
    };

    // CSRF
    const CSRF_TOKEN  = document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || null;
    const CSRF_HEADER = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-CSRF-TOKEN';
    const buildHeaders = (isForm=true) => {
        const h = new Headers();
        if (isForm) h.set('Content-Type','application/x-www-form-urlencoded;charset=UTF-8');
        if (CSRF_TOKEN) h.set(CSRF_HEADER, CSRF_TOKEN);
        return h;
    };
    async function postForm(url, params){
        const body = new URLSearchParams();
        Object.entries(params||{}).forEach(([k,v]) => body.append(k, String(v)));
        const res = await fetch(url, { method:'POST', headers:buildHeaders(true), body, credentials:'same-origin' });
        if (res.status === 401) { location.href = URLS.login; return null; }
        let data=null; try{ data = await res.clone().json(); }catch(_){}
        if (!res.ok) throw new Error(data?.message || `요청 실패(${res.status})`);
        return data;
    }

    // 토스트
    function toast(msg){
        let t = document.getElementById('_toast'); if (!t){ t = document.createElement('div'); t.id='_toast';
            Object.assign(t.style,{position:'fixed',left:'50%',bottom:'28px',transform:'translateX(-50%)',
                padding:'10px 14px',borderRadius:'10px',background:'rgba(0,0,0,.78)',color:'#fff',fontWeight:'700',zIndex:'9999',transition:'opacity .25s'});
            document.body.appendChild(t);
        }
        t.textContent = msg; t.style.opacity='1'; setTimeout(()=>t.style.opacity='0', 1400);
    }

    // 헬퍼
    const $ = (s,ctx=document)=>ctx.querySelector(s);
    const $$= (s,ctx=document)=>Array.from(ctx.querySelectorAll(s));
    const readIdFrom = (btn)=> Number(btn?.dataset?.id ?? btn.closest('[data-book-id]')?.dataset?.bookId ?? NaN);
    const readQty = (btn)=> { const q = Number(btn?.dataset?.qty); return Number.isFinite(q)&&q>0?q:1; };

    // 최초 진입: 카드 페이드업
    document.addEventListener('DOMContentLoaded', ()=>{
        $$('.srch-item').forEach((el,i)=>{ el.classList.add('soft-fade-up'); el.style.animationDelay = (i*0.03)+'s'; });
    });

    // 단건 클릭
    document.addEventListener('click', async (e)=>{
        const btn = e.target.closest('[data-act]'); if (!btn) return;
        const act = btn.dataset.act;
        if (act!=='cart' && act!=='like') return;

        const id = readIdFrom(btn), qty = readQty(btn);
        if (!Number.isFinite(id)) { alert('도서 ID가 없습니다.'); return; }

        if (!confirm(act==='cart'?'장바구니에 담으시겠습니까?':'위시리스트에 담으시겠습니까?')) return;

        try{
            if (act==='cart'){ await postForm(URLS.cartAdd,{bookId:id, quantity:qty}); toast('장바구니에 담았습니다.'); }
            else { await postForm(URLS.wishlistAdd,{bookId:id}); toast('위시리스트에 담았습니다.'); }
        }catch(err){ alert(err.message||'처리 중 오류'); }
    });

    // 전체선택
    const checkAll = $('#checkAll');
    if (checkAll){
        checkAll.addEventListener('change', ()=>{
            $$('input[type="checkbox"][name="selected"]').forEach(cb=>cb.checked = checkAll.checked);
        });
    }

    // 일괄 버튼
    $$('button[data-bulk]').forEach(btn=>{
        btn.addEventListener('click', async ()=>{
            const ids = $$('input[name="selected"]:checked').map(cb=>Number(cb.value)).filter(Number.isFinite);
            if (!ids.length) { alert('선택된 도서가 없습니다.'); return; }
            const mode = btn.dataset.bulk; // cart | like
            if (!confirm(mode==='cart'?'선택 도서를 장바구니에 담으시겠습니까?':'선택 도서를 위시리스트에 담으시겠습니까?')) return;
            try{
                if (mode==='cart'){
                    const qty = readQty(btn);
                    await Promise.all(ids.map(id=>postForm(URLS.cartAdd,{bookId:id, quantity:qty})));
                    toast('선택 도서를 장바구니에 담았습니다.');
                } else {
                    await Promise.all(ids.map(id=>postForm(URLS.wishlistAdd,{bookId:id})));
                    toast('선택 도서를 위시리스트에 담았습니다.');
                }
            }catch(err){ alert(err.message||'일괄 처리 오류'); }
        });
    });

    // 정렬 적용 시 page=0
    const form = document.forms['listForm'];
    if (form){
        const pageHidden = $('#page', form);
        const applyBtn = form.querySelector('button[type="submit"]');
        if (applyBtn && pageHidden){
            applyBtn.addEventListener('click', ()=>{ pageHidden.value = 0; });
        }
    }
})();
