// /js/signup.js
(() => {
    // ===== 0) DOM 요소 =====
    const $form = document.getElementById('signupForm');
    const $submit = document.getElementById('submitBtn');

    const $userId = document.getElementById('userId');

    // 이메일 구성 요소
    const $emailHidden = document.getElementById('email'); // 제출용 hidden
    const $emailLocal = document.getElementById('emailLocal');
    const $emailDomainSelect = document.getElementById('emailDomainSelect');
    const $emailDomainCustom = document.getElementById('emailDomainCustom');

    const $password = document.getElementById('password');
    const $password2 = document.getElementById('password2');

    const $userIdMsg = document.getElementById('userIdMsg');
    const $emailMsg = document.getElementById('emailMsg');
    const $passwordMsg = document.getElementById('passwordMsg');
    const $password2Msg = document.getElementById('password2Msg');

    if (!$form) return; // 안전장치: 해당 페이지가 아닐 수 있음

    // ===== 1) 상태 플래그 =====
    let idOK = false, emailOK = true, pwOK = false, pw2OK = false;

    // ===== 2) 유틸 =====
    const debounce = (fn, ms = 300) => {
        let t;
        return (...args) => { clearTimeout(t); t = setTimeout(() => fn(...args), ms); };
    };

    function refreshSubmit() { $submit.disabled = !(idOK && emailOK && pwOK && pw2OK); }

    // 아이디 정책
    const USERID_RE = /^[a-z0-9]{4,20}$/;

    function normalizeUserId(raw) {
        if (!raw) return "";
        const lowered = raw.toLowerCase();
        return lowered.replace(/[^a-z0-9]/g, "");
    }

    // 이메일 유효성(간단검사)
    const EMAIL_LOCAL_RE = /^[A-Za-z0-9._%+-]+$/;                // 로컬파트
    const EMAIL_DOMAIN_RE = /^([A-Za-z0-9-]+\.)+[A-Za-z]{2,}$/;  // 도메인
    const EMAIL_WHOLE_RE  = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;        // 전체

    function getSelectedDomain() {
        const sel = ($emailDomainSelect?.value ?? "");
        if (sel === "_custom") {
            return ($emailDomainCustom?.value || "").trim().toLowerCase();
        }
        return (sel || "").trim().toLowerCase();
    }

    function assembleEmail() {
        const local = ($emailLocal?.value || "").trim();
        const domain = getSelectedDomain();
        if (!local || !domain) return "";
        return `${local}@${domain}`.toLowerCase();
    }

    function toggleCustomDomainInput() {
        if (!$emailDomainSelect || !$emailDomainCustom) return;
        const useCustom = ($emailDomainSelect.value === "_custom");
        $emailDomainCustom.style.display = useCustom ? "" : "none";
        if (!useCustom) $emailDomainCustom.value = "";
    }

    function populateEmailFieldsFromHidden() {
        if (!$emailHidden || !$emailLocal || !$emailDomainSelect || !$emailDomainCustom) return;
        const v = ($emailHidden.value || "").trim();
        if (!v || !v.includes("@")) return;
        const [local, domain] = v.split("@");
        $emailLocal.value = local || "";

        const options = Array.from($emailDomainSelect.options).map(o => o.value);
        if (options.includes(domain)) {
            $emailDomainSelect.value = domain;
            $emailDomainCustom.value = "";
            $emailDomainCustom.style.display = "none";
        } else {
            $emailDomainSelect.value = "_custom";
            $emailDomainCustom.value = domain;
            $emailDomainCustom.style.display = "";
        }
    }

    // ===== 3) 검증 로직 =====
    async function checkId() {
        const raw = $userId.value;
        const normalized = normalizeUserId(raw);
        if (raw !== normalized) $userId.value = normalized;

        const v = normalized;

        if (!v) {
            idOK = false;
            $userIdMsg.textContent = '아이디를 입력하세요.';
            refreshSubmit(); return;
        }
        if (!USERID_RE.test(v)) {
            idOK = false;
            if (v.length < 4)       $userIdMsg.textContent = '아이디는 4자 이상이어야 합니다.';
            else if (v.length > 20) $userIdMsg.textContent = '아이디는 20자 이하여야 합니다.';
            else                    $userIdMsg.textContent = '영문 소문자와 숫자만 사용할 수 있습니다.';
            refreshSubmit(); return;
        }

        try {
            const res = await fetch('/users/check-id?userId=' + encodeURIComponent(v), { method: 'GET' });
            const ok = await res.json(); // 서버에서 true=사용가능 / false=중복 으로 응답한다고 가정
            idOK = !!ok;
            $userIdMsg.textContent = ok ? '사용 가능한 아이디입니다.' : '이미 사용 중인 아이디입니다.';
        } catch (e) {
            idOK = false;
            $userIdMsg.textContent = '아이디 확인 중 오류가 발생했습니다.';
        } finally {
            refreshSubmit();
        }
    }

    async function checkEmail() {
        const local = ($emailLocal?.value || "").trim();
        const domain = getSelectedDomain();

        // 선택항목 정책: 둘 다 비어있으면 통과
        if (!local && !domain) {
            emailOK = true;
            if ($emailHidden) $emailHidden.value = "";
            $emailMsg.textContent = '';
            refreshSubmit();
            return;
        }

        // 로컬/도메인 각각 기본 검사
        if (!EMAIL_LOCAL_RE.test(local)) {
            emailOK = false;
            $emailMsg.textContent = '이메일 앞부분(아이디) 형식을 확인해주세요.';
            refreshSubmit();
            return;
        }
        if (!EMAIL_DOMAIN_RE.test(domain)) {
            emailOK = false;
            $emailMsg.textContent = '이메일 도메인 형식을 확인해주세요.';
            refreshSubmit();
            return;
        }

        const email = `${local}@${domain}`.toLowerCase();

        // 전체 패턴(간단) 확인
        if (!EMAIL_WHOLE_RE.test(email)) {
            emailOK = false;
            $emailMsg.textContent = '이메일 형식을 확인해주세요.';
            refreshSubmit();
            return;
        }

        // hidden에 반영
        if ($emailHidden) $emailHidden.value = email;

        // 서버 중복확인
        try {
            const res = await fetch('/users/check-email?email=' + encodeURIComponent(email), { method: 'GET' });
            const ok = await res.json();
            emailOK = !!ok;
            $emailMsg.textContent = ok ? '사용 가능한 이메일입니다.' : '이미 가입된 이메일입니다.';
        } catch (e) {
            emailOK = false;
            $emailMsg.textContent = '이메일 확인 중 오류가 발생했습니다.';
        } finally {
            refreshSubmit();
        }
    }

    function checkPassword() {
        const v = $password?.value || "";
        if (v.length >= 8) {
            pwOK = true;
            $passwordMsg.textContent = '';
        } else {
            pwOK = false;
            $passwordMsg.textContent = '비밀번호는 8자 이상이어야 합니다.';
        }
        refreshSubmit();
        checkPasswordConfirm();
    }

    function checkPasswordConfirm() {
        const v1 = $password?.value || "";
        const v2 = $password2?.value || "";
        if (!v2) {
            pw2OK = false;
            $password2Msg.textContent = '비밀번호를 다시 입력하세요.';
        } else if (v1 !== v2) {
            pw2OK = false;
            $password2Msg.textContent = '비밀번호가 일치하지 않습니다.';
        } else {
            pw2OK = true;
            $password2Msg.textContent = '';
        }
        refreshSubmit();
    }

    // ===== 4) 이벤트 바인딩 =====
    // 아이디 입력: IME 대응 + 디바운스
    let composing = false;
    const debouncedCheckId = debounce(() => { if (!composing) checkId(); }, 400);

    $userId?.addEventListener('compositionstart', () => composing = true);
    $userId?.addEventListener('compositionend', () => { composing = false; debouncedCheckId(); });
    $userId?.addEventListener('input', debouncedCheckId);

    // 이메일 필드 이벤트
    $emailDomainSelect?.addEventListener('change', () => {
        toggleCustomDomainInput();
        checkEmail();
    });
    $emailLocal?.addEventListener('input', debounce(checkEmail, 400));
    $emailDomainCustom?.addEventListener('input', debounce(checkEmail, 400));

    // 비밀번호
    $password?.addEventListener('input', checkPassword);
    $password2?.addEventListener('input', checkPasswordConfirm);

    // 최종 제출 방어
    $form.addEventListener('submit', (e) => {
        // userId 최종 정규화
        if ($userId) $userId.value = normalizeUserId($userId.value);

        // 이메일 최종 합성
        const emailValue = assembleEmail();
        if ($emailHidden) $emailHidden.value = emailValue;

        // 아이디 정책 방어
        if (!$userId || !USERID_RE.test($userId.value)) {
            e.preventDefault();
            alert('아이디는 영문 소문자와 숫자만 사용하여 4~20자로 입력하세요.');
            return;
        }

        // 이메일 선택항목: 값이 있으면 유효해야 함
        if (emailValue && !EMAIL_WHOLE_RE.test(emailValue)) {
            e.preventDefault();
            alert('이메일 형식을 확인해주세요.');
            return;
        }

        if (!(idOK && emailOK && pwOK && pw2OK)) {
            e.preventDefault();
            alert('입력값을 확인해주세요.');
        }
    });

    // ===== 5) 초기 상태 점검 =====
    populateEmailFieldsFromHidden();
    toggleCustomDomainInput();

    checkPassword(); // 비밀번호 초기값 검사
    if ($userId?.value.trim()) checkId();
    if ($emailHidden?.value.trim()) checkEmail();
    if ($password2?.value.trim()) checkPasswordConfirm();
})();
