package com.singsing.frozenapi.controller;

import com.singsing.frozenapi.dto.*;
import com.singsing.frozenapi.entity.PredictionResult;
import com.singsing.frozenapi.repository.PredictionResultRepository;
import com.singsing.frozenapi.service.PredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PredictionController {

    private final PredictionService predictionService;
    private final PredictionResultRepository predictionResultRepo;

    public PredictionController(PredictionService s, PredictionResultRepository r) {
        this.predictionService = s;
        this.predictionResultRepo = r;
    }

    @PostMapping("/predictions")
    public ResponseEntity<?> predict(@RequestBody PredictRequestDto request) {
        try {
            return ResponseEntity.ok(predictionService.predict(request.getReadingId()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/predictions/{containerId}")
    public ResponseEntity<List<PredictionResult>> history(@PathVariable Integer containerId) {
        return ResponseEntity.ok(predictionResultRepo.findByContainerIdOrderByPredictedAtDesc(containerId));
    }
}