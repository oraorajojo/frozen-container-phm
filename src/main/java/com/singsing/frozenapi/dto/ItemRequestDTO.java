package com.singsing.frozenapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 품목(item) 등록(생성)/수정 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDTO {

    @NotBlank(message = "품목명은 필수입니다.")
    @Size(max = 50, message = "품목명은 50자 이하로 입력해주세요.")
    private String name;

    @NotBlank(message = "품목군은 필수입니다.")
    @Size(max = 50, message = "품목군은 50자 이하로 입력해주세요.")
    private String foodType;

    @NotNull(message = "유통기한(일수)은 필수입니다.")
    @Positive(message = "유통기한은 1일 이상이어야 합니다.")
    private Integer shelfLifeDays;

}
