package com.singsing.frozenapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponseDTO {

    private Integer alertId;
    private Integer containerId;
    private String source;   // enum -> 문자열 ("COMPRESSOR" 등)
    private String grade;    // enum -> 문자열 ("GREEN" 등)
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

}
