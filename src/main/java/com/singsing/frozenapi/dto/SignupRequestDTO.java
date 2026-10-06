package com.singsing.frozenapi.dto;

import com.singsing.frozenapi.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원가입 요청(request)으로 클라이언트가 보내는 JSON 데이터를 담는 DTO
// 예: { "loginId": "kkokk1234", "email": "a@a.com", "username": "홍길동", "password": "12345678", "branchId": 1, "position": "사원", "role": "STAFF" }
//
// User 엔티티를 직접 @RequestBody로 받지 않고 DTO를 따로 두는 이유:
// 1) User 엔티티에는 userId, role, status, createdAt 등 클라이언트가 직접 입력하면 안 되는 필드가 있음
// 2) 요청 전용 검증 규칙(@NotBlank, @Email 등)을 엔티티와 분리해서 관리 가능
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDTO {

    // Bean Validation 어노테이션들: 컨트롤러에서 @Valid와 함께 쓰이면
    // 조건을 만족하지 않을 경우 자동으로 MethodArgumentNotValidException이 발생함
    // -> CustomControllerAdvice의 handleValidation()에서 잡아서 400 응답으로 변환됨

    // 로그인 전용 아이디. 프론트 요구사항으로 로그인은 email이 아니라 이 값으로 함
    @NotBlank(message = "로그인 아이디는 필수입니다.")
    @Size(min = 4, max = 20, message = "로그인 아이디는 4자 이상 20자 이하로 입력해주세요.")
    private String loginId;

    @NotBlank(message = "이메일은 필수입니다.") // null, "", 공백만 있는 문자열을 모두 막음
    @Email(message = "이메일 형식이 올바르지 않습니다.") // "a@a.com" 같은 이메일 형식인지 검사
    private String email;

    @NotBlank(message = "유저명은 필수입니다.")
    @Size(min = 2, max = 20, message = "유저명은 2자 이상 20자 이하로 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하로 입력해주세요.") // 너무 짧거나 긴 비밀번호 방지
    private String password;

    // 2026-07-16 회의록 기준: 회원가입 시 소속 지점 선택 필수
    @NotNull(message = "지점 선택은 필수입니다.")
    private Integer branchId;

    // 회사 내 직함 (Role과는 별개의 단순 표시용 정보)
    @NotBlank(message = "직급은 필수입니다.")
    @Size(max = 50, message = "직급은 50자 이하로 입력해주세요.")
    private String position;

    // 사용자가 신청하는 시스템 권한. ADMIN은 서비스 계층(UserServiceImpl)에서 별도로 막는다
    // (셀프 가입으로 관리자 권한을 받을 수 없게 하기 위한 안전장치 - ADMIN도 여기서 막으면 검증 메시지가
    //  덜 명확해서, 일부러 값 자체는 받아준 뒤 서비스 로직에서 명확한 에러 메시지로 거부한다)
    @NotNull(message = "권한 선택은 필수입니다.")
    private Role role;

}
