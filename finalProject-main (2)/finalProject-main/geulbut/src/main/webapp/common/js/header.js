document.addEventListener('DOMContentLoaded', function () {
    // 햄버거 메뉴
    const hamburger = document.querySelector('.site-header__hamburger');
    const mobileMenu = document.querySelector('.site-header__nav--right');
    if (hamburger && mobileMenu) {
        hamburger.addEventListener('click', (e) => {
            e.stopPropagation();
            mobileMenu.classList.toggle('open');
            hamburger.classList.toggle('active');
        });

        document.addEventListener('click', (e) => {
            if (!hamburger.contains(e.target) && !mobileMenu.contains(e.target)) {
                mobileMenu.classList.remove('open');
                hamburger.classList.remove('active');
            }
        });

        window.addEventListener('resize', () => {
            if (window.innerWidth > 768) {
                mobileMenu.classList.remove('open');
                hamburger.classList.remove('active');
            }
        });
    }

    // 관리자 패널 토글
    const adminBtn = document.querySelector('.admin-toggle');
    const adminPanel = document.querySelector('.admin-panel');
    if (adminBtn && adminPanel) {
        adminBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            adminPanel.classList.toggle('open');
            adminBtn.classList.toggle('active');
        });

        document.addEventListener('click', (e) => {
            if (!adminBtn.contains(e.target) && !adminPanel.contains(e.target)) {
                adminPanel.classList.remove('open');
                adminBtn.classList.remove('active');
            }
        });

        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                adminPanel.classList.remove('open');
                adminBtn.classList.remove('active');
            }
        });
    }

});
