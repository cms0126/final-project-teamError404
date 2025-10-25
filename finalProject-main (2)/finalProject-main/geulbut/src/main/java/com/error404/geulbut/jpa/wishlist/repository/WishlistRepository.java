package com.error404.geulbut.jpa.wishlist.repository;

import com.error404.geulbut.jpa.wishlist.dto.WishlistDto;
import com.error404.geulbut.jpa.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, String> {

    // ✅ 유저 위시리스트 + 책 정보 조회
    @Query("SELECT new com.error404.geulbut.jpa.wishlist.dto.WishlistDto(" +
            "w.wishlistId, w.userId, b.bookId, b.title, a.name, p.name, b.imgUrl, " +
            "b.price, b.discountedPrice,b.wishCount, w.createdAt) " +
            "FROM Wishlist w " +
            "JOIN w.book b " +
            "LEFT JOIN b.author a " +
            "LEFT JOIN b.publisher p " +
            "WHERE w.userId = :userId")
    List<WishlistDto> findWishlistWithBookInfo(@Param("userId") String userId);


    // ✅ 중복 방지 체크 (이미 찜했는지 여부)
    boolean existsByUserIdAndBook_BookId(String userId, Long bookId);

    // ✅ 특정 책 삭제
    int deleteByUserIdAndBook_BookId(String userId, Long bookId);


    // ✅ 시퀀스 값 가져오기
    @Query(value = "SELECT SEQ_WISHLISTS.NEXTVAL FROM DUAL", nativeQuery = true)
    Long getNextSeq();
}
