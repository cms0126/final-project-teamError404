document.addEventListener('DOMContentLoaded', () => {
    /*** ===== 탭 자동 슬라이드 ===== ***/
    const tabItems = document.querySelectorAll('.tab-item');
    const playButton = document.querySelector('.play-button');

    const tabContents = [
        'editor-choice-content','weekly-content','new-books-content','trending-content','hotdeal-content'
    ];
    const tabTexts = ['편집장의 선택','이 주의 책','신간 소개','화제의 책','지금 핫딜중'];

    let currentTabIndex = 0;
    let autoSlideInterval = null;
    let isPlaying = false;
    const autoSlideDelay = 4000;
    const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    function applyA11y(i){
        tabItems.forEach((btn, idx)=>{
            btn.setAttribute('role','tab');
            btn.setAttribute('aria-controls', tabContents[idx]);
            btn.setAttribute('aria-selected', String(idx===i));
        });
    }

    function showTabContent(tabIndex){
        tabItems.forEach(t => t.classList.remove('active'));
        tabItems[tabIndex]?.classList.add('active');

        tabContents.forEach(id=>{
            const el = document.getElementById(id);
            if (!el) return;
            el.style.display = 'none';
            el.classList.remove('active');
        });

        const active = document.getElementById(tabContents[tabIndex]);
        if (active){
            active.style.display = 'block';
            active.classList.add('active');
        }

        const title = document.getElementById('section-title');
        if (title) title.textContent = tabTexts[tabIndex];

        currentTabIndex = tabIndex;
        applyA11y(tabIndex);
    }

    function nextTabSlide(){ showTabContent((currentTabIndex + 1) % tabContents.length); }
    function prevTabSlide(){ showTabContent((currentTabIndex - 1 + tabContents.length) % tabContents.length); }

    function startAutoSlide(){
        if (isPlaying || reduceMotion) return;
        isPlaying = true;
        autoSlideInterval = setInterval(nextTabSlide, autoSlideDelay);
        playButton?.classList.remove('playing');
        playButton?.classList.add('paused');
    }
    function stopAutoSlide(){
        if (!isPlaying) return;
        isPlaying = false;
        clearInterval(autoSlideInterval);
        autoSlideInterval = null;
        playButton?.classList.remove('paused');
        playButton?.classList.add('playing');
    }

    playButton?.addEventListener('click', ()=> isPlaying ? stopAutoSlide() : startAutoSlide());
    tabItems.forEach((tab, index)=>{
        tab.addEventListener('click', ()=>{ if (isPlaying) stopAutoSlide(); showTabContent(index); });
    });

    // 섹션 hover/focus 시 자동재생 일시정지 (UX)
    const section = document.querySelector('.editor-choice-section');
    section?.addEventListener('mouseenter', stopAutoSlide);
    section?.addEventListener('mouseleave', ()=> { if (!isPlaying && !reduceMotion) startAutoSlide(); });
    section?.addEventListener('focusin', stopAutoSlide);
    section?.addEventListener('focusout', ()=> { if (!isPlaying && !reduceMotion) startAutoSlide(); });

    // 초기 탭 + 자동재생
    showTabContent(0);
    setTimeout(()=>{ if (!reduceMotion) startAutoSlide(); }, 3000);

    /*** ===== 전폭 배너 슬라이드 ===== ***/
    let currentBannerSlide = 0;
    const totalBannerSlides = 3;
    let bannerInterval = null;
    let userInteracting = false;

    function updateBannerSlider(){
        const track = document.getElementById('sliderTrack');
        if (track) track.style.transform = `translateX(${-currentBannerSlide * 33.333}%)`;
    }
    function nextBannerSlide(){ currentBannerSlide = (currentBannerSlide + 1) % totalBannerSlides; updateBannerSlider(); }
    function prevBannerSlide(){ currentBannerSlide = (currentBannerSlide - 1 + totalBannerSlides) % totalBannerSlides; updateBannerSlider(); }

    function startBannerAutoSlide(){
        if (reduceMotion) return;
        bannerInterval = setInterval(()=>{ if (!userInteracting) nextBannerSlide(); }, 4000);
    }
    function stopBannerAutoSlide(){ if (bannerInterval){ clearInterval(bannerInterval); bannerInterval = null; } }
    function resetBannerAutoSlide(){ stopBannerAutoSlide(); userInteracting = false; setTimeout(startBannerAutoSlide, 3000); }

    window.nextBanner = function () { userInteracting = true; stopBannerAutoSlide(); nextBannerSlide(); resetBannerAutoSlide(); }
    window.prevBanner = function () { userInteracting = true; stopBannerAutoSlide(); prevBannerSlide(); resetBannerAutoSlide(); }

    setTimeout(startBannerAutoSlide, 2000);

    /*** ===== 장바구니 / 구매 ===== ***/
    window.addToCart = async (bookId, qty=1)=>{
        if (!Number.isFinite(bookId)) return alert('도서 ID가 없습니다.');
        const CTX = (window.CONTEXT_PATH || '');
        const URL = CTX + '/cart';

        const CSRF_TOKEN  = document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || null;
        const CSRF_HEADER = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-CSRF-TOKEN';

        try{
            const headers = new Headers({ 'Content-Type':'application/x-www-form-urlencoded;charset=UTF-8' });
            if (CSRF_TOKEN) headers.set(CSRF_HEADER, CSRF_TOKEN);
            const body = new URLSearchParams({ bookId:String(bookId), quantity:String(qty) });

            const res = await fetch(URL, { method:'POST', headers, body });
            if (res.status === 401){ location.href = CTX + '/users/login'; return; }

            const data = await res.json();
            if (data.status === 'ok') toast('장바구니에 담겼습니다!');
            else toast('장바구니 담기 실패: ' + (data.message || '알 수 없는 오류'));
        }catch(err){
            console.error('장바구니 추가 오류', err); toast('장바구니 담기 실패: 네트워크 오류');
        }
    };

    window.buyBook = async (bookId, qty=1)=>{
        if (!Number.isFinite(bookId)) return alert('도서 ID가 없습니다.');
        try{
            if (window.PRODUCT && window.PRODUCT.id === bookId && window.Orders?.openOrderInfoModal){
                const total = Number((window.PRODUCT?.discountedPrice ?? window.PRODUCT?.price) ?? 0);
                if (!Number.isFinite(total) || total <= 0) return alert('결제 금액을 계산할 수 없습니다.');
                Orders.openOrderInfoModal(total); return;
            }
            const card = document.querySelector(`.hotdeal-card[data-book-id="${bookId}"]`);
            const detailUrl = card?.dataset.detailUrl;
            if (detailUrl){
                const url = new URL(detailUrl, location.origin); url.searchParams.set('autoBuy','true'); location.href = url.toString();
            }
        }catch(err){ console.error('구매 오류', err); alert('구매 처리 중 오류가 발생했습니다.'); }
    };

    // 핫딜 버튼 위임
    document.querySelectorAll('.hotdeal-card .hotdeal-button.buy-now').forEach(btn=>{
        btn.addEventListener('click', e=>{
            e.stopPropagation();
            const bookId = Number(btn.dataset.bookId);
            window.buyBook(bookId);
        });
    });

    // 카드 전체 클릭 → 상세 (내부 버튼 클릭 시 무시)
    document.querySelectorAll('.hotdeal-card').forEach(card=>{
        card.addEventListener('click', e=>{
            if (e.target.closest('.hotdeal-button')) return;
            const url = card.dataset.detailUrl; if (url) location.href = url;
        });
    });

    // 간단 토스트
    function toast(msg){
        let t = document.getElementById('_toast');
        if (!t){
            t = document.createElement('div');
            t.id = '_toast';
            Object.assign(t.style, {
                position:'fixed', left:'50%', bottom:'28px', transform:'translateX(-50%)',
                padding:'10px 14px', borderRadius:'10px', background:'rgba(0,0,0,.78)',
                color:'#fff', fontWeight:'600', zIndex:'9999', transition:'opacity .25s ease'
            });
            document.body.appendChild(t);
        }
        t.textContent = msg; t.style.opacity = '1'; setTimeout(()=> t.style.opacity = '0', 1400);
    }

    console.log('Home loaded with improved UX/CLS/Accessibility.');
});
document.querySelectorAll('a[href^="#"]').forEach(a=>{
    a.addEventListener('click', e=>{
        const id = a.getAttribute('href');
        if(!id || id === '#') return;
        const el = document.querySelector(id);
        if(!el) return;
        e.preventDefault();
        el.scrollIntoView({ behavior:'smooth', block:'start' });
    });
});

