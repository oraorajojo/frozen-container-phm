package com.singsing.frozenapi.repository;

import com.singsing.frozenapi.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Integer> {
}
