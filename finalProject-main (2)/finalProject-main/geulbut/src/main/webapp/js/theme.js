document.addEventListener('DOMContentLoaded', function() {

    /*** === 테마 전환 === ***/
    document.querySelectorAll('.theme-switcher button').forEach(btn => {
        btn.addEventListener('click', () => {
            const theme = btn.getAttribute('data-theme');
            document.documentElement.setAttribute('data-theme', theme);
            localStorage.setItem('theme', theme); // 새로고침해도 유지
        });
    });

    // 저장된 테마 적용
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
        document.documentElement.setAttribute('data-theme', savedTheme);
    } else {
        document.documentElement.setAttribute('data-theme', 'light'); // 기본값
    }
})