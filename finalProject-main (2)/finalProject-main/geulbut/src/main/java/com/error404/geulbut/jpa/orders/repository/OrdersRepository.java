package com.error404.geulbut.jpa.orders.repository;

import com.error404.geulbut.jpa.orders.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUserId(String userId);

    // 멱등 체크용 — merchantUid로 조회
    Optional<Orders> findByMerchantUid(String merchantUid);

    @Query("SELECT o FROM Orders o " +
            "JOIN FETCH o.items i " +
            "JOIN FETCH i.book b " +
            "WHERE o.orderId = :orderId")
    Optional<Orders> findWithItemsAndBooksByOrderId(@Param("orderId") Long orderId);


    //    order orderItem book 전부 한번에 가져옴
    @Query("SELECT DISTINCT o FROM Orders o " +
            "JOIN FETCH o.items i " +
            "JOIN FETCH i.book b " +
            "WHERE o.userId = :userId " +
            "AND o.status <> 'PENDING' " +
            "ORDER BY o.createdAt DESC")
    List<Orders> findWithItemsAndBooksByUserId(@Param("userId") String userId);

    //  이 합계 쿼리문은 배치/점검용 (덕규)
    @Query("""
  select coalesce(sum(o.totalPrice), 0)
  from Orders o
  where o.userId = :userId and o.status = :status
""")
    Long sumTotalByUserAndStatus(@Param("userId") String userId,
                                 @Param("status") String status);

    // 관리자 조회용 (부분검색 + 상태 필터 + 페이징 유지)
    @Query("SELECT DISTINCT o FROM Orders o " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.book b " +
            "WHERE (:userId IS NULL OR o.userId LIKE %:userId%) " +
            "AND (:status IS NULL OR o.status = :status) " +
            "ORDER BY o.createdAt DESC")
    List<Orders> findAllWithItemsAndBooks(
            @Param("userId") String userId,
            @Param("status") String status
    );



    @Query("""
  select distinct o
  from Orders o
    join fetch o.items i
    join fetch i.book b
  where o.userId = :userId
    and o.deliveredAt is not null
  order by o.deliveredAt desc
""")
    List<Orders> findDeliveredWithItemsAndBooksByUserId(
            @Param("userId") String userId
    );

    // 페이징/개수 제한이 필요할 때 쓸 버전 (선택)
    @Query(value = """
  select distinct o
  from Orders o
    join fetch o.items i
    join fetch i.book b
  where o.userId = :userId
    and o.deliveredAt is not null
  order by o.deliveredAt desc
""",
            countQuery = """
  select count(o)
  from Orders o
  where o.userId = :userId
    and o.deliveredAt is not null
""")
    Page<Orders> findDeliveredWithItemsAndBooksByUserId(
            @Param("userId") String userId,
            Pageable pageable
    );

    // ====== 여기서부터 포인트용 추가 ======

    // (1) 미적립 PAID 주문
    @Query("""
    select o
    from Orders o
    where upper(o.status) = 'PAID'
      and o.pointsAccrued is null
    order by o.createdAt asc
    """)
    List<Orders> findPaidNotAccruedTop(Pageable pageable);

    @Query("""
    select count(o)
    from Orders o
    where upper(o.status) = 'PAID'
      and o.pointsAccrued is null
    """)
    long countPaidNotAccrued();

    // (2) 회수 필요 주문
    @Query("""
    select o
    from Orders o
    where upper(o.status) = 'CANCELLED'
      and o.pointsAccrued is not null
      and o.pointsRevokedAt is null
    order by o.createdAt asc
    """)
    List<Orders> findCancelledNeedRevokeTop(Pageable pageable);

    @Query("""
    select count(o)
    from Orders o
    where upper(o.status) = 'CANCELLED'
      and o.pointsAccrued is not null
      and o.pointsRevokedAt is null
    """)
    long countCancelledNeedRevoke();

    // (3) 사용자별 집계(선택)
    @Query("""
    select coalesce(sum(o.pointsAccrued), 0)
    from Orders o
    where o.userId = :userId
      and o.pointsAccrued is not null
      and (o.pointsRevokedAt is null)
    """)
    Long sumAccruedPointsAliveByUser(@Param("userId") String userId);

    @Query("""
    select count(o)
    from Orders o
    where o.userId = :userId
      and o.pointsAccrued is not null
    """)
    Long countAccruedOrdersByUser(@Param("userId") String userId);

    @Query("""
    select count(o)
    from Orders o
    where o.userId = :userId
      and o.pointsRevokedAt is not null
    """)
    Long countRevokedOrdersByUser(@Param("userId") String userId);

    // (4) 결제/취소 처리 시 단건 로딩
    @Query("""
    select o
    from Orders o
      left join fetch o.user u
    where o.orderId = :orderId
    """)
    Optional<Orders> findWithUserByOrderId(@Param("orderId") Long orderId);

    // (선택) 포인트 플래그 필터 포함 관리자 조회
    @Query("""
    select distinct o
    from Orders o
      left join fetch o.user u
      left join fetch o.items i
      left join fetch i.book b
    where (:userId is null or o.userId like %:userId%)
      and (:status is null or o.status = :status)
      and (:accStatus is null
           or (:accStatus = 'NONE' and o.pointsAccrued is null)
           or (:accStatus = 'DONE' and o.pointsAccrued is not null))
      and (:rvkStatus is null
           or (:rvkStatus = 'NONE' and o.pointsRevokedAt is null)
           or (:rvkStatus = 'DONE' and o.pointsRevokedAt is not null))
    order by o.createdAt desc
    """)
    List<Orders> findAllWithItemsBooksAndPointFlags(
            @Param("userId") String userId,
            @Param("status") String status,
            @Param("accStatus") String accStatus,
            @Param("rvkStatus") String rvkStatus
    );
    
//    삭제
    void deleteByUserUserId(String userId); // userId 기준으로 주문 삭제
}
