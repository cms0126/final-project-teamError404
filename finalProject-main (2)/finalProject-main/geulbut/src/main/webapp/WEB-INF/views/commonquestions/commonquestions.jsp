<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<html>
<head>
    <title>공지사항</title>
    <link rel="stylesheet" href="/css/00_common.css">
    <link rel="stylesheet" href="/css/commonquestions/commonquestions.css">
    <link rel="stylesheet" href="/css/header.css">
</head>
<body>
<jsp:include page="/common/header.jsp"></jsp:include>

<div class="page my-3">
    <div class="grid gap-4 commonquestions-layout">
        <!-- 왼쪽 사이드바 -->
        <aside class="sidebar-box bg-surface border rounded p-4">
            <h2 class="mb-3 text-center" style="color: var(--color-primary);">고객센터</h2>
            <nav class="grid gap-2">
                <a href="${pageContext.request.contextPath}/notice" class="text-main" style="color: var(--color-ink);">공지사항</a>
                <a href="${pageContext.request.contextPath}/commonquestions" class="text-light"style="color: var(--color-ink);">자주 묻는 질문</a>
                <a href="${pageContext.request.contextPath}/qna" class="text-light"style="color: var(--color-ink);">1:1 문의</a>
            </nav>
        </aside>

        <!-- 오른쪽 공지사항 콘텐츠 -->
        <div class="notice-content-box bg-surface rounded shadow-sm p-4" style="width: 100%;">
            <h2 class="mb-4 commonquestions-title"style="color: var(--color-primary);">▣ 자주 묻는 질문
                <sec:authorize access="hasRole('ADMIN')">
                    <a href="${pageContext.request.contextPath}/noticeWrite" class="btn btn-main">글쓰기</a>
                </sec:authorize>

            </h2>

            <table class="commonquestions-table border" style="width: 100%;">
                <thead class="bg-main">
                <tr>
                    <th class="py-2 px-3">번호</th>
                    <th class="py-2 px-6">제목</th>
                    <th class="py-2 px-3">글쓴이</th>
                    <th class="py-2 px-3">날짜</th>
                    <th class="py-2 px-3">조회</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="qna" items="${topQnas}">
                    <tr>
                        <td class="py-2 px-3 text-light">${qna.id}</td>
                        <td class="py-2 px-3">
                            <a href="${pageContext.request.contextPath}/qnaText?id=${qna.id}" class="text-main">
                                    ${qna.title}
                            </a>
                        </td>
                        <td class="py-2 text-center">${qna.userId}</td>
                        <td class="py-2 text-center"><fmt:formatDate value="${qna.QAt}" pattern="yyyy-MM-dd"/></td>
                        <td class="py-2 text-center">${qna.viewCount}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>





            </div>

        </div>
    </div>
</div>

</body>
</html>
