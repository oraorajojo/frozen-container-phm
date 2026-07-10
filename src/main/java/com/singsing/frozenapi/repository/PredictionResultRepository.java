package com.singsing.frozenapi.repository;

import com.singsing.frozenapi.entity.PredictionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PredictionResultRepository extends JpaRepository<PredictionResult, Integer> {
    List<PredictionResult> findByContainerIdOrderByPredictedAtDesc(Integer containerId);
}