(function promoSlider() {
    const container = document.querySelector('.promotion-container');
    if (!container) return;

    const pages = Array.from(container.querySelectorAll('.promotion-page'));
    if (pages.length <= 1) return;

    const prevBtn = document.getElementById('promoPrevBtn');
    const nextBtn = document.getElementById('promoNextBtn');

    let idx = pages.findIndex(p => p.classList.contains('active'));
    if (idx < 0) idx = 0;

    // 높이 부드럽게 맞추고 싶다면 컨테이너 높이를 전환 때 업데이트
    function setContainerHeight(i) {
        const h = pages[i].offsetHeight;
        container.style.height = h + 'px';
    }

    function show(i) {
        pages.forEach((p, k) => p.classList.toggle('active', k === i));
        setContainerHeight(i);
        idx = i;
    }

    // 초기 높이 세팅
    setContainerHeight(idx);

    nextBtn?.addEventListener('click', () => {
        const n = (idx + 1) % pages.length;
        show(n);
    });

    prevBtn?.addEventListener('click', () => {
        const n = (idx - 1 + pages.length) % pages.length;
        show(n);
    });

    // (선택) 자동 슬라이드
    let timer = setInterval(() => nextBtn?.click(), 5000);
    container.addEventListener('mouseenter', () => clearInterval(timer));
    container.addEventListener('mouseleave', () => { timer = setInterval(() => nextBtn?.click(), 5000); });
})();
