package com.error404.geulbut.jpa.carts.repository;

import com.error404.geulbut.jpa.carts.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * CartRepository
 * -----------------------------
 * - JpaRepository<Cart, Long> : Cart 엔티티 기본 CRUD 기능 제공
 * - CartQueryRepositoryCustom : QueryDSL 기반의 커스텀 조회 기능 제공 (DTO 매핑 등)
 */
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserIdAndBook_BookId(String userId, Long bookId);

    List<Cart> findByUserId(String userId);

    boolean existsByUserIdAndBook_BookId(String userId, Long bookId);

    void deleteByUserIdAndBook_BookId(String userId, Long bookId);

    // ✅ 결제용: Book까지 한 번에 로딩 (N+1 방지)
    @Query("select c from Cart c join fetch c.book where c.userId = :userId")
    List<Cart> findAllWithBookByUserId(@Param("userId") String userId);

    // ✅ 결제 후: 사용자 카트 전부 삭제
    void deleteByUserId(String userId);
}
