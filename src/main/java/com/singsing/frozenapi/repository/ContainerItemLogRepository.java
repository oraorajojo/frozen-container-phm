package com.singsing.frozenapi.repository;

import com.singsing.frozenapi.domain.ContainerItemLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContainerItemLogRepository extends JpaRepository<ContainerItemLog, Integer> {

    // unloaded_at이 NULL인 로그만 조회 = 아직 출고되지 않아 "현재 컨테이너에 실려있는" 품목만 골라내는 쿼리
    // (컨테이너 하나에 여러 품목이 쌓일 수 있어서, "지금 뭐가 실려있나"는 이렇게 조회로만 알 수 있다는 걸 보여주는 메서드)
    List<ContainerItemLog> findByContainerIdAndUnloadedAtIsNull(Integer containerId);

}
