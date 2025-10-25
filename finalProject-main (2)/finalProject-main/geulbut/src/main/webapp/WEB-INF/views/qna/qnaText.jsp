<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html>
<head>
    <title>1:1 문의</title>
    <link rel="stylesheet" href="/css/00_common.css">
    <link rel="stylesheet" href="/css/qna/qnaText.css">
    <link rel="stylesheet" href="/css/header.css">
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

        <!-- 오른쪽 콘텐츠 -->
        <div class="notice-content-box bg-surface rounded shadow-sm p-4" style="width: 100%;">
            <h2 class="mb-4 qna-title"style="color: var(--color-primary);">▣ 1:1 문의
                <div>
                    <c:if test="${qna.userId == pageContext.request.userPrincipal.name}">
                        <!-- 수정 버튼 -->
                        <form action="${pageContext.request.contextPath}/qnaUpdate" method="get" style="display:inline;">
                            <input type="hidden" name="id" value="${qna.id}"/>
                            <button type="submit" class="btn btn-main"
                                    onclick="return confirm('수정하시겠습니까?');">수정</button>
                        </form>

                        <!-- 삭제 버튼 -->
                        <form action="${pageContext.request.contextPath}/qnaDelete" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${qna.id}"/>
                            <button type="submit" class="btn btn-main"
                                    onclick="return confirm('정말 삭제하시겠습니까?');">삭제</button>
                        </form>
                    </c:if>
                </div>
            </h2>

            <p class="tt">제목 : ${qna.title}</p>

            <!-- 메타 정보 -->
            <div class="qna-meta px-3">
                <div class="meta-item"><i class="fa-solid fa-user"></i> <span>${qna.userId}</span></div>
                <div class="meta-item"><i class="fa-solid fa-comment"></i> <span>${totalCommentCount}</span></div>
                <div class="meta-item"><i class="fa-solid fa-eye"></i> <span><c:out value="${qna.viewCount}"/></span></div>
                <div class="meta-item time-item"><i class="fa-regular fa-clock"></i> <span><fmt:formatDate value="${qna.QAt}" pattern="yyyy-MM-dd HH:mm"/></span></div>
            </div>

            <!-- 글 내용 -->
            <div class="px-4 py-4 qna-text">
                ${qna.QContent}
            </div>

            <!-- 댓글 섹션 -->
            <div class="comment-section mt-4">
                <div><p class="comment-title">Comments</p></div>

                <!-- 댓글 입력 -->
                <sec:authorize access="authentication.name == 'admin001'">
                    <div class="comment-input mb-4">
                        <form action="${pageContext.request.contextPath}/qnaComment" method="post">
                            <input type="hidden" name="id" value="${qna.id}"/>
                            <textarea class="comment-textarea" name="aContent" placeholder="댓글을 작성하세요..." rows="3"></textarea>
                            <button type="submit" class="mt-3 btn-comment" style="float: right;"
                                    onclick="return confirm('댓글을 등록하시겠습니까?');">등록</button>
                        </form>
                    </div>
                </sec:authorize>


                <!-- 댓글 리스트 -->
                <div class="comment-list">
                    <c:forEach var="comment" items="${comments}">
                        <div class="comment-item" id="comment-${comment.commentId}">
                            <span class="comment-author">${comment.userId}</span>
                            <span class="comment-date"><fmt:formatDate value="${comment.createdAt}" pattern="yyyy-MM-dd HH:mm"/></span>
                            <p class="comment-text" id="text-${comment.commentId}">${comment.content}</p>

                            <!-- 수정/삭제 버튼 -->
                            <c:if test="${comment.userId == pageContext.request.userPrincipal.name}">
                                <div id="btns-${comment.commentId}" style="margin-top:0.5rem;">
                                    <!-- 수정 버튼 -->
                                    <button type="button" class="btn btn-main btn-sm"
                                            onclick="if(confirm('댓글을 수정하시겠습니까?')) { editComment(${comment.commentId}); }">수정</button>

                                    <!-- 삭제 버튼 -->
                                    <form action="${pageContext.request.contextPath}/qnaCommentDelete" method="post" style="display:inline;">
                                        <input type="hidden" name="commentId" value="${comment.commentId}"/>
                                        <button type="submit" class="btn btn-main btn-sm"
                                                onclick="return confirm('댓글을 삭제하시겠습니까?');">삭제</button>
                                    </form>
                                </div>

                                <!-- 수정용 textarea (숨김) -->
                                <form action="${pageContext.request.contextPath}/qnaCommentUpdate" method="post"
                                      id="form-${comment.commentId}" style="display:none;">
                                    <input type="hidden" name="commentId" value="${comment.commentId}" />
                                    <textarea name="content" rows="3" class="mt-2 comment-textarea">${comment.content}</textarea>
                                    <div style="margin-top:0.5rem;">
                                        <button type="submit" class="btn btn-main btn-sm"
                                                onclick="return confirm('댓글 수정을 저장하시겠습니까?');">저장</button>
                                        <button type="button" class="btn btn-main btn-sm"
                                                onclick="cancelEdit(${comment.commentId})">취소</button>
                                    </div>
                                </form>
                            </c:if>

                        </div>
                    </c:forEach>

                    <!-- 댓글 페이징 버튼 -->
                    <div class="pagination mt-4 text-center">
                        <c:choose>
                            <c:when test="${not empty commentCurrentPage and not empty commentTotalPage}">
                                <c:if test="${commentCurrentPage > 1}">
                                    <a href="?id=${qna.id}&commentPage=${commentCurrentPage - 1}" class="btn btn-light">&laquo; 이전</a>
                                </c:if>

                                <c:forEach begin="1" end="${commentTotalPage}" var="i">
                                    <c:choose>
                                        <c:when test="${i == commentCurrentPage}">
                                            <span class="btn btn-main">${i}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="?id=${qna.id}&commentPage=${i}" class="btn btn-light">${i}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>

                                <c:if test="${commentCurrentPage < commentTotalPage}">
                                    <a href="?id=${qna.id}&commentPage=${commentCurrentPage + 1}" class="btn btn-light">다음 &raquo;</a>
                                </c:if>
                            </c:when>
                        </c:choose>

                    </div>

                </div>

            </div>
        </div>
    </div>
</div>

<script>
    function editComment(commentId) {
        document.getElementById("text-" + commentId).style.display = "none";
        document.getElementById("form-" + commentId).style.display = "block";
        document.getElementById("btns-" + commentId).style.display = "none";
    }

    function cancelEdit(commentId) {
        document.getElementById("text-" + commentId).style.display = "block";
        document.getElementById("form-" + commentId).style.display = "none";
        document.getElementById("btns-" + commentId).style.display = "block";
    }
</script>

</body>
</html>
