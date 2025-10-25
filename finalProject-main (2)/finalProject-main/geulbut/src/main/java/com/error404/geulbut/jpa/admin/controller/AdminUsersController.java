package com.error404.geulbut.jpa.admin.controller;

import com.error404.geulbut.jpa.admin.service.AdminUsersService;
import com.error404.geulbut.jpa.users.dto.UsersDto;
import com.error404.geulbut.jpa.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsersController {

    private final AdminUsersService adminUsersService;

    /** 1. JSP 페이지 렌더링 */
    @GetMapping("/users-info")
    public String usersInfoPage(
            Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String roleFilter,
            @RequestParam(required = false) String statusFilter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Users> usersPage = adminUsersService.searchUsers(keyword, startDate, endDate, roleFilter, statusFilter, page, size);
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("roleFilter", roleFilter);
        model.addAttribute("statusFilter", statusFilter);
        return "admin/admin_users_Info";
    }

    /** 2. 전체 회원 조회 */
    @GetMapping("/api/users")
    @ResponseBody
    public List<UsersDto> getAllUsersApi() {
        return adminUsersService.getAllUsers();
    }

    /** 3. 특정 회원 조회 */
    @GetMapping("/api/users/{userId}")
    @ResponseBody
    public ResponseEntity<UsersDto> getUserByIdApi(@PathVariable String userId) {
        return adminUsersService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** 4. 회원 권한 변경 */
    @PutMapping("/api/users/{userId}/role")
    @ResponseBody
    public ResponseEntity<String> updateUserRoleApi(
            @PathVariable String userId,
            @RequestParam String newRole
    ) {
        boolean result = adminUsersService.updateUserRole(userId, newRole);
        if (result) return ResponseEntity.ok("권한 변경 완료");
        else return ResponseEntity.badRequest().body("회원이 존재하지 않음");
    }

    /** 5. 회원 삭제 */
    @DeleteMapping("/api/users/{userId}")
    @ResponseBody
    public ResponseEntity<String> deleteUserApi(@PathVariable String userId) {
        boolean result = adminUsersService.deleteUser(userId);
        if (result) return ResponseEntity.ok("회원 삭제 완료");
        else return ResponseEntity.badRequest().body("회원이 존재하지 않음");
    }

    /** 6. 회원 통계 */
    @GetMapping("/api/users/stats")
    @ResponseBody
    public Map<String, Long> getUserStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", adminUsersService.getTotalUsers());
        stats.put("todayNewUsers", adminUsersService.getTodayNewUsers());
        return stats;
    }

    /** 7. 계정 상태 변경 */
    @PutMapping("/api/users/{userId}/status")
    @ResponseBody
    public ResponseEntity<String> updateUserStatusApi(
            @PathVariable String userId,
            @RequestParam String newStatus
    ) {
        boolean result = adminUsersService.updateUserStatus(userId, newStatus);
        if (result) return ResponseEntity.ok("계정 상태 변경 완료");
        else return ResponseEntity.badRequest().body("회원이 존재하지 않음");
    }

    /** DTO 요청 */
    public static class UpdateUserInfoRequest {
        public String newRole;
        public String newStatus;
        public Long newPoint;
        public String newGrade;
    }

    /** 8. 회원 정보 통합 수정 */
    @PutMapping("/api/users/{userId}/info")
    @ResponseBody
    public ResponseEntity<String> updateUserInfoApi(
            @PathVariable String userId,
            @RequestBody UpdateUserInfoRequest request
    ) {
        boolean result = adminUsersService.updateUserInfo(
                userId,
                request.newRole,
                request.newStatus,
                request.newPoint,
                request.newGrade
        );
        if (result) return ResponseEntity.ok("회원 정보 수정 완료");
        else return ResponseEntity.badRequest().body("회원 정보 수정 실패");
    }
}
