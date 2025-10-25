package com.error404.geulbut.jpa.users.service;

import com.error404.geulbut.jpa.users.entity.Users;
import com.error404.geulbut.jpa.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UsersDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        if(user.getStatus() != Users.UserStatus.ACTIVE) {
            throw new DisabledException("탈퇴/비활성 계정입니다.");
        }

        // temp가 있으면 temp, 없으면 password
        String hashed = (user.getPasswordTemp() != null && !user.getPasswordTemp().isBlank())
                ? user.getPasswordTemp()
                : user.getPassword();
        log.debug("로그인 [{}] → 선택된 해시: {}, tempYn={}", username,
                (hashed != null ? hashed.substring(0, 10) + "..." : "null"),
                user.getTempPwYn());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserId())
                .password(hashed)
                .roles(user.getRole())
                .build();
    }
}
