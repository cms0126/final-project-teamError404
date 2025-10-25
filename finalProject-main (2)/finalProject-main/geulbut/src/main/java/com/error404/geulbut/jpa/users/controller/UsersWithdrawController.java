package com.error404.geulbut.jpa.users.controller;

import com.error404.geulbut.jpa.users.service.UsersWithdrawService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UsersWithdrawController {

    private final UsersWithdrawService usersWithdrawService;

    // ✅ GET: 탈퇴 확인 페이지 렌더 (네 버튼이 향하는 URL과 일치)
    @GetMapping("/users/mypage/withdraw")
    public String showWithdrawPage() {
        // 뷰 경로: /WEB-INF/views/users/mypage/withdraw.jsp  (아래 2번 참고)
        return "users/mypage/withdraw";
    }

    // ✅ POST: 실제 탈퇴 처리
    @PostMapping("/users/withdraw")
    public String withdraw(HttpServletRequest request,
                           HttpServletResponse response,
                           Authentication auth) {
        String userId = auth.getName();
        usersWithdrawService.withdraw(userId);

        new SecurityContextLogoutHandler().logout(request, response, auth);
        return "redirect:/login?withdrawn";
    }
}
