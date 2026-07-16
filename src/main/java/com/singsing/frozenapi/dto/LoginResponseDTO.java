package com.singsing.frozenapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 로그인 성공 시 응답 DTO
// accessToken/refreshToken과 함께 회원 기본 정보를 같이 내려줘서,
// 클라이언트(리액트)가 로그인 직후 별도 조회 없이 바로 화면에 사용자 정보를 표시할 수 있게 한다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String accessToken;  // 짧은 유효시간 (API 요청 시 Authorization 헤더에 사용)
    private String refreshToken; // 긴 유효시간 (accessToken 재발급 전용, /api/users/refresh 에서 사용)

    private Integer userId;
    private String email;
    private String username;
    private String role;
    private String status;
    private Integer branchId;
    private String branchName;

}
