package com.singsing.frozenapi.dto;

import com.singsing.frozenapi.domain.BranchStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 지점 상태 변경 전용 요청 DTO (PATCH /api/branches/{id}/status)
// 예: { "status": "CLOSED" }
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchStatusUpdateRequestDTO {

    @NotNull(message = "변경할 상태값은 필수입니다.")
    private BranchStatus status; // "ACTIVE" / "CLOSED" / "PAUSED" 문자열이 자동으로 enum에 매핑됨

}
