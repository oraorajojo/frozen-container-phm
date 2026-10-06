package com.singsing.frozenapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// accessToken 재발급 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequestDTO {

    @NotBlank(message = "refreshToken은 필수입니다.")
    private String refreshToken;

}
