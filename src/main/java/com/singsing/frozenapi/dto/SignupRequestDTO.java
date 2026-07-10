package com.singsing.frozenapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원가입 요청(request)으로 클라이언트가 보내는 JSON 데이터를 담는 DTO
// 예: { "email": "a@a.com", "password": "12345678" }
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

    @NotBlank(message = "이메일은 필수입니다.") // null, "", 공백만 있는 문자열을 모두 막음
    @Email(message = "이메일 형식이 올바르지 않습니다.") // "a@a.com" 같은 이메일 형식인지 검사
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하로 입력해주세요.") // 너무 짧거나 긴 비밀번호 방지
    private String password;

}
