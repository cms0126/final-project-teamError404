console.log('[authors] JS loaded?', new Date().toISOString());

$(function () {
    const $modal = $('#authorModal');
    const $modalTitle = $('#modalTitle');
    const $id = $('#modalAuthorId');
    const $name = $('#modalAuthorName');
    const $img = $('#modalAuthorImgUrl');
    const $created = $('#modalAuthorCreatedAt');
    const $desc = $('#modalAuthorDescription');
    const $preview = $('#modalAuthorImgPreview');

    const $booksModal = $('#authorBooksModal');
    const $booksList = $('#booksList');

    // ✅ 도서 페이지와 동일한 방식으로 ctx 사용
    const ctx = (typeof window.ctx !== 'undefined' && window.ctx) ? window.ctx : '';

    // 이미지 미리보기(디바운스)
    let previewTimer = null;
    $img.on('input', function () {
        const url = $(this).val().trim();
        clearTimeout(previewTimer);
        previewTimer = setTimeout(() => { $preview.attr('src', url || ''); }, 120);
    });

    // 모달 접근성/열고닫기(Books 톤과 동일)
    function openModal($m) { $m.css('display', 'flex').attr('aria-hidden', 'false'); }
    function closeModal($m) { $m.hide().attr('aria-hidden', 'true'); }

    $modal.off('click').on('click', e => { if (e.target.id === 'authorModal') closeModal($modal); });
    $booksModal.off('click').on('click', e => { if (e.target.id === 'authorBooksModal') closeModal($booksModal); });
    $(document).off('keydown.adminAuthorsEsc').on('keydown.adminAuthorsEsc', e => {
        if (e.key === 'Escape') { if ($modal.is(':visible')) closeModal($modal); if ($booksModal.is(':visible')) closeModal($booksModal); }
    });
    $('#btnCloseModal, #modalCloseBtn2').off('click').on('click', () => closeModal($modal));
    $('#btnCloseBooksModal').off('click').on('click', () => closeModal($booksModal));

    // 등록 모달
    $(document).off('click.openAuthorCreate', '#btnAddAuthor').on('click.openAuthorCreate', '#btnAddAuthor', function () {
        $modalTitle.text('작가 등록');
        $id.val(''); $name.val(''); $img.val(''); $created.val(''); $desc.val(''); $preview.attr('src', '');
        openModal($modal);
    });

    // 수정 모달
    $(document).off('click.openAuthorEdit', '.btnEdit').on('click.openAuthorEdit', '.btnEdit', function () {
        const $tr = $(this).closest('tr');
        const row = {
            id: $tr.data('id') || '',
            name: $tr.data('name') || '',
            imgUrl: $tr.data('imgurl') || '',
            createdAt: $tr.data('createdat') || $tr.find('.created-at-cell').text() || '',
            desc: $tr.data('description') || ''
        };
        $modalTitle.text('작가 수정');
        $id.val(row.id); $name.val(row.name); $img.val(row.imgUrl);
        $created.val(row.createdAt); $desc.val(row.desc); $preview.attr('src', row.imgUrl);
        openModal($modal);
    });

    // 저장 (등록/수정)
    $('#modalSaveBtn').off('click').on('click', function () {
        const authorId = $id.val();
        const payload = { name: $name.val().trim(), description: $desc.val().trim(), imgUrl: $img.val().trim() };
        if (!payload.name) { alert('작가명을 입력해주세요.'); $name.focus(); return; }
        if (payload.imgUrl) { try { new URL(payload.imgUrl); } catch (e) { alert('이미지 URL 형식이 올바르지 않습니다.'); $img.focus(); return; } }

        const url = authorId ? `${ctx}/admin/authors/${authorId}` : `${ctx}/admin/authors`;
        const method = authorId ? 'PUT' : 'POST';
        const $btn = $(this).prop('disabled', true);
        $.ajax({
            url, method, contentType: 'application/json', data: JSON.stringify(payload),
            success: () => { alert('저장 완료'); location.reload(); },
            error: xhr => { const msg = (xhr.responseJSON && xhr.responseJSON.message) ? xhr.responseJSON.message : '저장 실패'; alert(msg); },
            complete: () => { $btn.prop('disabled', false); }
        });
    });

    // 삭제
    $(document).off('click.authorDelete', '.btnDelete').on('click.authorDelete', '.btnDelete', function () {
        const id = $(this).closest('tr').data('id');
        if (!id) return; if (!confirm('정말 삭제하시겠습니까?')) return;

        $.ajax({
            url: `${ctx}/admin/authors/${id}`,
            method: 'DELETE',
            success: () => { alert('삭제 완료'); location.reload(); },
            error: () => { alert('삭제 실패'); }
        });
    });

    // 작가 이름 클릭 → 책 목록 모달
    $(document).off('click.authorBooks', '.author-name').on('click.authorBooks', '.author-name', function () {
        const authorId = $(this).closest('tr').data('id');
        if (!authorId) return;

        $.ajax({
            url: `${ctx}/admin/authors/${authorId}/books`,
            method: 'GET',
            success: function (books) {
                if (!books || books.length === 0) { alert('이 작가의 책이 없습니다.'); return; }
                $booksList.empty();
                books.forEach(b => { $booksList.append(`<li>${b.title}</li>`); });
                $('#booksModalTitle').text('작가 책 목록');
                openModal($booksModal);
            },
            error: function () { alert('책 목록 불러오기 실패'); }
        });
    });

    // (도서 페이지와 동일) 스크롤 초기화로 좌측부터 보이도록
    $('.table-scroll').each(function () { this.scrollLeft = 0; });

    // 검색/페이징은 서버 렌더를 유지(백엔드 변경 없이)
    $('#authorSearchForm').on('submit', function () { /* 기본 submit 유지 */ });
});
