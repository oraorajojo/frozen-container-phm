package com.singsing.frozenapi.repository;

import com.singsing.frozenapi.entity.ContainerItemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ContainerItemLogRepository extends JpaRepository<ContainerItemLog, Integer> {
    Optional<ContainerItemLog> findFirstByContainerIdAndUnloadedAtIsNullOrderByLoadedAtDesc(Integer containerId);
}