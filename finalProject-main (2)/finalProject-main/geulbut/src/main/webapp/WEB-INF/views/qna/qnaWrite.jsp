<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>공지사항</title>
    <link rel="stylesheet" href="/css/00_common.css">
    <link rel="stylesheet" href="/css/qna/qnaWrite.css">
    <link rel="stylesheet" href="/css/header.css">
    <!-- 이미지 아이콘 사용 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<jsp:include page="/common/header.jsp"></jsp:include>

<div class="page my-3">
    <div class="grid gap-4 qna-layout">
        <!-- 왼쪽 사이드바 -->
        <aside class="sidebar-box bg-surface border rounded p-4">
            <h2 class="mb-3 text-center" style="color: var(--color-primary);">고객센터</h2>
            <nav class="grid gap-2">
                <a href="${pageContext.request.contextPath}/notice" class="text-main" style="color: var(--color-ink);">공지사항</a>
                <a href="${pageContext.request.contextPath}/commonquestions" class="text-light"style="color: var(--color-ink);">자주 묻는 질문</a>
                <a href="${pageContext.request.contextPath}/qna" class="text-light"style="color: var(--color-ink);">1:1 문의</a>
            </nav>
        </aside>

        <!-- 오른쪽 1:1 글 작성 -->
        <div class="notice-content-box bg-surface rounded shadow-sm p-4 qna-write">
            <h2 class="mb-4 qna-title"style="color: var(--color-primary);">
                ▣ 1:1 문의 작성
            </h2>

            <form action="${pageContext.request.contextPath}/qnaSubmit" method="post">
                <c:if test="${not empty qna.id}">
                    <input type="hidden" name="id" value="${qna.id}"/>
                </c:if>

                <!-- 제목 -->
                <label for="title" class="tt ml-1">제목</label>
                <input type="text" id="title" name="title" class="form-input mb-5 mt-3"
                       value="${qna.title}" placeholder="제목을 입력하세요" required>

                <!-- 내용 -->
                <label for="content" class="ml-1">내용</label>
                <textarea id="qContent" name="qContent" class="form-textarea mb-3 mt-3"
                          placeholder="문의 내용을 입력하세요" rows="8" required>${qna.QContent}</textarea>

                <!-- 제출 버튼 -->
                <!-- 제출 버튼 -->
                <div class="text-right">
                    <c:choose>
                        <c:when test="${not empty qna.id}">
                            <!-- 수정일 때 -->
                            <button type="submit" class="btn btn-main"
                                    onclick="return confirm('수정하시겠습니까?');">수정</button>
                        </c:when>
                        <c:otherwise>
                            <!-- 등록일 때 -->
                            <button type="submit" class="btn btn-main"
                                    onclick="return confirm('등록하시겠습니까?');">등록</button>
                        </c:otherwise>
                    </c:choose>
                </div>

            </form>

        </div>

    </div>
    </div>
</div>

</body>
</html>
