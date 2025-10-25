/**
 * address.js
 * - 카카오(다음) 우편번호 팝업으로 주소를 채우고, 서버 전송용 hidden address 값을 조립한다.
 * - 의존: Kakao Postcode JS (//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js)
 *
 * 요구되는 DOM id:
 *   #signupForm         (선택) 폼이 있으면 submit 전에 hidden address 병합
 *   #btnFindAddress     (필수) "주소 검색" 버튼
 *   #postcode           (읽기전용) 우편번호
 *   #roadAddress        (읽기전용) 도로명 주소
 *   #jibunAddress       (선택)    지번 주소 (화면에 없으면 생략 가능)
 *   #detailAddress      (사용자 입력) 상세 주소 (동/호 등)
 *   #extraAddress       (읽기전용) 참고 항목 (건물명/법정동, 자동 입력)
 *   #address            (필수, hidden) 서버 제출용 전체 주소 문자열
 *
 * 사용법:
 *   <script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
 *   <script src="/js/address.js"></script>
 */

(() => {
    'use strict';

    // ---- 유틸: 안전한 이벤트 바인딩 ----
    function on(el, type, handler, options) {
        if (el && typeof el.addEventListener === 'function') {
            el.addEventListener(type, handler, options);
        }
    }

    // ---- 초기화 진입 ----
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    function init() {
        // 필드 캐시
        const $form          = document.getElementById('signupForm');      // 선택
        const $btnFind       = document.getElementById('btnFindAddress');  // 필수(클릭으로 팝업 오픈)
        const $postcode      = document.getElementById('postcode');
        const $roadAddress   = document.getElementById('roadAddress');
        const $jibunAddress  = document.getElementById('jibunAddress');    // 없을 수 있음
        const $detailAddress = document.getElementById('detailAddress');
        const $extraAddress  = document.getElementById('extraAddress');
        const $hiddenAddress = document.getElementById('address');         // 필수(hidden)

        // 필수 요소가 없으면 안내(콘솔)
        if (!$btnFind) {
            console.warn('[address.js] #btnFindAddress 버튼이 없어 주소 검색을 열 수 없습니다.');
        }
        if (!$hiddenAddress) {
            console.warn('[address.js] #address(hidden) 필드가 없어 서버 제출용 주소를 조립할 수 없습니다.');
        }

        // ---- hidden address 문자열 조립 ----
        function assembleHiddenAddress() {
            if (!$hiddenAddress) return;

            const parts = [];
            const road  = $roadAddress?.value?.trim();
            const det   = $detailAddress?.value?.trim();
            const ext   = $extraAddress?.value?.trim();
            const jibun = $jibunAddress?.value?.trim();
            const zip   = $postcode?.value?.trim();

            if (road) parts.push(road);
            if (det) parts.push(det);
            if (ext) parts.push(`(${ext})`);
            if (jibun) parts.push(`[지번:${jibun}]`);
            if (zip) parts.push(`우편번호:${zip}`);

            $hiddenAddress.value = parts.join(' ');
        }

        // 상세주소 실시간 변경 시 hidden 최신화
        on($detailAddress, 'input', assembleHiddenAddress);

        // ---- 카카오 우편번호 팝업 열기 ----
        function openPostcode() {
            // 라이브러리 로드 확인
            if (!window.daum || typeof daum.Postcode !== 'function') {
                alert('주소 검색 스크립트를 불러오지 못했습니다. 잠시 후 다시 시도해주세요.');
                console.error('[address.js] Kakao Postcode 라이브러리가 로드되지 않았습니다.');
                return;
            }

            // 팝업 오픈
            new daum.Postcode({
                oncomplete(data) {
                    // 도로명/지번
                    const roadAddr  = data.roadAddress || '';
                    const jibunAddr = data.jibunAddress || '';

                    // 참고항목(법정동/건물명)
                    let extra = '';
                    if (data.bname && /[동|로|가]$/g.test(data.bname)) {
                        extra += data.bname;
                    }
                    if (data.buildingName && data.apartment === 'Y') {
                        extra += (extra ? ', ' : '') + data.buildingName;
                    }

                    // 값 반영
                    if ($postcode)      $postcode.value = data.zonecode || '';
                    if ($roadAddress)   $roadAddress.value = roadAddr;
                    if ($jibunAddress)  $jibunAddress.value = jibunAddr;
                    if ($extraAddress)  $extraAddress.value = extra;

                    // 상세주소 포커스 + hidden 최신화
                    if ($detailAddress) $detailAddress.focus();
                    assembleHiddenAddress();
                }
                // 필요 시 width/height/theme 옵션 확장 가능
            }).open();
        }

        // 버튼 클릭으로 열기
        on($btnFind, 'click', openPostcode);

        // 입력칸 클릭만으로도 열리게 하고 싶다면(선택):
        on($postcode, 'click', openPostcode);
        on($roadAddress, 'click', openPostcode);

        // 폼 제출 직전 최종 병합(폼이 존재할 때만)
        on($form, 'submit', assembleHiddenAddress);

        // 페이지 진입 시에도 한 번 동기화(새로고침 이어쓰기 대비)
        assembleHiddenAddress();
    }

    // 전역 디버깅/테스트를 원할 때 아래 노출을 활성화
    // window.AddressJS = { init: () => init() };
})();
