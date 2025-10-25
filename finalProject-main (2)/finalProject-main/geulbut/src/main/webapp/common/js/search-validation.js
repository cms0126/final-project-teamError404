

(function initSearchValidationSubmitOnly() {
    const form = document.querySelector('.search-form');
    if (!form || form.dataset.validationBound === 'true') return;
    form.dataset.validationBound = 'true';

    const input = form.querySelector('input[type="text"]');
    if (!input) return;



    // 허용 규칙
    const ONLY_ALLOWED_CHARS = /^[A-Za-z0-9가-힣\s]+$/;   // 허용 문자 집합
    const MIN_LENGTH = 2;

    form.addEventListener('submit', function (e) {
        const raw = (input.value || '');
        const trimmed = raw.trim();

        // 빈 값 허용 안 함 (UX: 바로 안내)
        if (trimmed.length === 0) {
            alert('검색어를 입력해 주세요.');
            input.focus();
            e.preventDefault();
            return false;
        }

        // 최소 길이
        if (trimmed.length < MIN_LENGTH) {
            alert('검색어는 최소 2글자여야 해요.');
            input.focus();
            e.preventDefault();
            return false;
        }

        // 허용 문자 집합 검사 (특수문자 금지)
        if (!ONLY_ALLOWED_CHARS.test(trimmed)) {
            alert('특수문자는 사용할 수 없어요. 한글/영문/숫자만 입력해 주세요.');
            input.focus();
            e.preventDefault();
            return false;
        }

        // 통과 → 서버로 제출
        // (원하면 여기서 input.value = trimmed; 로 공백만 정리해 제출)
        input.value = trimmed;
        return true;
    });
})();
