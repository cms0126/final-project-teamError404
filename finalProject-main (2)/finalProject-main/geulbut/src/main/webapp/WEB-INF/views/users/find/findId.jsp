<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>아이디 찾기</title>
    <link rel="stylesheet" href="/css/00_common.css">
    <link rel="stylesheet" href="/css/find/find-id.css">

</head>
<body>
<div class="findid-wrapper">
    <div class="findid-card">
        <h1 class="findid-title">아이디 찾기</h1>

        <form class="findid-form" action="<c:url value='/find-id'/>" method="post">
            <div class="row">
                <label>이름</label>
                <input type="text" name="name" value="${param.name}">
            </div>
            <div class="row">
                <label>이메일</label>
                <input type="email" name="email" value="${param.email}">
            </div>
            <div class="findid-actions">
                <button class="findid-btn ghost" type="button" onclick="location.href='<c:url value="/find-password"/>'">비밀번호 찾기</button>
                <button class="findid-btn primary" type="submit">아이디 찾기</button>
            </div>
<%--분기 나눔 찾은아이디--%>
            <c:if test="${not empty foundUserId}">
                <div class="findid-result">
                    <p>찾은 아이디: <strong>${foundUserId}</strong></p>

                    <!-- 소셜 전용 계정 안내 -->
                    <c:if test="${isSocial == true}">
                        <c:choose>
                            <c:when test="${provider eq 'kakao'}">
                                <p class="hint">이 계정은 <strong>카카오 간편로그인 전용</strong>이에요.</p>
                                <a class="findid-btn kakao" href="<c:url value='/oauth2/authorization/kakao'/>">카카오로 로그인</a>
                            </c:when>
                            <c:when test="${provider eq 'naver'}">
                                <p class="hint">이 계정은 <strong>네이버 간편로그인 전용</strong>이에요.</p>
                                <a class="findid-btn naver" href="<c:url value='/oauth2/authorization/naver'/>">네이버로 로그인</a>
                            </c:when>
                            <c:when test="${provider eq 'google'}">
                                <p class="hint">이 계정은 <strong>구글 간편로그인 전용</strong>이에요.</p>
                                <a class="findid-btn google" href="<c:url value='/oauth2/authorization/google'/>">구글로 로그인</a>
                            </c:when>
                            <c:otherwise>
                                <!-- provider를 특정 못하는(머지된) 케이스: 3사 버튼 모두 노출 -->
                                <p class="hint">이 계정은 <strong>간편로그인 전용</strong>이에요. 가입하신 소셜로 로그인해 주세요.</p>
                                <div class="social-buttons">
                                    <a class="findid-btn kakao" href="<c:url value='/oauth2/authorization/kakao'/>">카카오</a>
                                    <a class="findid-btn naver" href="<c:url value='/oauth2/authorization/naver'/>">네이버</a>
                                    <a class="findid-btn google" href="<c:url value='/oauth2/authorization/google'/>">구글</a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:if>

                    <!-- 일반(폼) 계정이면 비번 찾기 유도 -->
                    <c:if test="${isSocial != true}">
                        <a class="findid-btn ghost" href="<c:url value='/find-password'/>">비밀번호 찾기</a>
                    </c:if>
                </div>
            </c:if>

            <c:if test="${not empty findIdError}">
                <p class="findid-error">${findIdError}</p>
            </c:if>

        </form>

        <div class="findid-actions" style="margin-top:10px">
            <a class="findid-btn ghost" href="<c:url value='/signup'/>">회원가입</a>
        </div>
    </div>
</div>
</body>
</html>
