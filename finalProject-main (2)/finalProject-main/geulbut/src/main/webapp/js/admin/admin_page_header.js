// /js/admin/admin_header.js
(function () {
    const dd = document.querySelector('.ad-dropdown');
    if (!dd) return;

    const trigger = dd.querySelector('.ad-link--dropdown');
    const menu    = dd.querySelector('.ad-menu');
    if (!trigger || !menu) return;

    let closeTimer = null;

    const firstItem = () => menu.querySelector('.ad-menu__item');

    function openMenu() {
        clearTimeout(closeTimer);
        dd.classList.add('is-open');
        trigger.setAttribute('aria-expanded', 'true');
    }

    function closeMenu() {
        dd.classList.remove('is-open');
        trigger.setAttribute('aria-expanded', 'false');
    }

    // --- 마우스: hover + grace close ---
    dd.addEventListener('mouseenter', openMenu);
    dd.addEventListener('mouseleave', () => {
        clearTimeout(closeTimer);
        closeTimer = setTimeout(closeMenu, 400); // 짧은 지연
    });

    // --- 포커스: 내부에 포커스 있으면 열림 유지 ---
    dd.addEventListener('focusin', openMenu);
    dd.addEventListener('focusout', () => {
        // 포커스가 완전히 빠져나간 경우만 닫기
        setTimeout(() => {
            if (!dd.contains(document.activeElement)) closeMenu();
        }, 0);
    });

    // --- 트리거 클릭: 첫 클릭은 열고, 열려 있으면 원래 링크로 이동 ---
    trigger.addEventListener('click', (e) => {
        if (!dd.classList.contains('is-open')) {
            e.preventDefault(); // 첫 클릭은 열기만
            openMenu();
            const item = firstItem();
            item && item.focus();
        }
        // 열려 있으면 기본 동작(해시태그 페이지로 이동)
    });

    // --- 키보드 접근성 ---
    trigger.addEventListener('keydown', (e) => {
        if (e.key === 'ArrowDown' || e.key === 'Enter' || e.key === ' ') {
            e.preventDefault();
            openMenu();
            const item = firstItem();
            item && item.focus();
        }
    });

    menu.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            e.preventDefault();
            closeMenu();
            trigger.focus();
        }
    });
})();
