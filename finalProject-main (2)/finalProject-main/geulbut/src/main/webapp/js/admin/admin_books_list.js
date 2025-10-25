$(function () {
    const ctx = (typeof window.ctx !== 'undefined' && window.ctx) ? window.ctx : '';

    // 🔹 모달 select 옵션 로드
    function loadOptions(callback) {
        $.get(`${ctx}/admin/books/options`, function (res) {
            let authorSelect = $('#authorId');
            let publisherSelect = $('#publisherId');
            let categorySelect = $('#categoryId');

            authorSelect.empty().append('<option value="">선택</option>');
            publisherSelect.empty().append('<option value="">선택</option>');
            categorySelect.empty().append('<option value="">선택</option>');

            res.authors.forEach(a => authorSelect.append(`<option value="${a.authorId}">${a.name}</option>`));
            res.publishers.forEach(p => publisherSelect.append(`<option value="${p.publisherId}">${p.name}</option>`));
            res.categories.forEach(c => categorySelect.append(`<option value="${c.categoryId}">${c.name}</option>`));

            if (callback) callback();
        });
    }

    // 🔹 도서 등록 모달 열기
    $('#btnAddBook').click(function () {
        $('#modalTitle').text('도서 등록');
        $('#bookForm')[0].reset();
        $('#bookId').val('');
        $('#imgPreview').attr('src', '').hide();
        $('#discountedPrice').val(0);
        $('#orderCount').val(0);
        $('#wishCount').val(0);
        $('#rating').val('0.0');
        $('#reviewCount').val(0);

        loadOptions();
        $('#bookModal').css('display', 'flex').attr('aria-hidden', 'false');
    });

    // 🔹 모달 닫기
    function closeBookModal() {
        $('#bookModal').hide().attr('aria-hidden', 'true');
    }
    $('#btnCloseModal, #btnCancel').on('click', closeBookModal);
    $('#bookModal').on('click', function (e) {
        if (e.target === this) closeBookModal();
    });
    $(document).on('keydown', function (e) {
        if (e.key === 'Escape' && $('#bookModal').is(':visible')) closeBookModal();
    });

    // 🔹 등록 / 수정 submit
    $('#bookForm').submit(function (e) {
        e.preventDefault();

        let authorVal = $('#authorId').val();
        let publisherVal = $('#publisherId').val();
        let categoryVal = $('#categoryId').val();

        if (!authorVal) { alert('저자를 선택해주세요.'); return; }
        if (!publisherVal) { alert('출판사를 선택해주세요.'); return; }
        if (!categoryVal) { alert('카테고리를 선택해주세요.'); return; }

        let bookId = $('#bookId').val();
        let method = bookId ? 'PUT' : 'POST';
        let url = bookId ? `${ctx}/admin/books/${bookId}` : `${ctx}/admin/books`;

        let data = {
            bookId: bookId || null,
            title: $('#title').val().trim(),
            isbn: $('#isbn').val().trim(),
            price: parseInt($('#price').val(), 10) || 0,
            discountedPrice: parseInt($('#discountedPrice').val(), 10) || 0,
            stock: parseInt($('#stock').val(), 10) || 0,
            authorId: parseInt(authorVal, 10),
            publisherId: parseInt(publisherVal, 10),
            categoryId: parseInt(categoryVal, 10),
            imgUrl: $('#imgUrl').val().trim(),
            orderCount: parseInt($('#orderCount').val(), 10) || 0,
            wishCount: parseInt($('#wishCount').val(), 10) || 0,
            rating: parseFloat($('#rating').val()) || 0.0,
            reviewCount: parseInt($('#reviewCount').val(), 10) || 0
        };

        if (!data.title) { alert('제목을 입력해주세요.'); return; }
        if (!data.isbn) { alert('ISBN을 입력해주세요.'); return; }
        if (data.price < 0 || data.stock < 0 || data.discountedPrice < 0) { alert('가격, 할인가, 재고는 0 이상이어야 합니다.'); return; }

        const keyword = $('#bookSearchForm input[name="keyword"]').val().trim();

        $.ajax({
            url, method,
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (res) {
                alert('저장 완료');

                if (bookId) {
                    const row = $(`#booksTableBody tr[data-id='${bookId}']`);
                    if (row.length) {
                        row.find('td:eq(1) .title-ellipsis').text(res.title);
                        row.find('td:eq(3) .isbn-mono').text(res.isbn);
                        row.find('td:eq(7)').text(res.price);
                        row.find('td:eq(8)').text(res.discountedPrice ?? '');
                        row.find('td:eq(9)').text(res.stock > 0 ? res.stock : '품절');
                        row.find('td:eq(10)').text(res.orderCount ?? 0);
                        row.find('td:eq(11)').text(res.wishCount ?? 0);
                        row.find('td:eq(12)').text((res.rating ?? 0).toFixed(1));
                        row.find('td:eq(13)').text(res.reviewCount ?? 0);
                    } else {
                        loadBooksPage(0, keyword);
                    }
                } else {
                    loadBooksPage(0, keyword);
                }

                closeBookModal();
            },
            error: function (xhr) {
                if (xhr.responseJSON && xhr.responseJSON.message) alert('저장 실패: ' + xhr.responseJSON.message);
                else alert('저장 실패');
            }
        });
    });

    // 🔹 테이블 버튼 이벤트
    $('#booksTableBody')
        .on('click', '.btnDelete', function () {
            let bookId = $(this).closest('tr').data('id');
            if (!confirm('정말 삭제하시겠습니까?')) return;

            const keyword = $('#bookSearchForm input[name="keyword"]').val().trim();
            $.ajax({
                url: `${ctx}/admin/books/${bookId}`,
                method: 'DELETE',
                success: function () { alert('삭제 완료'); loadBooksPage(0, keyword); },
                error: function () { alert('삭제 실패'); }
            });
        })
        .on('click', '.btnEdit', function () {
            let bookId = $(this).closest('tr').data('id');
            $.get(`${ctx}/admin/books/${bookId}/edit-options`, function (res) {
                let book = res.book;
                $('#modalTitle').text('도서 수정');
                $('#bookId').val(book.bookId);
                $('#title').val(book.title);
                $('#isbn').val(book.isbn);
                $('#price').val(book.price);
                $('#discountedPrice').val(book.discountedPrice || 0);
                $('#stock').val(book.stock);
                $('#imgUrl').val(book.imgUrl || '');
                $('#imgPreview').attr('src', book.imgUrl || '').toggle(!!book.imgUrl);
                $('#orderCount').val(book.orderCount || 0);
                $('#wishCount').val(book.wishCount || 0);
                $('#rating').val(book.rating != null ? book.rating.toFixed(1) : '0.0');
                $('#reviewCount').val(book.reviewCount || 0);

                let authorSelect = $('#authorId').empty().append('<option value="">선택</option>');
                let publisherSelect = $('#publisherId').empty().append('<option value="">선택</option>');
                let categorySelect = $('#categoryId').empty().append('<option value="">선택</option>');

                res.authors.forEach(a => authorSelect.append(`<option value="${a.authorId}">${a.name}</option>`));
                res.publishers.forEach(p => publisherSelect.append(`<option value="${p.publisherId}">${p.name}</option>`));
                res.categories.forEach(c => categorySelect.append(`<option value="${c.categoryId}">${c.name}</option>`));

                $('#authorId').val(book.authorId || '');
                $('#publisherId').val(book.publisherId || '');
                $('#categoryId').val(book.categoryId || '');

                $('#bookModal').css('display', 'flex').attr('aria-hidden', 'false');
            });
        })
        .on('click', '.btnView', function () {
            const bookId = $(this).closest('tr').data('id');
            if (bookId) window.open(ctx + `/book/${bookId}`, '_blank');
        });

    // 🔹 이미지 미리보기
    $('#imgUrl').on('input', function () {
        let url = $(this).val().trim();
        $('#imgPreview').attr('src', url).toggle(!!url);
    });

    // 🔹 검색 + 페이징 갱신
    $('#bookSearchForm').submit(function (e) {
        e.preventDefault();
        const keyword = ($(this).find('input[name="keyword"]').val() || '').trim();
        loadBooksPage(0, keyword);
    });

    // 🔹 AJAX로 책 목록 불러오기 (페이징 포함)
    function loadBooksPage(page, keyword) {
        $.get(`${ctx}/admin/books/search`, { page, keyword }, function (res) {
            const tbody = $('#booksTableBody');
            tbody.empty();

            if (!res.content || res.content.length === 0) {
                tbody.append('<tr><td colspan="16" class="t-center text-light">검색 결과가 없습니다.</td></tr>');
                $('.pagination, .pagination-toolbar').remove();
                return;
            }

            res.content.forEach(book => {
                let row = `
<tr class="data-row" data-id="${book.bookId}">
  <td>${book.bookId}</td>
  <td class="t-left"><div class="title-ellipsis" title="${book.title}">${book.title}</div></td>
  <td>${book.imgUrl ? `<img src="${book.imgUrl}" class="book-thumb" alt="${book.title}"/>` : ''}</td>
  <td class="hide-md"><span class="isbn-mono">${book.isbn}</span></td>
  <td>${book.authorName ?? ''}</td>
  <td class="hide-lg">${book.publisherName ?? ''}</td>
  <td class="hide-lg">${book.categoryName ?? ''}</td>
  <td class="t-center">${book.price}</td>
  <td class="t-center hide-lg">${book.discountedPrice ?? ''}</td>
  <td>
  <span class="stock-chip ${book.stock > 0 ? 'ok' : 'out'}">
    ${book.stock > 0 ? book.stock : '품절'}
  </span>
</td>
  <td>${book.orderCount ?? 0}</td>
  <td>${book.wishCount ?? 0}</td>
  <td>${(book.rating ?? 0).toFixed(1)}</td>
  <td>${book.reviewCount ?? 0}</td>
<td class="hide-lg" data-created="${book.createdAtFormatted}">
    ${book.createdAtFormatted}
</td>
  <td>
    <button type="button" class="btn btn-secondary btnView">상세보기</button>
    <button type="button" class="btn btn-primary btnEdit">수정</button>
    <button type="button" class="btn btn-danger btnDelete">삭제</button>
  </td>
</tr>`;
                tbody.append(row);
            });

            // --- 페이징 ---
            $('.pagination, .pagination-toolbar').remove();
            const $toolbar = $(`<div class="btn-toolbar pagination-toolbar" role="toolbar" aria-label="페이지네이션"><div class="btn-group" role="group" aria-label="페이지"></div></div>`);
            const $group = $toolbar.find('.btn-group');

            const total = res.totalPages || 0;
            const now = res.number || 0;
            const first = res.first || (now === 0);
            const last = res.last || (now === total - 1);

            if (total > 1) {
                $group.append(`<button class="btn btn-secondary btn-nav" ${first ? 'disabled' : ''} data-page="${Math.max(0, now - 1)}">&laquo;</button>`);
                for (let i = 0; i < total; i++) {
                    const isActive = i === now;
                    $group.append(`<button class="btn btn-secondary ${isActive ? 'active' : ''}" data-page="${i}">${i + 1}</button>`);
                }
                $group.append(`<button class="btn btn-secondary btn-nav" ${last ? 'disabled' : ''} data-page="${Math.min(total - 1, now + 1)}">&raquo;</button>`);
                $('.table-scroll').after($toolbar);

                // 페이징 클릭 이벤트
                $group.find('button[data-page]').off('click').on('click', function () {
                    const targetPage = $(this).data('page');
                    loadBooksPage(targetPage, keyword);
                });
            }

            // 스크롤 초기화
            $('.table-scroll').each(function () { this.scrollLeft = 0; });
        });
    }

    // 초기 로드 시 테이블 스크롤 초기화
    $('.table-scroll').each(function () { this.scrollLeft = 0; });

    // 초기 데이터 로드
    loadBooksPage(0, $('#bookSearchForm input[name="keyword"]').val().trim());
});
