<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<html>
<head>
    <title>공지사항 상세</title>
    <link rel="stylesheet" href="/css/00_common.css">
    <link rel="stylesheet" href="/css/notice/noticeText.css">
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<jsp:include page="/common/header.jsp"></jsp:include>

<div class="page my-3">
    <div class="grid gap-4 notice-layout">
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
            <h2 class="mb-4 notice-title" style="color: var(--color-primary);">▣ 공지사항
                <div>
                    <sec:authorize access="principal.username == '${notice.userId}'">
                        <!-- 수정 버튼 -->
                        <form action="${pageContext.request.contextPath}/noticeUpdate" method="get" style="display:inline;">
                            <input type="hidden" name="id" value="${notice.noticeId}" />
                            <button type="submit" class="btn btn-main"
                                    onclick="return confirm('수정하시겠습니까?');">수정</button>
                        </form>

                        <!-- 삭제 버튼 -->
                        <form action="${pageContext.request.contextPath}/noticeDelete" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${notice.noticeId}" />
                            <button type="submit" class="btn btn-main"
                                    onclick="return confirm('정말 삭제하시겠습니까?');">삭제</button>
                        </form>
                    </sec:authorize>
                </div>
            </h2>

            <p class="tt">제목 : <c:out value="${notice.title}" /></p>

            <!-- 아이콘 + 텍스트 메타 정보 -->
            <div class="notice-meta px-3">
                <div class="meta-item">
                    <i class="fa-solid fa-user"></i>
                    <span><c:out value="${notice.writer}" /></span>
                </div>
                <div class="meta-item">
                    <i class="fa-solid fa-comment"></i>
                    <span>0</span>
                </div>
                <div class="meta-item">
                    <i class="fa-solid fa-eye"></i>
                    <span><c:out value="${notice.viewCount}" /></span>
                </div>
                <div class="meta-item time-item">
                    <i class="fa-regular fa-clock"></i>
                    <span><fmt:formatDate value="${notice.createdAt}" pattern="yyyy-MM-dd HH:mm" /></span>
                </div>
            </div>

            <!-- 공지사항 글 내용 -->
            <div class="px-4 py-4 notice-text">
                ${notice.content}
            </div>
            <div>
                <p class="comment-title">Comments</p>
            </div>
            <!-- 댓글 구간 (기존 구조 그대로 유지) -->
            <!-- 댓글 입력 -->
            <div class="comment-input mb-4">
                <form action="${pageContext.request.contextPath}/noticeComment" method="post">
                    <input type="hidden" name="noticeId" value="${notice.noticeId}" />
                    <textarea class="comment-textarea" name="content" placeholder="댓글을 작성하세요..." rows="3"></textarea>
                    <button type="submit" class="mt-3 btn-comment" style="float: right;"
                            onclick="return confirm('댓글을 등록하시겠습니까?');">등록</button>
                </form>
            </div>

        <%--댓글 리스트--%>
            <div class="comment-list">
                <c:forEach var="comment" items="${comments}">
                    <div class="comment-item">
                        <span class="comment-author">${comment.userId}</span>
                        <span class="comment-date"><fmt:formatDate value="${comment.createdAt}" pattern="yyyy-MM-dd HH:mm"/></span>

                        <div id="comment-${comment.commentId}">
                            <p class="comment-text">${comment.content}</p>
                        </div>
                            <%--댓글 수정, 삭제--%>
                        <sec:authorize access="principal.username == '${comment.userId}'">
                            <div style="margin-top:0.5rem;">
                                <!-- 수정 버튼 -->
                                <button type="button" class="btn btn-main btn-sm"
                                        onclick="if(confirm('댓글을 수정하시겠습니까?')) { editComment('${comment.commentId}', '${notice.noticeId}', '${comment.content}'); }">수정</button>

                                <!-- 삭제 버튼 -->
                                <form action="${pageContext.request.contextPath}/noticeCommentDelete" method="post" style="display:inline;">
                                    <input type="hidden" name="commentId" value="${comment.commentId}" />
                                    <button type="submit" class="btn btn-main btn-sm"
                                            onclick="return confirm('댓글을 삭제하시겠습니까?');">삭제</button>
                                </form>
                            </div>
                        </sec:authorize>

                    </div>
                </c:forEach>
                <!-- 댓글 페이징 버튼 -->
                <div class="pagination mt-2 text-center">
                    <c:if test="${commentCurrentPage > 1}">
                        <a href="${pageContext.request.contextPath}/noticeText?id=${notice.noticeId}&commentPage=${commentCurrentPage - 1}" class="btn btn-light">&laquo; 이전</a>
                    </c:if>

                    <c:forEach begin="1" end="${commentTotalPage}" var="i">
                        <c:choose>
                            <c:when test="${i == commentCurrentPage}">
                                <span class="btn btn-main">${i}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/noticeText?id=${notice.noticeId}&commentPage=${i}" class="btn btn-light">${i}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <c:if test="${commentCurrentPage < commentTotalPage}">
                        <a href="${pageContext.request.contextPath}/noticeText?id=${notice.noticeId}&commentPage=${commentCurrentPage + 1}" class="btn btn-light">다음 &raquo;</a>
                    </c:if>
                </div>
                <script>
                    function editComment(commentId, noticeId, content) {
                        const div = document.getElementById("comment-" + commentId);

                        // 댓글 원본 HTML 저장 (취소 시 복원용)
                        if (!div.dataset.original) {
                            div.dataset.original = div.innerHTML;
                        }

                        // 수정 폼 만들기
                        const form = document.createElement('form');
                        form.action = '${pageContext.request.contextPath}/noticeCommentUpdate';
                        form.method = 'post';

                        const inputId = document.createElement('input');
                        inputId.type = 'hidden';
                        inputId.name = 'commentId';
                        inputId.value = commentId;

                        const inputNoticeId = document.createElement('input');
                        inputNoticeId.type = 'hidden';
                        inputNoticeId.name = 'noticeId';
                        inputNoticeId.value = noticeId;

                        const textarea = document.createElement('textarea');
                        textarea.name = 'content';
                        textarea.rows = 2;
                        textarea.className = 'mt-2 comment-textarea';
                        textarea.value = content;

                        const saveBtn = document.createElement('button');
                        saveBtn.type = 'submit';
                        saveBtn.className = 'mt-2 mr-1 btn btn-main btn-sm';
                        saveBtn.innerText = '저장';
                        saveBtn.onclick = () => {
                            return confirm('댓글 수정을 저장하시겠습니까?');
                        };


                        const cancelBtn = document.createElement('button');
                        cancelBtn.type = 'button';
                        cancelBtn.className = 'mt-2 btn btn-main btn-sm';
                        cancelBtn.innerText = '취소';
                        cancelBtn.onclick = () => cancelEdit(commentId);

                        form.appendChild(inputId);
                        form.appendChild(inputNoticeId);
                        form.appendChild(textarea);
                        form.appendChild(saveBtn);
                        form.appendChild(cancelBtn);

                        // 댓글 내용 div 교체
                        div.innerHTML = '';
                        div.appendChild(form);

                        // 댓글 버튼 숨기기
                        const btnDiv = div.nextElementSibling;
                        if(btnDiv) btnDiv.style.display = 'none';
                    }

                    function cancelEdit(commentId) {
                        const div = document.getElementById("comment-" + commentId);
                        // 저장해둔 원본 HTML 복원
                        if (div.dataset.original) {
                            div.innerHTML = div.dataset.original;
                        }

                        // 댓글 버튼 다시 보이게
                        const btnDiv = div.nextElementSibling;
                        if(btnDiv) btnDiv.style.display = 'block';
                    }
                </script>


            </div>



        </div>
    </div>
</div>

</body>
</html>
