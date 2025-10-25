<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>오늘의 날씨 & 미세먼지</title>
    <!-- 외부 CSS 연결 -->
    <link rel="stylesheet" href="<c:url value='/css/api/DustWeatherApi.css'/>">
</head>
<body>
<div id="weather-dust-header">불러오는 중...</div>

<!-- 외부 JS 연결 -->
<script src="<c:url value='/js/api/DustWeatherApi.js'/>"></script>
</body>
</html>
