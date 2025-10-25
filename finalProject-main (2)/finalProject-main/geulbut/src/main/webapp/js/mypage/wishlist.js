// ✅ 위시리스트 → 장바구니 담기
function addToCart(bookId, btn) {
    fetch('/cart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-CSRF-TOKEN': window.csrfToken
        },
        body: 'bookId=' + bookId + '&quantity=1'
    })
        .then(res => res.json())
        .then(data => {
            if (data.status === 'ok') {
                alert('장바구니에 담겼습니다 ✅');

                // 👉 장바구니 갱신 (SPA 방식)
                refreshCart();

                //  탭 전환 코드 실행
                const cartTabBtn = document.querySelector('#v-pills-cart-tab');
                if (cartTabBtn) {
                    new bootstrap.Tab(cartTabBtn).show();
                }

                // 👉 위시리스트에서도 제거
                fetch(`/wishlist/${bookId}`, {
                    method: 'DELETE',
                    headers: { 'X-CSRF-TOKEN': window.csrfToken }
                })
                    .then(res => res.json())
                    .then(delData => {
                        if (delData.status === 'ok') {
                            btn.closest('li').remove();
                            if (document.querySelectorAll('#v-pills-wishlist li').length === 0) {
                                document.querySelector('#v-pills-wishlist').innerHTML =
                                    '<div class="alert alert-info">위시리스트에 담긴 책이 없습니다.</div>';
                            }
                        }
                    });
            } else {
                alert(data.message || '이미 장바구니에 있습니다 ❌');
            }
        })
        .catch(err => console.error(err));
}


// ✅ 위시리스트 삭제
function removeWishlist(bookId, btn, confirmFlag = true) {
    if (confirmFlag && !confirm("정말 삭제하시겠습니까?")) return;

    fetch(`/wishlist/${bookId}`, {
        method: 'DELETE',
        headers: { 'X-CSRF-TOKEN': window.csrfToken }
    })
        .then(res => res.json())
        .then(data => {
            if (data.status === 'ok') {
                btn.closest('li').remove();
                if (document.querySelectorAll('#v-pills-wishlist li').length === 0) {
                    document.querySelector('#v-pills-wishlist').innerHTML =
                        '<div class="alert alert-info">위시리스트에 담긴 책이 없습니다.</div>';
                }
            } else {
                alert('삭제 실패 ❌ ' + data.message);
            }
        })
        .catch(err => console.error(err));
}
