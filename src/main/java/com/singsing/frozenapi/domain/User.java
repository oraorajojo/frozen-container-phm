package com.singsing.frozenapi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// ERD의 users 테이블과 매핑되는 JPA 엔티티 클래스
// 이 클래스의 인스턴스 하나가 DB의 users 테이블 row 하나에 대응된다.
@Entity                    // 이 클래스가 JPA 엔티티임을 선언 (테이블과 매핑됨)
@Table(name = "users")     // 매핑될 실제 테이블 이름 지정 (클래스명은 User지만 테이블명은 users)
@Getter                    // 모든 필드에 대해 getter 메서드를 자동 생성 (예: getUserId(), getEmail())
@ToString                  // 로그 출력 등에 사용할 toString() 자동 생성
@Builder                   // User.builder().email("a@a.com")... 형태로 객체 생성 가능하게 해줌
@AllArgsConstructor        // 모든 필드를 파라미터로 받는 생성자 자동 생성 (Builder가 내부적으로 사용)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 파라미터 없는 기본 생성자 (JPA는 필수로 요구함). 외부에서 함부로 못 쓰게 protected로 제한
@EntityListeners(AuditingEntityListener.class) // 아래 @CreatedDate 필드를 자동으로 채워주는 리스너 등록
public class User {

    @Id // 이 필드가 테이블의 기본키(PK)임을 표시
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 값을 DB(AUTO_INCREMENT)가 자동으로 채번하도록 위임
    @Column(name = "user_id") // 실제 DB 컬럼명 지정 (Java 필드명 userId -> DB 컬럼명 user_id)
    private Long userId;

    @Column(nullable = false, unique = true) // NOT NULL + 중복 불가(유니크) 제약조건. 이메일은 로그인 아이디 역할이라 중복되면 안 됨
    private String email;

    @Column(name = "password_hash", nullable = false) // 원본 비밀번호가 아니라 암호화(BCrypt)된 값이 저장됨
    private String passwordHash;

    @Enumerated(EnumType.STRING) // enum을 DB에 저장할 때 순서(0,1,2..) 대신 이름 문자열("USER" 등)로 저장 (순서가 바뀌어도 안전)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @CreatedDate // 엔티티가 처음 저장(insert)될 때 현재 시간이 자동으로 채워짐 (수정 시에는 값이 바뀌지 않음)
    @Column(name = "created_at", updatable = false, nullable = false) // updatable = false : 이후 update 쿼리에서는 이 컬럼을 건드리지 않음
    private LocalDateTime createdAt;

    // ===== 수정용 메서드 =====
    // JPA 엔티티는 Setter를 열어두지 않고, "의미가 드러나는 메서드"로만 값을 바꾸는 것이 관례
    // (예: setPasswordHash() 대신 changePasswordHash() 처럼 어떤 의도로 바뀌는지 이름에 드러냄)

    // 비밀번호 변경 시 사용 (지금은 회원가입 기능만 있어서 호출되는 곳은 없지만, 추후 비밀번호 변경 기능에서 사용 예정)
    public void changePasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // 계정 상태 변경 시 사용 (추후 탈퇴/정지 기능에서 사용 예정)
    public void changeStatus(UserStatus status) {
        this.status = status;
    }

}
