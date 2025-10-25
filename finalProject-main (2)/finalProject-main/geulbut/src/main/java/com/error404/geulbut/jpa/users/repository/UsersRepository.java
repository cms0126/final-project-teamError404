package com.error404.geulbut.jpa.users.repository;

import com.error404.geulbut.jpa.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
    // 이메일 중복 체크용 메서드
    Optional<Users> findByEmail(String email);

    // 아이디 , 이메일 중복 체크용 메서드
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<Users> findByUserIdAndEmail(String userId, String email);
    Optional<Users> findByNameAndEmail(String name, String email);
    Optional<Users> findByPhone(String phone);
    Optional<Users> findByEmailIgnoreCase(String email);        // 이메일 대소문자 무시
    Optional<Users> findByUserIdAndStatus(String userId, Users.UserStatus status);

    List<Users> findAllByStatus(Users.UserStatus status);

    // 아디 또는 이름+이메일 조합으로 찾을때 쿼리문 추가
    @Query("select u from Users u" +
            "    where (upper(u.email)=upper(:email)) " +
            "    and (u.userId=:userId or :userId is null)")
    Optional<Users> findByEmailAndMaybeUserId(@Param("email") String email,
                                                                        @Param("userId") String userId);


    // 회원 검색 (아이디, 이름, 이메일) - 관리자페이지에서 회원 검색용
    @Query("SELECT u FROM Users u " +
            "WHERE (:keyword IS NULL OR u.userId LIKE %:keyword% OR u.name LIKE %:keyword% OR u.email LIKE %:keyword%) " +
            "AND (:startDate IS NULL OR u.joinDate >= :startDate) " +
            "AND (:endDate IS NULL OR u.joinDate <= :endDate) " +
            "AND (:roleFilter IS NULL OR u.role = :roleFilter) " +
            "AND (:statusFilter IS NULL OR u.status = :statusFilter)")
    Page<Users> searchByKeyword(@Param("keyword") String keyword,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate,
                                @Param("roleFilter") String roleFilter,
                                @Param("statusFilter") String statusFilter,
                                Pageable pageable);

    //    금일 가입자수 카운트
    long countByJoinDateBetween(LocalDate start, LocalDate end);


}
