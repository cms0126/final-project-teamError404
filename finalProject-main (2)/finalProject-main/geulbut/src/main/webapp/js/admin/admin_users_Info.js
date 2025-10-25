$(function () {
    // 세부검색 토글
    $('#toggleAdvancedSearch').click(function() {
        $('#advancedSearch').slideToggle();
        let isVisible = $('#advancedSearch').is(':visible');
        $(this).text(isVisible ? '세부검색 ▲' : '세부검색 ▼');
    });

    // 행 클릭 시 상세 데이터 토글
    $('.data-row').click(function() {
        $(this).next('.detail-row').slideToggle();
    });

    // 권한, 상태, 포인트, 등급 변경 저장
    $('.save-btn').click(function (e) {
        e.stopPropagation();
        let $row = $(this).closest('tr');
        let userId = $(this).data('userid');

        let newRole = $row.find('.role-select').val();
        let newStatus = $row.find('.status-select').val();
        let newPoint = $row.find('.point-input').val();
        let newGrade = $row.find('.grade-select').val();

        $.ajax({
            url: '/admin/api/users/' + userId + '/info', // 수정된 URL
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({
                newRole: newRole,
                newStatus: newStatus,
                newPoint: newPoint,
                newGrade: newGrade
            }),
            success: function () {
                alert('회원 정보가 변경되었습니다.');
                location.reload();
            },
            error: function () {
                alert('회원 정보 변경 실패');
            }
        });
    });

    // 회원 삭제
    $('.delete-btn').click(function (e) {
        e.stopPropagation();
        if (!confirm('정말 삭제하시겠습니까?')) return;
        let userId = $(this).data('userid');
        $.ajax({
            url: '/admin/api/users/' + userId,
            method: 'DELETE',
            success: function () {
                alert('회원이 삭제되었습니다.');
                location.reload();
            },
            error: function() { alert('회원 삭제 실패'); }
        });
    });

    // 계정 상태 변경 저장 (선택 시 바로 반영)
    $('.status-select').change(function (e) {
        e.stopPropagation();
        let userId = $(this).data('userid');
        let newStatus = $(this).val();
        $.ajax({
            url: '/admin/api/users/' + userId + '/status?newStatus=' + newStatus,
            method: 'PUT',
            success: function () {
                alert('계정 상태가 변경되었습니다.');
                location.reload();
            },
            error: function() { alert('계정 상태 변경 실패'); }
        });
    });

    // 통계 정보 가져오기
    $.ajax({
        url: '/admin/api/users/stats',
        method: 'GET',
        success: function(data) {
            $('#totalUsers').text(data.totalUsers);
            $('#todayNewUsers').text(data.todayNewUsers);
        },
        error: function() { console.error('통계 정보 불러오기 실패'); }
    });
});

// 도서 관리와 동일: 비활성(aria-disabled="true") 페이지네이션 링크 클릭 방지
$(document).on('click', '.pagination-toolbar a[aria-disabled="true"]', function (e) { e.preventDefault(); });
