package com.singsing.frozenapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 토큰 재발급 응답 DTO
// 로그인 응답(LoginResponseDTO)과 달리 회원 정보 없이 토큰 두 개만 내려준다.
// (재발급은 이미 로그인된 상태에서 토큰만 갱신하는 것이므로 회원 정보를 다시 내려줄 필요가 없음)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDTO {

    private String accessToken;
    private String refreshToken;

}
