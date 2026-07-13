package com.singsing.frozenapi.service;

import com.singsing.frozenapi.domain.Role;
import com.singsing.frozenapi.domain.User;
import com.singsing.frozenapi.domain.UserStatus;
import com.singsing.frozenapi.dto.SignupRequestDTO;
import com.singsing.frozenapi.dto.SignupResponseDTO;
import com.singsing.frozenapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// UserService 인터페이스의 실제 구현체. 회원가입의 핵심 로직이 들어있는 곳
@Service            // 이 클래스를 Spring이 관리하는 Bean으로 등록 (컨트롤러 등에서 자동 주입 가능해짐)
@Transactional       // 이 클래스의 메서드 실행 중 예외가 발생하면 DB 변경사항을 자동 롤백해주는 트랜잭션 처리
@Slf4j                // log.info(), log.error() 등을 바로 쓸 수 있게 해주는 로깅 어노테이션
@RequiredArgsConstructor // final 필드(아래 두 개)를 파라미터로 받는 생성자를 자동 생성 -> Spring이 이 생성자로 의존성 주입(DI)
public class UserServiceImpl implements UserService {

    // final + @RequiredArgsConstructor 조합 = "생성자 주입"
    // Spring 컨테이너가 UserRepository, PasswordEncoder(둘 다 Bean으로 등록되어 있음)를 찾아 자동으로 넣어준다.
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // CustomSecurityConfig에서 Bean으로 등록한 BCryptPasswordEncoder가 주입됨

    @Override
    public SignupResponseDTO signup(SignupRequestDTO signupRequestDTO) {
        log.info("*********** UserService - signup - email: {}", signupRequestDTO.getEmail());

        // 1) 이메일 중복 체크
        //    이미 가입된 이메일이면 예외를 던짐 -> CustomControllerAdvice가 잡아서 409 Conflict 응답으로 변환
        if (userRepository.existsByEmail(signupRequestDTO.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 2) 요청받은 정보로 User 엔티티 생성
        //    - 비밀번호는 절대 평문(원본) 그대로 저장하지 않고, passwordEncoder.encode()로 암호화(BCrypt 해시)한 값을 저장
        //    - role은 클라이언트가 정하는 게 아니라 서버가 강제로 STAFF를 부여 (보안상 중요: 누구나 회원가입만으로 ADMIN이 되면 안 됨)
        //    - status는 PENDING(승인 대기)으로 시작 -> 관리자가 별도로 승인해야 ACTIVE로 전환되는 구조 (관리자 승인 기능은 추후 구현)
        User user = User.builder()
                .email(signupRequestDTO.getEmail())
                .username(signupRequestDTO.getUsername())
                .passwordHash(passwordEncoder.encode(signupRequestDTO.getPassword()))
                .role(Role.STAFF)
                .status(UserStatus.PENDING)
                .build();

        // 3) DB에 저장
        //    save()가 리턴하는 saved 엔티티에는 DB가 채번한 userId, @CreatedDate로 채워진 createdAt이 반영되어 있음
        User saved = userRepository.save(user);

        // 4) 엔티티를 그대로 리턴하지 않고, 응답용 DTO로 변환해서 리턴 (passwordHash는 응답에서 제외됨)
        return entityToDTO(saved);
    }

    // User 엔티티 -> SignupResponseDTO 변환 전용 private 메서드
    private SignupResponseDTO entityToDTO(User user) {
        return SignupResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().name())     // enum -> 문자열 ("STAFF")
                .status(user.getStatus().name()) // enum -> 문자열 ("PENDING")
                .createdAt(user.getCreatedAt())
                .build();
    }

}
