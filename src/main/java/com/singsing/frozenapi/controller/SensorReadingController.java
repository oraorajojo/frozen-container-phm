package com.singsing.frozenapi.controller;

import com.singsing.frozenapi.entity.SensorReading;
import com.singsing.frozenapi.repository.SensorReadingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SensorReadingController {

    private final SensorReadingRepository sensorReadingRepo;

    public SensorReadingController(SensorReadingRepository repo) {
        this.sensorReadingRepo = repo;
    }

    @PostMapping("/sensor-readings")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> data) {
        SensorReading reading = new SensorReading();
        reading.setContainerId((Integer) data.get("containerId"));
        reading.setVibration(((Number) data.get("vibration")).doubleValue());
        reading.setOilPressure(((Number) data.get("oilPressure")).doubleValue());
        reading.setDischargeTemp(((Number) data.get("dischargeTemp")).doubleValue());
        reading.setMotorCurrent(((Number) data.get("motorCurrent")).doubleValue());
        reading.setRecordedAt(LocalDateTime.now());
        sensorReadingRepo.save(reading);
        return ResponseEntity.ok(Map.of("readingId", reading.getReadingId()));
    }
}