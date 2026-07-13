package com.singsing.frozenapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "prediction_result")
@Data
public class PredictionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prediction_id")
    private Integer predictionId;

    @Column(name = "reading_id")
    private Integer readingId;

    @Column(name = "container_id")
    private Integer containerId;

    @Column(name = "rul_predicted")
    private Double rulPredicted;

    @Column(name = "delta_t")
    private Double deltaT;

    private Double fdr;

    @Column(name = "freshness_grade")
    private String freshnessGrade;

    @Column(name = "prob_green")
    private Double probGreen;

    @Column(name = "prob_yellow")
    private Double probYellow;

    @Column(name = "prob_red")
    private Double probRed;

    @Column(name = "calc_version")
    private String calcVersion;

    @Column(name = "predicted_at")
    private LocalDateTime predictedAt;
}