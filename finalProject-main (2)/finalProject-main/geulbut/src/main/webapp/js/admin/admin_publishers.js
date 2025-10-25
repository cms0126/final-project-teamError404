// /js/admin/admin_publishers.js
$(function () {
    const $modal = $('#publisherModal');
    const $modalTitle = $('#modalTitle');

    const $id = $('#modalPublisherId');
    const $name = $('#modalPublisherName');
    const $desc = $('#modalPublisherDescription');

    const $booksModal = $('#publisherBooksModal');
    const $booksModalDialog = $('#publisherBooksModal > div');
    const $booksModalTitle = $('#booksModalTitle');
    const $booksTableBody = $('#publisherBooksTable tbody');

    // (선택) ctx 지원
    const ctx = (typeof window.ctx !== 'undefined' && window.ctx) ? window.ctx : '';

    // 공통 알림
    function showMessage(msg) {
        alert(msg);
    }

    // ---------------- 모달 열고/닫기 ----------------
    function openModal() {
        $modal.css('display', 'flex').attr('aria-hidden', 'false');
    }

    function closeModal() {
        $modal.hide().attr('aria-hidden', 'true');
    }

    function openBooksModal(){
        $booksModal.css('display','flex').attr('aria-hidden','false');
    }

    function closeBooksModal() {
        $booksModal.hide().attr('aria-hidden', 'true');
    }

    // 배경 클릭/ESC 닫기 + 닫기 버튼
    $modal.off('click').on('click', function (e) {
        if (e.target.id === 'publisherModal') closeModal();
    });
    $booksModal.off('click').on('click', function (e) {
        if (e.target.id === 'publisherBooksModal') closeBooksModal();
    });

    $(document).off('keydown.pubEsc').on('keydown.pubEsc', function (e) {
        if (e.key === 'Escape') {
            closeModal();
            closeBooksModal();
        }
    });
    $('#btnCloseModal, #btnCancel').off('click').on('click', closeModal);
    $('#btnCloseBooksModal, #btnCloseBooksModalFooter').off('click').on('click', closeBooksModal);

    // ---------------- 출판사 등록/수정 ----------------
    $(document).off('click.pubAdd', '#btnAddPublisher').on('click.pubAdd', '#btnAddPublisher', function () {
        $modalTitle.text('출판사 등록');
        $id.val('');
        $name.val('');
        $desc.val('');
        openModal();
    });


    // 수정 모달 열기 (위임 바인딩, 기존 클래스 유지: .btn-edit)
     $(document).off('click.pubEdit', '.btnEdit, .btn-edit')
       .on('click.pubEdit', '.btnEdit, .btn-edit', function (e) {
       e.stopPropagation();

        const $row = $(this).closest('tr');
        const id = $row.data('id');
        const name = $row.find('.publisher-name').text();
        const desc = $row.find('.publisher-description').text();

        $modalTitle.text('출판사 수정');
        $id.val(id);
        $name.val(name);
        $desc.val(desc);
        openModal();
    });

    $('#publisherForm').off('submit').on('submit', function (e) {
        e.preventDefault();

        const id = $id.val();
        const data = {
            name: ($name.val() || '').trim(),
            description: ($desc.val() || '').trim()
        };
        if (!data.name) {
            showMessage('출판사 이름을 입력하세요.');
            $name.focus();
            return;
        }

        const url = id ? `${ctx}/admin/publishers/${id}` : `${ctx}/admin/publishers`;
        const method = id ? 'PUT' : 'POST';

        const $btn = $('#modalSaveBtn').prop('disabled', true);
        $.ajax({
            url, method,
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function () {
                showMessage(id ? '수정 완료' : '등록 완료');
                location.reload();
            },
            error: function () {
                showMessage(id ? '수정 실패' : '등록 실패');
            },
            complete: function () {
                $btn.prop('disabled', false);
            }
        });
    });

    // ---------------- 삭제 ----------------
    $(document).off('click.pubDelete', '.btn-delete, .btnDelete')
                .on('click.pubDelete',  '.btn-delete, .btnDelete', function (e) {
        e.stopPropagation();
        if (!confirm('정말 삭제하시겠습니까?')) return;
        const id = $(this).closest('tr').data('id');
        $.ajax({
            url: `${ctx}/admin/publishers/${id}`,
            method: 'DELETE',
            success: function () {
                showMessage('삭제 완료');
                location.reload();
            },
            error: function () {
                showMessage('삭제 실패');
            }
        });
    });

    // ---------------- 출판사별 책 목록 모달 ----------------
    $('#publishersTableBody').on('click', 'tr.data-row', function (e) {
        if ($(e.target).hasClass('btnEdit') || $(e.target).hasClass('btnDelete')) return;

        const publisherId = $(this).data('id');
        const publisherName = $(this).data('name');

        $booksModalTitle.text(`${publisherName} - 등록된 책 목록`);
        $booksTableBody.empty();

        $.ajax({
            url: `${ctx}/admin/publishers/${publisherId}/books`,
            type: 'GET',
            dataType: 'json',
            success: function (data) {
                if (!data || data.length === 0) {
                    $booksTableBody.append('<tr><td colspan="3">등록된 책이 없습니다.</td></tr>');
                    return;
                }
                data.forEach(book => {
                    $booksTableBody.append(`
                        <tr>
                            <td>${book.bookId}</td>
                            <td>${book.title}</td>
                            <td>${book.authorName}</td>
                        </tr>
                    `);
                });
                openBooksModal();
            },
            error: function () {
                showMessage('책 데이터를 불러오는데 실패했습니다.');
            }
        });
    });

    // ---------------- 검색/페이징 (기본 GET 활용) ----------------
    // $('#publisherSearchForm').on('submit', function(){ /* 기본 GET 유지 */ });
});
