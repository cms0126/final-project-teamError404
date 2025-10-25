package com.error404.geulbut.jpa.carts.service;

import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.books.repository.BooksRepository;
import com.error404.geulbut.jpa.carts.controller.PaymentController;
import com.error404.geulbut.jpa.carts.dto.CartDto;
import com.error404.geulbut.jpa.carts.entity.Cart;
import com.error404.geulbut.jpa.carts.repository.CartRepository;
import com.error404.geulbut.jpa.orderitem.entity.OrderItem;
import com.error404.geulbut.jpa.orders.entity.Orders;
import com.error404.geulbut.jpa.orders.repository.OrdersRepository;
import com.error404.geulbut.jpa.users.entity.Users;
import com.error404.geulbut.jpa.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.error404.geulbut.jpa.orders.entity.Orders.STATUS_PAID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final BooksRepository booksRepository;
    private final MapStruct mapStruct;
    private final OrdersRepository ordersRepository;
    private final UsersRepository usersRepository;

    /** 장바구니 목록 조회 */
    @Transactional(readOnly = true)
    public List<CartDto> getCartList(String userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        return mapStruct.toCartDtos(carts);
    }

    /** 장바구니 합계 */
    @Transactional(readOnly = true)
    public long getCartTotal(String userId) {
        List<CartDto> cartList = getCartList(userId);
        return cartList.stream()
                .mapToLong(CartDto::getTotalPrice)
                .sum();
    }

    /** 장바구니 추가 */
    @Transactional
    public void addToCart(String userId, Long bookId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        // 해당 책이 이미 장바구니에 있는지 확인
        Cart cart = cartRepository.findByUserIdAndBook_BookId(userId, bookId)
                .orElse(null);

        if (cart != null) {
            cart.setQuantity(cart.getQuantity() + quantity);
        } else {
            Books book = booksRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책 ID: " + bookId));

            Cart newCart = Cart.builder()
                    .userId(userId)
                    .book(book)
                    .quantity(quantity)
                    .build();

            cartRepository.save(newCart);
        }
    }

    /** 장바구니 수량/금액 합계 수정 */
    @Transactional
    public Cart updateCartItem(String userId, Long bookId, int quantity) {
        if (quantity <= 0) {
            cartRepository.deleteByUserIdAndBook_BookId(userId, bookId);
            return null;
        }
        Cart cart = cartRepository.findByUserIdAndBook_BookId(userId, bookId)
                .orElseThrow(() -> new RuntimeException("장바구니 항목을 찾을 수 없습니다."));
        cart.setQuantity(quantity);
        return cart;
    }

    /** 장바구니 삭제 */
    @Transactional
    public void removeFromCart(String userId, Long bookId) {
        cartRepository.deleteByUserIdAndBook_BookId(userId, bookId);
    }

    /** 📌 결제(주문 생성) */
    @Transactional
    public Orders checkout(String userId, String merchantUid, PaymentController.VerifyReq.OrdersInfo info) {

        List<Cart> items = cartRepository.findAllWithBookByUserId(userId);
        if (items.isEmpty()) throw new IllegalStateException("장바구니가 비어있습니다.");

        Users userRef = usersRepository.getReferenceById(userId);

        Orders order = Orders.builder()
                .userId(userId)
                .merchantUid(merchantUid)
                .status(STATUS_PAID)
                .paidAt(LocalDateTime.now())
                .recipient(info != null ? info.recipient() : userRef.getName())
                .phone(info != null ? info.phone() : userRef.getPhone())
                .address(info != null ? info.address() : userRef.getAddress())
                .memo(info != null ? info.memo() : null)
                .paymentMethod(info != null ? info.payMethod().toUpperCase() : "CARD")
                .build();

        long total = 0L;        // 실제 결제 총액(할인가 반영)

        for (Cart c : items) {
            long unitPrice = (c.getBook().getDiscountedPrice() != null && c.getBook().getDiscountedPrice() > 0)
                    ? c.getBook().getDiscountedPrice()
                    : c.getBook().getPrice();  // 할인가 있으면 적용, 없으면 정가

            int qty = c.getQuantity();
            total += unitPrice * qty;   // 결제 합산

            order.addItem(OrderItem.builder()
                    .order(order)
                    .book(c.getBook())
                    .price(c.getBook().getPrice())              // 정가 스냅샷
                    .discountedPrice(c.getBook().getDiscountedPrice())  // 할인가 스냅샷
                    .quantity(qty)
                    .build());
        }

        // 총액 계산
        order.setTotalPrice(total);

        cartRepository.deleteByUserId(userId); // 장바구니 비우기
        return order;
    }

    @Transactional
    public Orders checkoutFromCart(String userId, String merchantUid, Long expectedAmount) {
        List<Cart> items = cartRepository.findAllWithBookByUserId(userId);
        if (items.isEmpty()) throw new IllegalStateException("장바구니가 비어있습니다.");

        Users userRef = usersRepository.getReferenceById(userId);

        Orders order = Orders.builder()
                .userId(userId)
                .merchantUid(merchantUid)
                .status(STATUS_PAID)
                .paidAt(LocalDateTime.now())
                .recipient(userRef.getName())
                .phone(userRef.getPhone())
                .address(userRef.getAddress())
                .paymentMethod("CARD")
                .build();

        long total = 0L;
        for (Cart c : items) {
            long unitPrice = (c.getBook().getDiscountedPrice() != null && c.getBook().getDiscountedPrice() > 0)
                    ? c.getBook().getDiscountedPrice()
                    : c.getBook().getPrice();
            int qty = c.getQuantity();
            total += unitPrice * qty;

            order.addItem(OrderItem.builder()
                    .order(order)
                    .book(c.getBook())
                    .price(c.getBook().getPrice())
                    .discountedPrice(c.getBook().getDiscountedPrice())
                    .quantity(qty)
                    .build());
        }

        // (선택) 금액 무결성 체크
        if (expectedAmount != null && total != expectedAmount) {
            throw new IllegalStateException("주문 금액 불일치");
        }

        order.setTotalPrice(total);

        // 장바구니 비우기
        cartRepository.deleteByUserId(userId);

        return order;
    }

//      컨트롤러 분기용: 바로구매(장바구니 사용 안 함, 단건 아이템)
    @Transactional
    public Orders checkoutBuyNow(String userId, String merchantUid, long bookId, int quantity, Long expectedAmount) {
        if (quantity <= 0) quantity = 1;

        Books book = booksRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책 ID: " + bookId));

        Users userRef = usersRepository.getReferenceById(userId);

        long unitPrice = (book.getDiscountedPrice() != null && book.getDiscountedPrice() > 0)
                ? book.getDiscountedPrice()
                : book.getPrice();
        long total = unitPrice * quantity;

        // (선택) 금액 무결성 체크
        if (expectedAmount != null && total != expectedAmount) {
            throw new IllegalStateException("주문 금액 불일치");
        }

        Orders order = Orders.builder()
                .userId(userId)
                .merchantUid(merchantUid)
                .status(STATUS_PAID)
                .paidAt(LocalDateTime.now())
                .recipient(userRef.getName())
                .phone(userRef.getPhone())
                .address(userRef.getAddress())
                .paymentMethod("CARD")
                .totalPrice(total)
                .build();

        order.addItem(OrderItem.builder()
                .order(order)
                .book(book)
                .price(book.getPrice())
                .discountedPrice(book.getDiscountedPrice())
                .quantity(quantity)
                .build());

        return order;
    }
}
