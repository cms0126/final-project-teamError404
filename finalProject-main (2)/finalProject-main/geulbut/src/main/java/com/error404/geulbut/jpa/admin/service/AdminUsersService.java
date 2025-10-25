// AdminUsersService.java
package com.error404.geulbut.jpa.admin.service;

import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.carts.repository.CartRepository;
import com.error404.geulbut.jpa.orders.repository.OrdersRepository;
import com.error404.geulbut.jpa.users.dto.UsersDto;
import com.error404.geulbut.jpa.users.entity.Users;
import com.error404.geulbut.jpa.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminUsersService {

    private final UsersRepository usersRepository;
    private final MapStruct mapStruct;
    private final OrdersRepository ordersRepository;
    private final CartRepository cartRepository;


    //   1. 전체 회원 조회 (DTO 반환)
    public List<UsersDto> getAllUsers() {
        return usersRepository.findAll()
                .stream()
                .map(mapStruct::toAdminDto)
                .collect(Collectors.toList());
    }


    //  2. 회원 ID로 조회 (DTO 반환)
    public Optional<UsersDto> getUserById(String userId) {
        return usersRepository.findById(userId)
                .map(mapStruct::toAdminDto);
    }

    //  3. 회원 권한 변경
    public boolean updateUserRole(String userId, String newRole) {
        Optional<Users> optionalUsers = usersRepository.findById(userId);
        if (optionalUsers.isPresent()) {
            Users users = optionalUsers.get();
            users.setRole(newRole);
            usersRepository.save(users);
            return true;
        }
        return false;
    }

    //  4. 회원 삭제
    @Transactional
    public boolean deleteUser(String userId) {
        if (usersRepository.existsById(userId)) {
            // 연관 데이터 먼저 삭제
            // 회원삭제전에 주문내역/장바구니 삭제
            ordersRepository.deleteByUserUserId(userId);
            cartRepository.deleteByUserId(userId);

            // 회원 삭제
            usersRepository.deleteById(userId);
            return true;
        }
        return false;
    }


    //     5. 회원 검색 + 필터 + 페이징 (DTO 반환)
    public Page<Users> searchUsers(String keyword,
                                   String startDate,
                                   String endDate,
                                   String roleFilter,
                                   String statusFilter,
                                   int page,
                                   int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("joinDate").descending());

        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;

        return usersRepository.searchByKeyword(
                (keyword == null || keyword.trim().isEmpty()) ? null : keyword,
                start,
                end,
                (roleFilter == null || roleFilter.isEmpty()) ? null : roleFilter,
                (statusFilter == null || statusFilter.isEmpty()) ? null : statusFilter,
                pageable
        );
    }

    //      총 회원 수
    public Long getTotalUsers() {
        return usersRepository.count();
    }

    //     * 오늘 가입한 회원 수
    public long getTodayNewUsers() {
        LocalDate today = LocalDate.now();
        return usersRepository.countByJoinDateBetween(today, today.plusDays(1));
    }

    // 6. 계정 상태 변경
    public boolean updateUserStatus(String userId, String newStatus) {
        Optional<Users> optionalUsers = usersRepository.findById(userId);
        if (optionalUsers.isPresent()) {
            Users user = optionalUsers.get();
            try {
                Users.UserStatus statusEnum = Users.UserStatus.valueOf(newStatus.toUpperCase());
                user.setStatus(statusEnum);
                usersRepository.save(user);
                return true;
            } catch (IllegalArgumentException e) {
                // 잘못된 값이면 false
            }
        }
        return false;
    }

    //  7. 회원 정보 전체 수정 (role, status, point, grade)
    public boolean updateUserInfo(String userId, String newRole, String newStatus, Long newPoint, String newGrade) {
        Optional<Users> optionalUsers = usersRepository.findById(userId);
        if (optionalUsers.isPresent()) {
            Users user = optionalUsers.get();

            if (newRole != null) user.setRole(newRole);

            if (newStatus != null) {
                try {
                    Users.UserStatus statusEnum = Users.UserStatus.valueOf(newStatus.toUpperCase());
                    user.setStatus(statusEnum);
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }

            if (newPoint != null) user.setPoint(newPoint);

            if (newGrade != null) user.setGrade(newGrade);

            usersRepository.save(user);
            return true;
        }
        return false;
    }
}
