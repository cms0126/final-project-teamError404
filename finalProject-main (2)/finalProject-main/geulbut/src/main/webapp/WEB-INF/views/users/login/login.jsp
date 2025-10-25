<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>글벗 로그인</title>

    <!-- 공통/페이지 CSS: 컨텍스트패스 안전하게 -->
    <link rel="stylesheet" href="<c:url value='/css/00_common.css'/>">
    <link rel="stylesheet" href="<c:url value='/v/users/login/css/login.css'/>">
</head>
<body>
<main class="page">

    <!-- 브랜드 -->
    <a class="brand" href="<c:url value='/'/>">
        <span class="logo-badge" aria-hidden="true">ㄱㅂ</span>
        <span class="logo-title">글벗</span>
        <span class="logo-sub">Geulbut</span>
    </a>

    <!-- 오류/알림 -->
    <c:if test="${not empty loginError}">
        <div class="alert error">${loginError}</div>
    </c:if>
    <c:if test="${empty loginError and param.error ne null}">
        <div class="alert error">로그인에 실패했습니다. 아이디/비밀번호를 확인해주세요.</div>
    </c:if>
    <c:if test="${param.withdrawn ne null}">
        <p class="notice" style="color: green; font-weight: bold;">
            탈퇴가 완료되었습니다. 이용해 주셔서 감사합니다.
        </p>
    </c:if>

    <section class="grid-2">
        <!-- 좌측: 회원 로그인 카드 -->
        <article class="card" aria-labelledby="loginTitle">
            <div class="card-body" role="tabpanel" aria-labelledby="loginTitle">
                <h1 id="loginTitle" class="sr-only">글벗 회원 로그인</h1>

                <!-- ★ 폼 로그인: /loginProc -->
                <form class="form" method="post" action="<c:url value='/loginProc'/>">
                    <!-- CSRF 사용 시 운영에서 활성화
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    -->

                    <div class="field">
                        <label for="uid">아이디</label>
                        <div class="input">
                            <input id="uid" name="username" type="text" placeholder="아이디를 입력하세요"
                                   autocomplete="username" required>
                        </div>
                    </div>
                    <div class="field">
                        <label for="upw">비밀번호</label>
                        <div class="input">
                            <input id="upw" name="password" type="password" placeholder="비밀번호를 입력하세요"
                                   autocomplete="current-password" required>
                        </div>
                    </div>

                    <div class="options">
                        <label class="opt"><input type="checkbox" name="remember-me"> 로그인 상태 유지</label>
                        <label class="opt"><input type="checkbox" name="save-id"> 아이디 저장</label>
                    </div>

                    <button type="submit" class="btn-primary">로그인</button>

                    <nav class="links" aria-label="부가 링크">
                        <a href="<c:url value='/find-id'/>">아이디 찾기</a>
                        <span class="dot">·</span>
                        <a href="<c:url value='/find-password'/>">비밀번호 찾기</a>
                        <span class="dot">·</span>
                        <a href="<c:url value='/signup'/>">회원가입</a>
                    </nav>

                    <div class="divider">또는 간편 로그인</div>
                    <div class="sns-list" role="group" aria-label="간편 로그인">
                        <a class="sns-btn" href="<c:url value='/oauth2/authorization/naver'/>" aria-label="네이버로 로그인">
                            <img src="<c:url value='/v/users/login/img/naver-icon.png'/>" alt="네이버 로그인" class="sns-img"/>
                        </a>
                        <a class="sns-btn" href="<c:url value='/oauth2/authorization/kakao'/>" aria-label="카카오로 로그인">
                            <img src="<c:url value='/v/users/login/img/kakao-icon.png'/>" alt="카카오 로그인" class="sns-img"/>
                        </a>
                        <a class="sns-btn" href="<c:url value='/oauth2/authorization/google'/>" aria-label="구글로 로그인">
                            <img src="<c:url value='/v/users/login/img/google-icon.png'/>" alt="구글 로그인" class="sns-img"/>
                        </a>
                    </div>
                </form>
            </div>
        </article>

        <!-- 우측 배너 (디자인 개선) -->
        <aside class="banner" aria-label="로그인 안내 배너">
            <figure class="banner__media">
                <img src="<c:url value='/v/users/login/img/login2.jpg'/>" alt="" aria-hidden="true">
            </figure>
            <div class="banner__overlay" aria-hidden="true"></div>
            <div class="banner__inner">
                <p class="banner__badge">Welcome Back</p>
                <h2 class="banner__title">다시 만나는<br>당신의 서재</h2>
                <ul class="banner__bullets" aria-label="로그인 혜택">
                    <li>구매 이력/배송 상태 한눈에</li>
                    <li>등급별 혜택과 포인트 적립</li>
                    <li>관심 도서 맞춤 추천</li>
                </ul>
            </div>
        </aside>
    </section>

</main>

<c:if test="${signupDone}">
    <script>
        alert("회원가입이 완료되었습니다. 로그인 해주세요.");
    </script>
</c:if>
</body>
</html>
