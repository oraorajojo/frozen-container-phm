package com.singsing.frozenapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PredictResponseDto {
    @JsonProperty("RUL_pred") private double rulPred;
    @JsonProperty("food_type") private String foodType;
    @JsonProperty("freshness_grade") private String freshnessGrade;
    @JsonProperty("prob_Green") private double probGreen;
    @JsonProperty("prob_Yellow") private double probYellow;
    @JsonProperty("prob_Red") private double probRed;
    @JsonProperty("warning") private String warning;
}