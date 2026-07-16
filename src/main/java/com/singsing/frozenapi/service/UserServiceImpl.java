package com.singsing.frozenapi.service;

import com.singsing.frozenapi.domain.Role;
import com.singsing.frozenapi.domain.User;
import com.singsing.frozenapi.domain.UserStatus;
import com.singsing.frozenapi.dto.LoginRequestDTO;
import com.singsing.frozenapi.dto.LoginResponseDTO;
import com.singsing.frozenapi.dto.RefreshRequestDTO;
import com.singsing.frozenapi.dto.SignupRequestDTO;
import com.singsing.frozenapi.dto.SignupResponseDTO;
import com.singsing.frozenapi.dto.TokenResponseDTO;
import com.singsing.frozenapi.repository.BranchRepository;
import com.singsing.frozenapi.repository.UserRepository;
import com.singsing.frozenapi.util.JWTUtil;
import com.singsing.frozenapi.util.LoginFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Map;

// UserService 인터페이스의 실제 구현체. 회원가입/로그인/토큰재발급의 핵심 로직이 들어있는 곳
@Service            // 이 클래스를 Spring이 관리하는 Bean으로 등록 (컨트롤러 등에서 자동 주입 가능해짐)
@Transactional       // 이 클래스의 메서드 실행 중 예외가 발생하면 DB 변경사항을 자동 롤백해주는 트랜잭션 처리
@Slf4j                // log.info(), log.error() 등을 바로 쓸 수 있게 해주는 로깅 어노테이션
@RequiredArgsConstructor // final 필드(아래)를 파라미터로 받는 생성자를 자동 생성 -> Spring이 이 생성자로 의존성 주입(DI)
public class UserServiceImpl implements UserService {

    // 액세스 토큰(짧게)/리프레시 토큰(길게) 유효시간을 분 단위 상수로 관리
    private static final int ACCESS_TOKEN_EXPIRE_MINUTES = 30;       // 30분
    private static final int REFRESH_TOKEN_EXPIRE_MINUTES = 60 * 24; // 1일

    // final + @RequiredArgsConstructor 조합 = "생성자 주입"
    // Spring 컨테이너가 UserRepository, PasswordEncoder, JWTUtil(모두 Bean으로 등록되어 있음)을 찾아 자동으로 넣어준다.
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // CustomSecurityConfig에서 Bean으로 등록한 BCryptPasswordEncoder가 주입됨
    private final JWTUtil jwtUtil;                  // 로그인/재발급 시 JWT 토큰을 생성/검증하기 위해 주입받음
    private final BranchRepository branchRepository; // 회원가입 시 branchId 존재 검증 + 응답용 지점명 조회에 사용

