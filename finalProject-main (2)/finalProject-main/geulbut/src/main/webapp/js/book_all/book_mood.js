/* 화면 전용 초경량 이펙트 (로직/네트워크 무관)
 * - 페이지 페이드인
 * - 카드/이미지 소프트 등장
 * - 이미지 쉬머(순수 CSS keyframes 주입)
 * - 버튼/아이콘 클릭 플래시(하이라이트)
 */
(() => {
    const d = document, w = window;
    const reduce = w.matchMedia?.('(prefers-reduced-motion: reduce)')?.matches ?? false;

    // ===== 0) 필요한 최소 CSS를 JS로 주입 (프로젝트 CSS 안 건드림) =====
    const css = `
  @keyframes moodFadeIn { from{opacity:0; transform:translateY(4px)} to{opacity:1; transform:none} }
  @keyframes moodShimmer {
      0%{background-position:-200% 0} 100%{background-position:200% 0}
  }
  .mood-fade { animation:moodFadeIn .28s ease both; }
  .mood-shimmer {
      position: relative; overflow: hidden;
      background: linear-gradient(90deg, #f2f4f7 0%, #e9ecf1 50%, #f2f4f7 100%);
      background-size: 200% 100%;
      animation: moodShimmer 1.4s linear infinite;
  }
  .mood-clickflash { position:relative; }
  .mood-clickflash::after{
      content:""; position:absolute; inset:0; background:rgba(255,255,255,.45);
      opacity:0; pointer-events:none; transition:opacity 180ms ease;
  }
  .mood-clickflash.is-flash::after{ opacity:1; }
  @media (hover:hover){
    .mood-elevate { transition: box-shadow 140ms ease, transform 140ms ease; }
    .mood-elevate:hover { box-shadow:0 8px 20px rgba(0,0,0,.08); transform: translateY(-1px); }
  }
  `;
    const style = d.createElement('style');
    style.textContent = css;
    d.head.appendChild(style);

    // ===== 1) 페이지 페이드인 (reduce면 스킵) =====
    function mountPageFade() {
        if (reduce) return;
        d.documentElement.style.opacity = '0';
        d.documentElement.style.transition = 'opacity 200ms ease';
        requestAnimationFrame(() => { d.documentElement.style.opacity = '1'; });
    }

    // ===== 2) 카드 등장 & 호버만 살짝 부각 =====
    function mountCardFX() {
        const cards = Array.from(d.querySelectorAll('.srch-item'));
        if (!cards.length) return;

        // 호버 부각
        cards.forEach(c => c.classList.add('mood-elevate'));

        if (reduce) return;
        // 뷰포트 들어올 때만 짧게 등장
        const io = new IntersectionObserver((ents) => {
            ents.forEach(ent => {
                if (ent.isIntersecting) {
                    ent.target.classList.add('mood-fade');
                    io.unobserve(ent.target);
                }
            });
        }, { threshold: 0.08 });

        cards.forEach(c => io.observe(c));
    }

    // ===== 3) 썸네일 쉬머 (이미지 로드 전·저해상도일 때도 살짝 반짝) =====
    function mountThumbShimmer() {
        if (reduce) return;
        const thumbs = Array.from(d.querySelectorAll('.srch-thumb'));
        thumbs.forEach(box => {
            const img = box.querySelector('img');
            if (!img) return;

            // 이미지 로딩 전/지연 로딩일 때만 쉬머 표시
            const apply = () => box.classList.add('mood-shimmer');
            const remove = () => box.classList.remove('mood-shimmer');

            // 초기 상태
            if (!img.complete || img.naturalWidth === 0) apply();

            img.addEventListener('load', remove, { once: true });
            img.addEventListener('error', remove, { once: true });
        });
    }

    // ===== 4) 버튼/아이콘 클릭 순간 플래시 =====
    function mountClickFlash() {
        const targets = Array.from(d.querySelectorAll(
            '.srch-info button, .srch-icons .icon-btn, nav[aria-label="페이지 네비게이션"] a'
        ));
        targets.forEach(el => {
            el.classList.add('mood-clickflash');
            el.addEventListener('click', () => {
                el.classList.add('is-flash');
                setTimeout(() => el.classList.remove('is-flash'), 140);
            });
        });
    }

    // ===== Init =====
    w.addEventListener('DOMContentLoaded', () => {
        mountPageFade();
        mountCardFX();
        mountThumbShimmer();
        mountClickFlash();
    });
})();
