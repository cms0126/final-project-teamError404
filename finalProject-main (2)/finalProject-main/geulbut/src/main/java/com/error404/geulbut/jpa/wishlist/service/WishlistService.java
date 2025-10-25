package com.error404.geulbut.jpa.wishlist.service;

import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.books.repository.BooksRepository;
import com.error404.geulbut.jpa.carts.service.CartService;
import com.error404.geulbut.jpa.wishlist.dto.WishlistDto;
import com.error404.geulbut.jpa.wishlist.entity.Wishlist;
import com.error404.geulbut.jpa.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final BooksRepository booksRepository;
    private final CartService cartService;

    /** 📌 위시리스트 조회 (BOOK, AUTHOR, PUBLISHER JOIN 포함) */
    public List<WishlistDto> getWishlist(String userId) {
        return wishlistRepository.findWishlistWithBookInfo(userId);
    }

    /** 📌 위시리스트 추가 */
    public void addWishlist(String userId, Long bookId) {
        boolean exists = wishlistRepository.existsByUserIdAndBook_BookId(userId, bookId);
        if (exists) return; // 이미 있으면 아무 것도 안 함

        Long seq = wishlistRepository.getNextSeq();
        String newId = "W" + String.format("%03d", seq);

        Books book = booksRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책 ID: " + bookId));

        wishlistRepository.save(Wishlist.builder()
                .wishlistId(newId)
                .userId(userId)
                .book(book)
                .build());

        // ✅ 실제로 새로 추가된 경우에만 +1
        booksRepository.incrementWishCount(bookId);
    }

    /** 📌 위시리스트 → 장바구니 이동 */
    @Transactional
    public void moveToCart(String userId, Long bookId, int quantity) {
        cartService.addToCart(userId, bookId, quantity);

        long deleted = wishlistRepository.deleteByUserIdAndBook_BookId(userId, bookId);

        // ✅ 정말 삭제되었을 때만 -1
        if (deleted > 0) {
            booksRepository.decrementWishCount(bookId);
        }
    }

    /** 📌 위시리스트 삭제 */
    public void removeWishlist(String userId, Long bookId) {
        int deleted = wishlistRepository.deleteByUserIdAndBook_BookId(userId, bookId);

        // ✅ 정말 삭제되었을 때만 -1
        if (deleted > 0) {
            booksRepository.decrementWishCount(bookId);
        }
    }
}
