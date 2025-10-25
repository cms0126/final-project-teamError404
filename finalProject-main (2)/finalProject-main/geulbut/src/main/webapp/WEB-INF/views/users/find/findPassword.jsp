<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>비밀번호 찾기</title>
    <link rel="stylesheet" href="<c:url value='/css/00_common.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/find/find-password.css'/>">

    <!-- CSRF 메타 (활성화된 경우에만) -->
    <c:if test="${not empty _csrf}">
        <meta name="_csrf" content="${_csrf.token}"/>
        <meta name="_csrf_header" content="${_csrf.headerName}"/>
    </c:if>
</head>
<body>
<div class="findpw-card">
    <h1 class="findpw-title">비밀번호 찾기</h1>

    <!-- ✅ SMS 탭/패널 제거 → 단일 이메일 패널만 유지 -->
    <div id="tab-email" class="tab-panel active">

        <!-- 이메일 검증 폼 -->
        <form id="emailVerifyForm" method="post" action="<c:url value='/find-password/email/verify'/>">
            <c:if test="${not empty _csrf}">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </c:if>

            <div class="row">
                <label for="emailField">이메일</label>
                <div class="input-group">
                    <input id="emailField" name="email" type="email" placeholder="example@domain.com" required>
                    <button id="emailSendBtn" class="btn ghost" type="button" onclick="sendEmailCode()">인증코드 전송</button>
                </div>
                <div class="hint-row">
                    <span class="hint">가입 시 등록한 이메일 주소</span>
                    <span id="emailTimer" class="timer" aria-live="polite"></span>
                </div>
                <div id="emailSendMsg" class="msg" aria-live="polite"></div>
            </div>

            <div class="row">
                <label for="emailCodeField">인증코드</label><br/>
                <input id="emailCodeField" class="code-input" name="code" type="text" maxlength="6" pattern="[0-9]{6}"
                       inputmode="numeric" autocomplete="one-time-code"
                       placeholder="6자리 숫자" style="width:100%;" required>
            </div>

            <div class="actions">
                <button class="btn primary" type="submit">인증 & 임시 비밀번호 발급</button>
            </div>

            <c:if test="${not empty resetPwMsg}">
                <p class="msg success">${resetPwMsg}</p>
            </c:if>
            <c:if test="${not empty resetPwError}">
                <p class="msg error">${resetPwError}</p>
            </c:if>
        </form>
    </div>

    <div class="actions" style="margin-top:16px;">
        <button class="btn ghost" type="button" onclick="location.href='<c:url value="/find-id"/>'">아이디 찾기</button>
        <button class="btn ghost" type="button" onclick="location.href='<c:url value="/login"/>'">로그인</button>
    </div>

    <!-- 임시 비밀번호 안내 박스 (이메일 검증 후 서비스가 temp 반환 시 표시) -->
    <c:if test="${not empty resetPw}">
        <hr style="margin:16px 0;">
        <div class="msg success">임시 비밀번호가 발급되었습니다. 아래 비밀번호로 로그인해 주세요.</div>

        <div class="temp-box">
            <div class="temp-row">
                <input id="issuedTempPw" class="temp-input" type="text" readonly value="${resetPw}">
                <button type="button" class="btn ghost" onclick="copyTempPw()">복사</button>
            </div>
            <div class="hint" style="margin-top:6px;">보안을 위해 로그인 후 반드시 비밀번호를 변경해 주세요.</div>
        </div>
    </c:if>
</div>

