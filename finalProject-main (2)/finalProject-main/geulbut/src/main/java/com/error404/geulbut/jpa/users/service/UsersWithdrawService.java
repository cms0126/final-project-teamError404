package com.error404.geulbut.jpa.users.service;

import com.error404.geulbut.jpa.users.entity.Users;
import com.error404.geulbut.jpa.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class UsersWithdrawService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void withdraw(String userId) {
        Users u = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (u.getStatus() != Users.UserStatus.ACTIVE) {
            log.info("이미 탈퇴/비활성 계정: userId={}, status={}", userId, u.getStatus());
            return;
        }
//      시연용 간단 익명화 [덮어쓰기 -> 재가입 충돌방지]
        u.setName("탈퇴회원");
        u.setEmail("withdrawn_" + u.getUserId() + "@invalid.local");

//        재로그인 무력화
        u.setPassword(passwordEncoder.encode(Long.toHexString(System.nanoTime())));
        u.setPasswordTemp(null);
        u.setTempPwYn("N");

//        상태전환 + 삭제시각 기록(엔티티는 놔두고 여기서 세팅)
        u.setStatus(Users.UserStatus.DELETED);  // 탈퇴라는 의미
        u.setDeletedAt(LocalDateTime.now());

        usersRepository.save(u);
        log.info("회원탈퇴 완료 userId={}", userId);
    }
}
