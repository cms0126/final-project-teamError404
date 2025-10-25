// /js/home/mood-hero.js
console.log('[mood-hero] 감성 문장 배너 작동');

(function(){
    const QUOTES = [
        { q: "괜찮아, 천천히 가도 돼.", m: "밤은 늘 너를 쉬게 하려고 온다" },
        { q: "지금은 멈춰 서도 괜찮은 시간이에요.", m: "쉬는 것도 전진의 한 모양" },
        { q: "작은 숨이라도, 오늘은 너의 편이 될게.", m: "조용한 밤, 마음을 덮는 한 줄" },
        { q: "흐린 날에도 바다는 사라지지 않더라.", m: "당신의 바다도 여전히 깊어요" },
        { q: "우리는 결국, 견뎌낸 만큼 단단해져요.", m: "빛은 늘 가장 마지막에 온다" }
    ];

    const elSection = document.querySelector('.mood-hero');
    const elQuote   = document.getElementById('moodQuote');
    const elMeta    = document.getElementById('moodMeta');

    if(!elSection || !elQuote) return;

    let hour = new Date().getHours();
    let idx  = (hour >= 20 || hour <= 6) ? 2 : 0;

    function render(i, instant=false){
        const { q, m } = QUOTES[i % QUOTES.length];
        if(!instant){
            elSection.classList.add('mood-hero--fadeout');
            setTimeout(()=>{
                elQuote.textContent = `“${q}”`;
                elMeta.textContent  = m || '';
                elSection.classList.remove('mood-hero--fadeout');
                elSection.classList.add('mood-hero--fadein');
                setTimeout(()=> elSection.classList.remove('mood-hero--fadein'), 400);
            }, 240);
        }else{
            elQuote.textContent = `“${q}”`;
            elMeta.textContent  = m || '';
        }
    }

    render(idx, true);

    let timer = setInterval(()=>{ idx = (idx+1) % QUOTES.length; render(idx); }, 4000);
    elSection.addEventListener('mouseenter', ()=> clearInterval(timer));
    elSection.addEventListener('mouseleave', ()=>{
        clearInterval(timer);
        timer = setInterval(()=>{ idx = (idx+1) % QUOTES.length; render(idx); }, 7000);
    });

})();

/* Night Mood Layer: Dust Particles + Shooting Star + Parallax
 * - 홈 메인에만 로드 (home.js 다음에)
 */
