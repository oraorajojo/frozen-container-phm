package com.singsing.frozenapi.service;

import com.singsing.frozenapi.dto.*;
import com.singsing.frozenapi.entity.*;
import com.singsing.frozenapi.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    @Value("${phm.python.path}")
    private String pythonPath;
    @Value("${phm.script.path}")
    private String scriptPath;

    private final SensorReadingRepository sensorReadingRepo;
    private final PredictionResultRepository predictionResultRepo;
    private final ContainerItemLogRepository containerItemLogRepo;
    private final ItemRepository itemRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CALC_VERSION = "xgb_json_v2_foodtype";

    public PredictionService(SensorReadingRepository s, PredictionResultRepository p,
                             ContainerItemLogRepository c, ItemRepository i) {
        this.sensorReadingRepo = s;
        this.predictionResultRepo = p;
        this.containerItemLogRepo = c;
        this.itemRepo = i;
    }

    public PredictResponseDto predict(Integer readingId) throws Exception {
        SensorReading latest = sensorReadingRepo.findById(readingId)
                .orElseThrow(() -> new RuntimeException("reading_id 없음: " + readingId));

        // 현재 컨테이너에 실린 품목 조회 (unloaded_at이 null = 아직 하역 안 됨)
        ContainerItemLog log = containerItemLogRepo
                .findFirstByContainerIdAndUnloadedAtIsNullOrderByLoadedAtDesc(latest.getContainerId())
                .orElseThrow(() -> new RuntimeException("현재 적재된 품목 없음: container_id=" + latest.getContainerId()));
        Item item = itemRepo.findById(log.getItemId())
                .orElseThrow(() -> new RuntimeException("item 없음: item_id=" + log.getItemId()));
        String foodType = item.getFoodType();

        List<SensorReading> window = sensorReadingRepo
                .findTop5ByContainerIdOrderByRecordedAtDesc(latest.getContainerId());

        double vibMean = window.stream().mapToDouble(SensorReading::getVibration).average().orElse(0);
        double oilMean = window.stream().mapToDouble(SensorReading::getOilPressure).average().orElse(0);
        double tempMean = window.stream().mapToDouble(SensorReading::getDischargeTemp).average().orElse(0);
        double curMean = window.stream().mapToDouble(SensorReading::getMotorCurrent).average().orElse(0);

        double vibStd = std(window.stream().mapToDouble(SensorReading::getVibration).toArray(), vibMean);
        double oilStd = std(window.stream().mapToDouble(SensorReading::getOilPressure).toArray(), oilMean);
        double tempStd = std(window.stream().mapToDouble(SensorReading::getDischargeTemp).toArray(), tempMean);
        double curStd = std(window.stream().mapToDouble(SensorReading::getMotorCurrent).toArray(), curMean);

        double deltaT = 0.4*latest.getDischargeTemp() + 0.3*latest.getVibration()
                + 0.2*(1-latest.getOilPressure()) + 0.1*latest.getMotorCurrent();
        double fdr = Math.pow(2.5, deltaT/10);
        long cycle = sensorReadingRepo.countByContainerId(latest.getContainerId());

        String inputJson = String.format(
                "{\\\"food_type\\\":\\\"%s\\\",\\\"vibration\\\":%f,\\\"oil_pressure\\\":%f,\\\"discharge_temp\\\":%f,\\\"motor_current\\\":%f," +
                        "\\\"vibration_roll_mean\\\":%f,\\\"oil_pressure_roll_mean\\\":%f,\\\"discharge_temp_roll_mean\\\":%f,\\\"motor_current_roll_mean\\\":%f," +
                        "\\\"vibration_roll_std\\\":%f,\\\"oil_pressure_roll_std\\\":%f,\\\"discharge_temp_roll_std\\\":%f,\\\"motor_current_roll_std\\\":%f," +
                        "\\\"delta_T_weighted\\\":%f,\\\"FDR\\\":%f,\\\"cycle\\\":%d}",
                foodType,
                latest.getVibration(), latest.getOilPressure(), latest.getDischargeTemp(), latest.getMotorCurrent(),
                vibMean, oilMean, tempMean, curMean,
                vibStd, oilStd, tempStd, curStd,
                deltaT, fdr, cycle
        );

        ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptPath, inputJson);
        pb.environment().put("PYTHONIOENCODING", "utf-8");
        pb.redirectErrorStream(true);
        Process process = pb.start();

        String output;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
            output = reader.lines().collect(Collectors.joining()).trim();
        }
        process.waitFor();

        if (output.contains("\"error\"")) throw new RuntimeException("예측 실패: " + output);

        PredictResponseDto response = objectMapper.readValue(output, PredictResponseDto.class);

        PredictionResult result = new PredictionResult();
        result.setReadingId(latest.getReadingId());
        result.setContainerId(latest.getContainerId());
        result.setFoodType(foodType);
        result.setRulPredicted(response.getRulPred());
        result.setDeltaT(deltaT);
        result.setFdr(fdr);
        result.setFreshnessGrade(response.getFreshnessGrade());
        result.setProbGreen(response.getProbGreen());
        result.setProbYellow(response.getProbYellow());
        result.setProbRed(response.getProbRed());
        result.setCalcVersion(CALC_VERSION);
        result.setPredictedAt(LocalDateTime.now());
        predictionResultRepo.save(result);

        return response;
    }

    private double std(double[] values, double mean) {
        double sumSq = 0;
        for (double v : values) sumSq += Math.pow(v - mean, 2);
        return Math.sqrt(sumSq / values.length);
    }
}