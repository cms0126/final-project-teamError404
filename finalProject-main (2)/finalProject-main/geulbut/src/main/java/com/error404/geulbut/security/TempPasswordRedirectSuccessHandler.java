// com.error404.geulbut.security.TempPasswordRedirectSuccessHandler
package com.error404.geulbut.security;

import com.error404.geulbut.jpa.users.entity.Users;
import com.error404.geulbut.jpa.users.repository.UsersRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class TempPasswordRedirectSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UsersRepository usersRepository;
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        String loginName = extractLoginName(authentication);
        Optional<Users> opt = usersRepository.findById(loginName);
        if (opt.isEmpty()) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }



        Users user = opt.get();
        if ("Y".equalsIgnoreCase(user.getTempPwYn())) {
            log.info("[LOGIN] tempPwYn=Y → force redirect to /mypage/password/change");

            // ★ 원래 가려던 주소(SavedRequest) 제거해서 무조건 우리 경로로!
            requestCache.removeRequest(request, response);
            clearAuthenticationAttributes(request);

            String target = request.getContextPath() + "/mypage/password/change";
            response.sendRedirect(target);
            return;
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private String extractLoginName(Authentication authentication) {
        Object p = authentication.getPrincipal();
        if (p instanceof UserDetails ud) return ud.getUsername();
        return authentication.getName();
    }
}
