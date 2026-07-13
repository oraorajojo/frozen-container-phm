package com.singsing.frozenapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 회원가입 완료 후 클라이언트에게 돌려줄 응답(response) DTO
//
// User 엔티티를 그대로 리턴하지 않는 이유:
// - passwordHash(암호화된 비밀번호라도)를 응답에 절대 포함시키면 안 되기 때문에
//   꼭 필요한 필드만 골라서 별도의 DTO로 변환(entityToDTO)해서 내려준다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponseDTO {

    private Integer userId;    // 생성된 회원의 PK
    private String email;
    private String username;
    private String role;       // enum Role을 문자열로 변환해서 전달 (Role.name() 결과, 예: "USER")
    private String status;     // enum UserStatus를 문자열로 변환해서 전달 (예: "ACTIVE")
    private LocalDateTime createdAt; // 가입 시각

}
