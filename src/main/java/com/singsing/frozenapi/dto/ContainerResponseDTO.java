package com.singsing.frozenapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 컨테이너 등록/조회/수정 결과로 클라이언트에게 돌려줄 응답(response) DTO
//
// Container 엔티티를 그대로 리턴하지 않고 별도 DTO로 변환해서 내려주는 이유:
// - 엔티티 구조가 바뀌어도 API 응답 형태를 독립적으로 유지할 수 있음 (User 기능에서도 같은 이유로 SignupResponseDTO를 따로 둠)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainerResponseDTO {

    private Long containerId;     // 컨테이너 PK
    private String modelName;
    private String installLocation;
    private LocalDate registeredAt; // 등록일 (서버가 자동으로 채운 값)

}
