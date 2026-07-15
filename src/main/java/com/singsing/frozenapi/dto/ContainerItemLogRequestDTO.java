package com.singsing.frozenapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// 컨테이너 적재 등록 요청 DTO
// loaded_at/unloaded_at은 클라이언트가 정하지 않는다.
// loaded_at은 등록 시점에 서버가 자동으로 채우고(@CreatedDate), unloaded_at은 별도 출고 API에서만 채워진다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainerItemLogRequestDTO {

    @NotNull(message = "컨테이너 ID는 필수입니다.")
    private Integer containerId;

    @NotNull(message = "품목 ID는 필수입니다.")
    private Integer itemId;

    // DB설계.pdf 기준 quantity는 Null 허용이라 @NotNull은 걸지 않고, 값이 있을 때만 범위를 검증
    @DecimalMin(value = "0.0", inclusive = false, message = "적재 수량은 0보다 커야 합니다.")
    private BigDecimal quantity;

}