(() => {
    const PRM = window.matchMedia?.('(prefers-reduced-motion: reduce)')?.matches;
    if (PRM) return; // 모션 최소화 환경이면 비활성

    // 1) 레이어/캔버스 생성 (DOM 수정 없이 body에만 추가)
    const layer = document.createElement('div');
    layer.id = 'mood-layer';
    const canvas = document.createElement('canvas');
    canvas.id = 'mood-canvas';
    layer.appendChild(canvas);
    document.body.insertBefore(layer, document.body.firstChild);

    // 2) 캔버스 컨텍스트/크기
    const dpr = Math.max(1, Math.min(2, window.devicePixelRatio || 1));
    const ctx = canvas.getContext('2d', { alpha: true });

    function resize() {
        const { innerWidth: w, innerHeight: h } = window;
        canvas.width  = Math.floor(w * dpr);
        canvas.height = Math.floor(h * dpr);
        canvas.style.width  = w + 'px';
        canvas.style.height = h + 'px';
    }
    resize();
    window.addEventListener('resize', resize, { passive: true });

    // 3) 파티클(먼지/빛) 세팅
    const state = {
        particles: [],
        maxParticles: 90,                // 너무 많지 않게 (부드럽게)
        minSize: 0.4, maxSize: 1.4,      // 작고 은은한 점
        minSpeed: 0.02, maxSpeed: 0.12,  // 아주 느린 부유감
        hueBase: 42,                     // 따뜻한 노랑/크림톤
        alphaBase: 0.25,                 // 약한 밝기
        parallax: { x: 0, y: 0 },
        targetParallax: { x: 0, y: 0 },
        shooting: null,                  // 현재 별똥별
        starCooldown: 0,                 // 재생성 쿨다운
        time: 0,
    };

    function rand(a, b) { return a + Math.random() * (b - a); }

    function initParticles() {
        state.particles.length = 0;
        const { width: W, height: H } = canvas;
        for (let i = 0; i < state.maxParticles; i++) {
            state.particles.push({
                x: Math.random() * W,
                y: Math.random() * H,
                r: rand(state.minSize * dpr, state.maxSize * dpr),
                spx: rand(-state.maxSpeed, state.maxSpeed) * dpr,
                spy: rand(-state.maxSpeed, state.maxSpeed) * dpr,
                a: rand(state.alphaBase * 0.5, state.alphaBase), // 기본 투명
                tw: rand(0.0003, 0.001),                         // 반짝 주기
                t: Math.random() * 1000
            });
        }
    }
    initParticles();

    // 4) 별똥별 생성
    function spawnShootingStar() {
        const { width: W, height: H } = canvas;
        const fromTop = Math.random() < 0.5;

        // 시작점(좌상/우상 한쪽)
        const startX = fromTop ? rand(0, W * 0.4) : rand(W * 0.6, W);
        const startY = rand(0, H * 0.2);
        // 방향(우하로 살짝)
        const angle = rand(Math.PI * 0.12, Math.PI * 0.22);
        const speed = rand(6, 9) * dpr; // 비교적 빠르게 슥 지나감
        const length = rand(W * 0.08, W * 0.14);
        state.shooting = {
            x: startX,
            y: startY,
            vx: Math.cos(angle) * speed,
            vy: Math.sin(angle) * speed,
            life: 0,
            maxLife: rand(500, 900), // ms
            length
        };
    }

    // 5) 파랄랙스: 마우스 살짝 따라오게
    const maxShift = 10; // px (레이어 전체를 살짝 이동)
    function onPointerMove(e) {
        const w = window.innerWidth, h = window.innerHeight;
        const nx = (e.clientX / w - 0.5) * 2; // -1 ~ 1
        const ny = (e.clientY / h - 0.5) * 2;
        state.targetParallax.x = nx * maxShift;
        state.targetParallax.y = ny * maxShift;
    }
    window.addEventListener('mousemove', onPointerMove, { passive: true });

    // 6) 루프
    let rafId = null;
    let last = performance.now();
    function tick(now) {
        const dt = now - last; last = now;
        state.time += dt;

        // 레이어 부드럽게 이동
        state.parallax.x += (state.targetParallax.x - state.parallax.x) * 0.05;
        state.parallax.y += (state.targetParallax.y - state.parallax.y) * 0.05;
        layer.style.transform = `translate3d(${state.parallax.x}px, ${state.parallax.y}px, 0)`;

        const { width: W, height: H } = canvas;
        ctx.clearRect(0, 0, W, H);

        // 파티클 업데이트 & 렌더
        for (const p of state.particles) {
            p.t += dt;
            p.x += p.spx * dt * 0.06;
            p.y += p.spy * dt * 0.06;

            // 가장자리를 부드럽게 되돌리기
            if (p.x < -10) p.x = W + 10;
            if (p.x > W + 10) p.x = -10;
            if (p.y < -10) p.y = H + 10;
            if (p.y > H + 10) p.y = -10;

            // 은은한 반짝임
            const twinkle = (Math.sin(p.t * p.tw) + 1) * 0.5; // 0~1
            const alpha = p.a * (0.4 + twinkle * 0.6);

            ctx.beginPath();
            ctx.fillStyle = `hsla(${state.hueBase}, 70%, 80%, ${alpha})`;
            ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
            ctx.fill();
        }

        // 별똥별 스폰 타이밍 (15~25초 랜덤)
        if (!state.shooting && state.starCooldown <= 0) {
            if (Math.random() < 0.002) { // 낮은 확률로 트리거
                spawnShootingStar();
                state.starCooldown = rand(15000, 25000);
            }
        } else if (state.starCooldown > 0) {
            state.starCooldown -= dt;
        }

        // 별똥별 업데이트 & 렌더
        if (state.shooting) {
            const s = state.shooting;
            s.life += dt;
            s.x += s.vx;
            s.y += s.vy;

            const progress = s.life / s.maxLife; // 0 ~ 1
            const fade = Math.max(0, 1 - progress);
            const tailX = s.x - s.vx * 2.2; // 꼬리 길이 조정
            const tailY = s.y - s.vy * 2.2;

            const grd = ctx.createLinearGradient(s.x, s.y, tailX, tailY);
            grd.addColorStop(0, `hsla(${state.hueBase}, 80%, 92%, ${0.8 * fade})`);
            grd.addColorStop(1, `hsla(${state.hueBase}, 80%, 92%, 0)`);

            ctx.beginPath();
            ctx.strokeStyle = grd;
            ctx.lineWidth = Math.max(1, 1.2 * dpr);
            ctx.moveTo(s.x, s.y);
            ctx.lineTo(tailX, tailY);
            ctx.stroke();

            if (progress >= 1) state.shooting = null;
        }

        rafId = requestAnimationFrame(tick);
    }
    rafId = requestAnimationFrame(tick);

    // 7) 탭 비활성/활성 시 절전
    document.addEventListener('visibilitychange', () => {
        if (document.hidden) {
            cancelAnimationFrame(rafId);
            rafId = null;
        } else if (!rafId) {
            last = performance.now();
            rafId = requestAnimationFrame(tick);
        }
    });

    // 8) 안전 정리(혹시 SPA 전환 시)
    window.addEventListener('beforeunload', () => {
        cancelAnimationFrame(rafId);
    });
})();



