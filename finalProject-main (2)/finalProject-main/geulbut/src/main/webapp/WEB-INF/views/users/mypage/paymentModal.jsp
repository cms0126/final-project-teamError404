<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- 결제 전에 띄울 모달 -->
<div class="modal fade" id="orderInfoModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">

      <div class="modal-header">
        <h5 class="modal-title">주문 정보 확인</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
      </div>

      <div class="modal-body">
        <form id="orderForm" class="row g-3" autocomplete="on">
          <!-- 서버/검증/JS에서 실제 사용할 값들 (disabled는 제출 안되므로 hidden으로 별도 전송) -->
          <input type="hidden" name="userId"   value="${fn:escapeXml(user.userId)}"/>
          <input type="hidden" name="userName" value="${fn:escapeXml(user.userName)}"/>
          <input type="hidden" name="email"    value="${fn:escapeXml(user.email)}"/>
          <input type="hidden" name="phone"    value="${fn:escapeXml(user.phone)}"/>
          <input type="hidden" name="mode" id="oiMode" value="CART"/>
          <input type="hidden" name="bookId" id="oiBookId" value=""/>
          <input type="hidden" name="quantity" id="oiQty" value=""/>

          <!-- 주문ID: 표시용(제출 안 함) -->
          <div class="col-12 col-md-6">
            <label class="form-label">주문ID</label>
            <input type="text" class="form-control" id="f-orderId" value="" placeholder="결제 직전에 생성됩니다" disabled>
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">사용자ID</label>
            <input type="text" class="form-control" id="f-userId"
                   value="${fn:escapeXml(user.userId)}" disabled>
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">사용자이름</label>
            <input type="text" class="form-control" id="f-userName"
                   value="${fn:escapeXml(user.userName)}" disabled>
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">회원 이메일</label>
            <input type="email" class="form-control" id="f-email"
                   value="${fn:escapeXml(user.email)}" disabled>
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">전화번호</label>
            <input type="text" class="form-control" id="oiPhone"
                   value="${fn:escapeXml(user.phone)}" disabled>
          </div>

          <div class="col-12">
            <label class="form-label">주소</label>
            <textarea class="form-control" id="oiAddress" name="address" rows="2">${fn:escapeXml(user.address)}</textarea>
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">수령인</label>
            <input type="text" class="form-control" id="oiReceiver" name="recipient"
                   value="${fn:escapeXml(user.userName)}">
          </div>

          <div class="col-12">
            <label class="form-label">메모</label>
            <textarea class="form-control" id="oiMemo" name="memo" rows="2" placeholder="배송 요청사항 등"></textarea>
          </div>

          <!-- 결제수단: radio만 전송 (hidden은 제거) -->
          <div class="col-12">
            <label class="form-label d-block">결제수단</label>
            <div class="form-check form-check-inline">
              <input class="form-check-input" type="radio" name="paymentMethod" id="payCard" value="card" checked>
              <label class="form-check-label" for="payCard">카드</label>
            </div>
            <%-- 필요 시 다른 수단 추가
            <div class="form-check form-check-inline">
              <input class="form-check-input" type="radio" name="paymentMethod" id="payVbank" value="vbank">
              <label class="form-check-label" for="payVbank">무통장</label>
            </div>
            --%>
          </div>

          <div class="col-12 text-end">
            <h5 class="mb-0">총액: <span id="oiTotal">0</span> 원</h5>
          </div>
        </form>
      </div> <!-- /.modal-body -->

      <div class="modal-footer">
        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">취소</button>
        <button type="button" class="btn btn-primary" id="oi-confirm" data-role="oi-confirm">결제 진행</button>
      </div>

    </div> <!-- /.modal-content -->
  </div>   <!-- /.modal-dialog -->
</div>     <!-- /.modal -->
