package com.singsing.frozenapi.repository;

import com.singsing.frozenapi.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Integer> {
    List<SensorReading> findTop5ByContainerIdOrderByRecordedAtDesc(Integer containerId);
    long countByContainerId(Integer containerId);
}