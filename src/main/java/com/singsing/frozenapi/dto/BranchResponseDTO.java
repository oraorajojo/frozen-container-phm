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
public class BranchResponseDTO {

    private Integer branchId;
    private String name;
    private String address;
    private String status;    // enum -> 문자열 ("ACTIVE" 등)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
