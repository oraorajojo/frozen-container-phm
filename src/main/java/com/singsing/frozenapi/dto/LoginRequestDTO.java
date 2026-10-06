package com.singsing.frozenapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 로그인 요청 DTO
// 예: { "loginId": "kkokk1234", "password": "12345678" }
// 로그인은 email이 아니라 loginId로 한다 (프론트 요구사항)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @NotBlank(message = "로그인 아이디는 필수입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

}
