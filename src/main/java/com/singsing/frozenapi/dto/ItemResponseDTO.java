package com.singsing.frozenapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDTO {

    private Integer itemId;
    private String name;
    private String foodType;
    private Integer shelfLifeDays;

}
