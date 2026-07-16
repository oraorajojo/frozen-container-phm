package com.singsing.frozenapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "sensor_reading")
@Data
public class SensorReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reading_id")
    private Integer readingId;

    @Column(name = "container_id")
    private Integer containerId;

    private Double vibration;

    @Column(name = "oil_pressure")
    private Double oilPressure;

    @Column(name = "discharge_temp")
    private Double dischargeTemp;

    @Column(name = "motor_current")
    private Double motorCurrent;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
}