<script>
    /* --- CSRF 안전 처리 --- */
    function csrf() {
        const t = document.querySelector('meta[name="_csrf"]')?.content || null;
        const h = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
        return { token: t, header: h };
    }

    /* --- 공통: POST(JSON) (세션 쿠키 포함) --- */
    async function postJson(url, body) {
        const headers = { 'Content-Type': 'application/json' };
        const { token, header } = csrf();
        if (token) headers[header] = token; // 토큰 있을 때만 추가
        return fetch(url, {
            method: 'POST',
            headers,
            credentials: 'same-origin',   // 세션 쿠키 전송
            redirect: 'follow',
            body: JSON.stringify(body)
        });
    }

    /* --- 타이머/쿨타임 유틸 (JSP-EL 충돌 방지 버전) --- */
    function clearTimer(spanEl, btnEl, origText){
        if (spanEl && spanEl._timer) { clearInterval(spanEl._timer); spanEl._timer = null; }
        if (btnEl && btnEl._cool) { clearInterval(btnEl._cool); btnEl._cool = null; }
        if (btnEl){ btnEl.disabled = false; btnEl.innerText = origText || '인증코드 전송'; }
        if (spanEl){ spanEl.textContent = ''; }
    }

    function startTimer(spanEl, buttonEl, ttlSec = 180, cooldownSec = 60, label = '남은시간'){
        const startedAt = Date.now();

        // --- 남은시간 표시 (즉시 1회 + 주기적 갱신) ---
        function setRemain(remain){
            const mm = String(Math.floor(remain / 60)).padStart(2, '0');
            const ss = String(remain % 60).padStart(2, '0');
            spanEl.textContent = remain > 0 ? (label + ' ' + mm + ':' + ss) : '';
        }
        if (spanEl && spanEl._timer) clearInterval(spanEl._timer);
        setRemain(ttlSec); // 버튼 누르자마자 보이게

        spanEl._timer = setInterval(function(){
            const elapsed = Math.floor((Date.now() - startedAt) / 1000);
            const remain = Math.max(0, ttlSec - elapsed);
            setRemain(remain);
            if (remain <= 0) clearInterval(spanEl._timer);
        }, 250);

        // --- 재전송 카운트다운 ---
        buttonEl.disabled = true;
        var cd = cooldownSec;
        var origText = buttonEl.innerText;

        // 첫 화면부터 숫자 보이게 즉시 반영
        buttonEl.innerText = '재전송(' + cd + ')';

        if (buttonEl._cool) clearInterval(buttonEl._cool);
        buttonEl._cool = setInterval(function(){
            cd--;
            if (cd > 0){
                buttonEl.innerText = '재전송(' + cd + ')';
            } else {
                buttonEl.innerText = origText;
                buttonEl.disabled = false;
                clearInterval(buttonEl._cool);
            }
        }, 1000);

        return origText; // 실패 시 복원용
    }

    /* --- 이메일 코드 전송 (낙관적 시작 + 실패 롤백) --- */
    async function sendEmailCode() {
        const emailField = document.querySelector('#emailVerifyForm [name=email]');
        const email = emailField.value.trim();
        const msg = document.getElementById('emailSendMsg');
        const btn = document.getElementById('emailSendBtn');
        const timer = document.getElementById('emailTimer');

        msg.className = 'msg'; msg.textContent = '';
        if (!email) { msg.classList.add('error'); msg.textContent = '이메일을 입력해주세요.'; return; }

        // 1) 즉시 타이머 시작
        const origBtnText = startTimer(timer, btn, 180, 60);

        try {
            const res = await postJson('<c:url value="/find-password/email/code"/>', { email });
            const text = await res.text();

            if (res.ok) {
                msg.classList.add('success');
                msg.textContent = '인증코드를 전송했습니다. 3분 내에 입력하세요.';
                return; // 타이머 유지
            }

            // 2) 실패: 롤백 + 에러
            clearTimer(timer, btn, origBtnText);
            msg.classList.add('error');
            if (res.status === 403) msg.textContent = '요청이 거부되었습니다(403). CSRF 또는 로그인 세션을 확인해주세요.';
            else if (res.status === 429) msg.textContent = '요청이 너무 많습니다. 잠시 후 다시 시도해주세요.';
            else msg.textContent = text || '전송에 실패했습니다.';
        } catch (e) {
            clearTimer(timer, btn, origBtnText);
            msg.classList.add('error');
            msg.textContent = '네트워크 오류로 전송에 실패했습니다.';
        }
    }

    /* --- (선택) 이메일 필드에서 Enter 눌러도 전송 버튼 로직 실행 --- */
    document.getElementById('emailVerifyForm')?.addEventListener('keydown', (e)=>{
        if (e.key === 'Enter' && e.target?.name === 'email') {
            e.preventDefault();
            sendEmailCode();
        }
    });

    /* --- 임시비밀번호 복사 --- */
    function copyTempPw(){
        const el = document.getElementById('issuedTempPw');
        if(!el) return;
        el.select(); el.setSelectionRange(0, 99999);
        try { navigator.clipboard.writeText(el.value); }
        catch(e) { document.execCommand('copy'); }
        alert('복사되었습니다.');
    }
</script>
</body>
</html>
