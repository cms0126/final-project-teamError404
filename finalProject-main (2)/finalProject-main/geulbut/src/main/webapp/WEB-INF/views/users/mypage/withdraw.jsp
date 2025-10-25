<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>회원 탈퇴</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="/css/00_common.css">
</head>
<body>


<div class="container my-5">
  <h1 class="mb-4">회원 탈퇴</h1>
  <div class="alert alert-warning">
    정말 탈퇴하시겠습니까? 탈퇴 후에는 계정이 비활성화되며 로그인할 수 없습니다.
  </div>

  <form action="<c:url value='/users/withdraw'/>" method="post" class="d-flex gap-2">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <button type="submit" class="btn btn-danger">네, 탈퇴하겠습니다</button>
    <a class="btn btn-secondary" href="<c:url value='/mypage'/>">취소</a>
  </form>
</div>


</body>
</html>