    @Override
    public SignupResponseDTO signup(SignupRequestDTO signupRequestDTO) {
        log.info("*********** UserService - signup - email: {}", signupRequestDTO.getEmail());

        // 1) 이메일 중복 체크
        //    이미 가입된 이메일이면 예외를 던짐 -> CustomControllerAdvice가 잡아서 409 Conflict 응답으로 변환
        if (userRepository.existsByEmail(signupRequestDTO.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 2) 지점 존재 여부 검증 (2026-07-16 회의록: 회원가입 시 소속 지점 선택 필수)
        //    branch_id는 User.java에 @ManyToOne 없이 단순 값으로만 저장하므로, 존재하지 않는 지점을 가리키지 않도록 여기서 직접 확인
        if (!branchRepository.existsById(signupRequestDTO.getBranchId())) {
            throw new NoSuchElementException("존재하지 않는 지점입니다.");
        }

        // 3) 요청받은 정보로 User 엔티티 생성
        //    - 비밀번호는 절대 평문(원본) 그대로 저장하지 않고, passwordEncoder.encode()로 암호화(BCrypt 해시)한 값을 저장
        //    - role은 클라이언트가 정하는 게 아니라 서버가 강제로 STAFF를 부여 (보안상 중요: 누구나 회원가입만으로 ADMIN/MANAGER가 되면 안 됨)
        //    - status는 PENDING(승인 대기)으로 시작 -> 관리자가 별도로 승인해야 ACTIVE로 전환되는 구조 (관리자 승인 기능은 추후 구현)
        User user = User.builder()
                .email(signupRequestDTO.getEmail())
                .username(signupRequestDTO.getUsername())
                .passwordHash(passwordEncoder.encode(signupRequestDTO.getPassword()))
                .branchId(signupRequestDTO.getBranchId())
                .role(Role.STAFF)
                .status(UserStatus.PENDING)
                .build();

        // 4) DB에 저장
        //    save()가 리턴하는 saved 엔티티에는 DB가 채번한 userId, @CreatedDate로 채워진 createdAt이 반영되어 있음
        User saved = userRepository.save(user);

        // 5) 엔티티를 그대로 리턴하지 않고, 응답용 DTO로 변환해서 리턴 (passwordHash는 응답에서 제외됨)
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
                .branchId(user.getBranchId())
                .branchName(findBranchName(user.getBranchId())) // 프론트가 branchId만 보고 이름을 다시 조회하지 않도록 같이 내려줌
                .createdAt(user.getCreatedAt())
                .build();
    }

    // branchId로 지점명만 조회하는 헬퍼. 존재 검증은 signup()/login()에서 이미 끝났으므로 못 찾으면 null 처리만 함
    private String findBranchName(Integer branchId) {
        return branchRepository.findById(branchId)
                .map(branch -> branch.getName())
                .orElse(null);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        log.info("*********** UserService - login - email: {}", loginRequestDTO.getEmail());

        // 1) 이메일로 회원 조회. 없으면 로그인 실패
        //    "이메일이 존재하지 않습니다"처럼 구체적으로 알려주지 않고 동일한 메시지를 쓰는 이유:
        //    이메일 존재 여부까지 노출하면 공격자가 가입된 이메일 목록을 추측(계정 열거 공격)할 수 있기 때문
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new LoginFailException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 2) 비밀번호 검증: 요청받은 원본 비밀번호를 암호화해서 비교하는 게 아니라,
        //    passwordEncoder.matches(원본, 저장된해시)가 내부적으로 같은 방식으로 해시해서 비교해준다.
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPasswordHash())) {
            throw new LoginFailException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3) 승인 대기(PENDING) 상태면 로그인 자체를 막는다 (관리자 승인 API는 아직 없음 - 추후 구현 예정)
        if (user.getStatus() == UserStatus.PENDING) {
            throw new LoginFailException("관리자 승인 대기 중인 계정입니다.");
        }

        // 4) 토큰에 담을 정보(claims) 구성 후 access/refresh 토큰 발급
        Map<String, Object> claims = buildClaims(user);
        String accessToken = jwtUtil.generateToken(claims, ACCESS_TOKEN_EXPIRE_MINUTES);
        String refreshToken = jwtUtil.generateToken(claims, REFRESH_TOKEN_EXPIRE_MINUTES);

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .branchId(user.getBranchId())
                .branchName(findBranchName(user.getBranchId()))
                .build();
    }

    @Override
    public TokenResponseDTO refresh(RefreshRequestDTO refreshRequestDTO) {
        log.info("*********** UserService - refresh");

        // refreshToken 자체를 검증 -> 유효하지 않거나(위조/만료) 하면 JWTUtil이 CustomJWTException을 던짐
        // (CustomControllerAdvice가 잡아서 401로 응답)
        Map<String, Object> claims = jwtUtil.validateToken(refreshRequestDTO.getRefreshToken());

        // 검증에 성공한 refreshToken 안의 claims를 그대로 재사용해서 새 토큰 쌍을 발급
        // (재로그인 없이, 즉 비밀번호 재확인 없이 토큰만 새로 받는 것 - refreshToken 자체가 "로그인된 상태"를 증명하는 역할)
        String newAccessToken = jwtUtil.generateToken(claims, ACCESS_TOKEN_EXPIRE_MINUTES);
        String newRefreshToken = jwtUtil.generateToken(claims, REFRESH_TOKEN_EXPIRE_MINUTES);

        return TokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    // JWT에 담을 사용자 정보(claims) 구성
    private Map<String, Object> buildClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole().name());
        // 비밀번호(해시)는 절대 claims에 넣지 않는다.
        // JWT는 암호화가 아니라 서명만 되는 방식이라, 토큰을 가진 사람은 누구나 Base64 디코딩만으로 내용을 볼 수 있기 때문
        return claims;
    }

}
