package com.error404.geulbut.jpa.users.entity;

import com.error404.geulbut.common.Char1Converter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "USERS")
@SequenceGenerator(
        name = "SEQ_USERS_JPA",
        sequenceName = "SEQ_USERS",
        allocationSize = 1
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"password", "passwordTemp", "profileImg"})
@DynamicInsert
@DynamicUpdate
@EqualsAndHashCode(of = "userId", callSuper = false)

public class Users {

    public enum AuthProvider { LOCAL, GOOGLE, NAVER, KAKAO}

    @Id
    @Column(name = "USER_ID", length = 50, nullable = false)        // 컬럼 어노테이션 쓸 이유가없다.
    private String userId; // @GeneratedValue 제거
//    로그인/회원가입 공통
    private String password;
    private String passwordTemp;                        // 임시비밀번호로 넣어둠
    private String name;
    private String email;                                       // 카카오는 null 가능
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROVIDER", length = 16, nullable = false)
    private AuthProvider provider;                         // 가입경로

//    임시비밀번호 관련
    private String tempPwYn;                                  // 임시비번 발급 여부
    private String pwCode;                                  // 인증코드 6자리
    private LocalDateTime pwCodeExpiresAt;       // 만료시각 예) +3분
    private Integer pwCodeAttempts;                     // 시도횟수 (예 : 0~5회?)
    private LocalDateTime pwCodeLastSentAt;     // 최근전송시각(재전송쿨타임)

//    기본정보
    private LocalDate joinDate;
    private String role;
    @Convert(converter = Char1Converter.class)
    @Column(name = "GENDER", columnDefinition = "CHAR(1)")
    private Character  gender;
    //    회원가입할때 대소문자 상관없이 생일란 찾음
    private LocalDate birthdate;
    private String address;

//    포인트 & 등급
    private Long point;
    @Convert(converter = Char1Converter.class)
    @Column(name = "POST_NOTIFY_AGREE", columnDefinition = "CHAR(1)")
    private Character postNotifyAgree;
    @Convert(converter = Char1Converter.class)
    @Column(name = "PROMO_AGREE", columnDefinition = "CHAR(1)")
    private Character  promoAgree;
    private String grade;                   // 브론즈/실버/골드
    private Long totalPurchase;

//    프로필
    @Lob
    private byte[] profileImg;
    private String imgUrl;

//    생성/수정 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

//    유저 상태(enum)
    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, DELETED
    }

//    업데이트_AT 자동갱신
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

//    동의 관련
@PrePersist
public void onCreate() {
    // 대소문자 보정
         if (promoAgree == null || (promoAgree != 'Y' && promoAgree != 'N')) promoAgree = 'N';
         if (postNotifyAgree == null || (postNotifyAgree != 'Y' && postNotifyAgree != 'N')) postNotifyAgree = 'N';
         if (gender == null || (gender != 'M' && gender != 'F')) gender = 'U';

//        기본 등급/포인트/역할
        if(grade == null) grade = "BRONZE";
        if(role == null) role = "USER";
        if(point == null) point = 0L;
        if(totalPurchase == null) totalPurchase = 0L;

//        날짜/시간
        if (joinDate == null) joinDate = LocalDate.now();
        if (createdAt == null) createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

//        유저상태 기본값
        if (status == null) status = UserStatus.ACTIVE;

        //    폼 가입 사용자의 기본값
        if (provider == null) provider = AuthProvider.LOCAL;
    }


//  유저상태 확인 컬럼추가 9/16 강대성
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 16)
    private UserStatus status; // ACTIVE, INACTIVE

}
