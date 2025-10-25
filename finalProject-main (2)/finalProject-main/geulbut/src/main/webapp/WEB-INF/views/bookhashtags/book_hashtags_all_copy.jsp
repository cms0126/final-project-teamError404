<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Book Hashtags 조회</title>
</head>
<body>

<h1>Book Hashtags 조회</h1>

<form action="/book-hashtags/search" method="get">
    <label>책 검색:</label>
    <input type="text" name="bookQuery" value="${queryBook}" />
    <button type="submit">검색</button>

    <label>해시태그 검색:</label>
    <input type="text" name="hashtagQuery" value="${queryHashtag}" />
    <button type="submit">검색</button>
</form>

<hr/>

<!-- 책 기준 해시태그 목록 -->
<h2>책 검색 결과</h2>
<c:if test="${not empty bookSearchResult}">
    <table border="1">
        <thead>
        <tr>
            <th>책 ID</th>
            <th>책 제목</th>
            <th>해시태그 ID</th>
            <th>해시태그 이름</th>
            <th>연결 생성일</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="bh" items="${bookSearchResult}">
            <tr>
                <td>${bh.book.bookId}</td>
                <td>${bh.book.title}</td>
                <td>${bh.hashtag.hashtagId}</td>
                <td>${bh.hashtag.name}</td>
                <td>${bh.createdAt}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>
<c:if test="${empty bookSearchResult}">
    <p>검색된 책이 없습니다.</p>
</c:if>

<!-- 해시태그 기준 책 목록 -->
<h2>해시태그 검색 결과</h2>
<c:if test="${not empty hashtagSearchResult}">
    <table border="1">
        <thead>
        <tr>
            <th>해시태그 ID</th>
            <th>해시태그 이름</th>
            <th>책 ID</th>
            <th>책 제목</th>
            <th>연결 생성일</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="bh" items="${hashtagSearchResult}">
            <tr>
                <td>${bh.hashtag.hashtagId}</td>
                <td>${bh.hashtag.name}</td>
                <td>${bh.book.bookId}</td>
                <td>${bh.book.title}</td>
                <td>${bh.createdAt}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>
<c:if test="${empty hashtagSearchResult}">
    <p>검색된 해시태그가 없습니다.</p>
</c:if>

</body>
</html>
