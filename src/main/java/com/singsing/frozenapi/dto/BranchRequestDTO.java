package com.singsing.frozenapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 지점 등록(생성)/수정 요청 DTO
// status는 여기 포함하지 않는다 - 등록 시 기본값 ACTIVE로 고정, 이후 상태 변경은
// BranchStatusUpdateRequestDTO를 쓰는 전용 API(PATCH /api/branches/{id}/status)로만 가능
// (회의록: "삭제 대신 폐점·휴점으로만 상태 변경" - 일반 수정과 상태 변경을 API 레벨에서부터 분리)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchRequestDTO {

    @NotBlank(message = "지점명은 필수입니다.")
    @Size(max = 50, message = "지점명은 50자 이하로 입력해주세요.")
    private String name;

    @Size(max = 100, message = "지점 주소는 100자 이하로 입력해주세요.")
    private String address; // 선택 입력 (Null 허용)

}
