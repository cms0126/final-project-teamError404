$(function () {
    const ctx = (typeof window.ctx !== 'undefined' && window.ctx) ? window.ctx : '';

    // ========== 모달 열기/닫기 ==========
    function openOrderModal() {
        $('#orderModal').css('display', 'flex').attr('aria-hidden', 'false');
    }
    function closeOrderModal() {
        $('#orderModal').hide().attr('aria-hidden', 'true');
        $('#orderDetailContent').empty();
    }
    $('#btnCloseOrderModal, #btnOrderModalClose').on('click', closeOrderModal);
    $('#orderModal').on('click', function (e) { if (e.target === this) closeOrderModal(); });
    $(document).on('keydown', function (e) { if (e.key === 'Escape' && $('#orderModal').is(':visible')) closeOrderModal(); });

// ========== 상세보기 ==========
    $('#ordersTableBody').on('click', '.btn-detail', function () {
        const orderId = $(this).data('id');
        if (!orderId) return;

        // (1) 모달을 먼저 띄우고 로딩 메시지 표시
        openOrderModal();
        $('#orderDetailContent')
            .attr('aria-busy', 'true')
            .html('<div style="padding:16px;">불러오는 중...</div>');

        // (2) 데이터 로드해서 모달 내용만 교체
        $.get(`${ctx}/admin/orders/${orderId}`, function (res) {
            let html;
            if (typeof res === 'object') {
                const o = res;
                html = `
              <div><label class="field-label">주문ID</label><div>${o.orderId ?? '-'}</div></div>
              <div><label class="field-label">사용자</label><div>${o.userId ?? '-'} / ${o.userName ?? '-'}</div></div>
              <div><label class="field-label">총액</label><div>${o.totalPrice ?? '-'}</div></div>
              <div><label class="field-label">상태</label><div>${o.status ?? '-'}</div></div>
              <div><label class="field-label">결제수단</label><div>${o.paymentMethod ?? '-'}</div></div>
              <div><label class="field-label">주문번호</label><div>${o.merchantUid ?? '-'}</div></div>
              <div><label class="field-label">수령인</label><div>${o.recipient ?? '-'}</div></div>
              <div><label class="field-label">주소</label><div>${o.address ?? '-'}</div></div>
              <div><label class="field-label">주문/결제/배송</label>
              <div>${o.createdAtFormatted ?? '-'} / ${o.paidAtFormatted ?? '-'} / ${o.deliveredAtFormattedShort ?? '-'}</div>
            </div>

            `;
            } else {
                html = res; // 서버가 HTML로 내려준 경우 그대로 삽입
            }
            $('#orderDetailContent').removeAttr('aria-busy').html(html);
        }).fail(function () {
            $('#orderDetailContent')
                .removeAttr('aria-busy')
                .html('<div style="padding:16px;color:#c00;">주문 상세 조회에 실패했습니다.</div>');
            // 실패 시 즉시 닫고 싶다면 다음 줄 주석 해제:
            // closeOrderModal();
        });
    });


    // ========== 상태 변경 ==========
    $('#ordersTableBody').on('change', '.status-select', function () {
        const $sel = $(this);
        const orderId = $sel.data('id');
        const before = $sel.data('current-status');
        const after  = $sel.val();

        if (!orderId) return;

        if (!confirm(`상태를 '${before}' → '${after}' 로 변경할까요?`)) {
            $sel.val(before);
            return;
        }

        $.ajax({
            url: `${ctx}/admin/orders/${orderId}/status`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({ status: after }),
            success: function () {
                $sel.data('current-status', after);
                alert('상태가 변경되었습니다.');
            },
            error: function (xhr) {
                alert(xhr?.responseJSON?.message ? `변경 실패: ${xhr.responseJSON.message}` : '변경 실패');
                $sel.val(before);
            }
        });
    });

    // ========== 기타: 스크롤 초기화 ==========
    $('.table-scroll').each(function () { this.scrollLeft = 0; });
});
