package com.singsing.frozenapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainerItemLogResponseDTO {

    private Integer logId;
    private Integer containerId;
    private Integer itemId;
    private BigDecimal quantity;
    private LocalDateTime loadedAt;
    private LocalDateTime unloadedAt; // null이면 아직 컨테이너에 실려있는 중

}
