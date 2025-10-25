<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <title>회원가입</title>
    <link rel="stylesheet" href="/css/00_common.css">
    <link rel="stylesheet" href="/css/signup/signup.css">
</head>
<body>
<div class="signup-wrapper">
    <div class="signup-card">
        <h1 class="signup-title">ㄱㅂ</h1>

        <c:if test="${not empty signupError}">
            <p class="error-msg">${signupError}</p>
        </c:if>

        <form class="signup-form" action="<c:url value='/signup'/>" method="post" id="signupForm">
            <!-- CSRF -->
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="input-group">
                <input id="userId" type="text" name="userId" placeholder="아이디" value="${usersSignupDto.userId}"
                    autocomplete="username"
                       inputmode="latin"
                       maxlength="20"
                       pattern="^[a-z0-9]{4,20}$"
                />
                <span id="userIdMsg" class="help-msg"></span>
            </div>

            <div class="input-group">
                <input id="password"
                       type="password"
                       name="password"
                       placeholder="비밀번호"
                       autocomplete="new-password"
                />
                <span id="passwordMsg" class="help-msg"></span>
            </div>

            <div class="input-group">
                <input id="password2"
                       type="password"
                       name="passwordConfirm"
                       placeholder="비밀번호 확인"
                       autocomplete="new-password"
                />
                <span id="password2Msg" class="help-msg"></span>
            </div>


            <div class="input-group">
                <input type="text" name="name" placeholder="이름" value="${usersSignupDto.name}"/>
            </div>

            <div class="input-group email-group">
                <div class="email-row">
                    <input id="emailLocal" type="text" placeholder="이메일 아이디(예: user)"
                           autocomplete="email" />
                    <span class="at" aria-hidden="true">@</span>

                    <select id="emailDomainSelect">
                        <option value="">이메일 선택</option>
                        <option value="gmail.com">gmail.com</option>
                        <option value="naver.com">naver.com</option>
                        <option value="daum.net">daum.net</option>
                        <option value="kakao.com">kakao.com</option>
                        <option value="outlook.com">outlook.com</option>
                        <option value="_custom">직접입력</option>
                    </select>

                    <input id="emailDomainCustom" type="text" placeholder="도메인 직접입력(예: example.com)"
                           style="display:none" />
                </div>

                <!-- 백엔드 제출용(합쳐서 여기로) -->
                <input id="email" type="hidden" name="email" value="${usersSignupDto.email}"/>

                <span id="emailMsg" class="help-msg"></span>
            </div>

            <div class="input-group">
                <input type="text" name="phone" placeholder="전화번호" value="${usersSignupDto.phone}"/>
            </div>

            <div class="input-group address-group">
                <div class="addr-row">
                    <input id="postcode" type="text" placeholder="우편번호" readonly />
                    <button type="button" id="btnFindAddress" class="btn-find">주소 검색</button>
                </div>

                <div class="addr-row">
                    <input id="roadAddress" type="text" placeholder="도로명 주소" readonly />
                </div>

                <div class="addr-row">
                    <input id="detailAddress" type="text" placeholder="상세 주소 (동/호수 등)" />
                </div>

                <div class="addr-row">
                    <input id="extraAddress" type="text" placeholder="참고 항목(건물명 등, 자동입력)" readonly />
                </div>

                <!-- 백엔드 호환을 위해 기존 address 필드는 hidden으로 유지 -->
                <input id="address" type="hidden" name="address" value="${usersSignupDto.address}"/>
            </div>

            <div class="input-group">
                <input type="date" name="birthday" value="${usersSignupDto.birthday}"/>
            </div>

            <div class="form-row">
                <label class="select-label">성별:
                    <c:set var="g" value="${usersSignupDto.gender}" />
                    <select name="gender">
                        <option value="M" ${fn:toUpperCase(g) eq 'M' ? 'selected="selected"' : ''}>남</option>
                        <option value="F" ${fn:toUpperCase(g) eq 'F' ? 'selected="selected"' : ''}>여</option>
                    </select>
                </label>
                <div class="checkbox-group">
                    <input type="hidden" name="_postNotifyAgree" value="on"/>
                    <label class="checkbox-label">
                        <input type="checkbox" name="postNotifyAgree" value="true"
                        ${usersSignupDto.postNotifyAgree ? 'checked' : ''}/> 알림 수신
                    </label>

                    <input type="hidden" name="_promoAgree" value="on"/>
                    <label class="checkbox-label">
                        <input type="checkbox" name="promoAgree" value="true"
                        ${usersSignupDto.promoAgree ? 'checked' : ''}/> 프로모션 수신
                    </label>
                </div>
            </div>

            <button id="submitBtn" type="submit" class="submit-btn" disabled>회원가입</button>
        </form>
    </div>
</div>
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src="/js/sign_up/address.js" defer></script>

<script src="/js/sign_up/signup.js" defer></script>
</body>
</html>
