<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <%-- 이건 미세먼지 api 데이터 전송 확인용입니다. 수정금지 --%>
  <title>미세먼지 API 확인</title>
  <!-- 외부 CSS 연결 -->
  <link rel="stylesheet" href="<c:url value='/css/api/dustApi.css'/>">
</head>
<body>
<div id="dust-ticker">미세먼지API확인용페이지</div>

<!-- 외부 JS 연결 -->
<script src="<c:url value='/js/api/dustapi.js'/>"></script>
</body>
</html>
