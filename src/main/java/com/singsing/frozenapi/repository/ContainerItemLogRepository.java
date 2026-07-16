package com.singsing.frozenapi.repository;

import com.singsing.frozenapi.domain.ContainerItemLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContainerItemLogRepository extends JpaRepository<ContainerItemLog, Integer> {

    // unloaded_at이 NULL인 로그만 조회 = 아직 출고되지 않아 "현재 컨테이너에 실려있는" 품목만 골라내는 쿼리
    // (컨테이너 하나에 여러 품목이 쌓일 수 있어서, "지금 뭐가 실려있나"는 이렇게 조회로만 알 수 있다는 걸 보여주는 메서드)
    List<ContainerItemLog> findByContainerIdAndUnloadedAtIsNull(Integer containerId);

    // PredictionService에서 사용: 특정 컨테이너에 "현재 실려있는" 품목 중 가장 최근에 적재된 것 하나만 조회
    // (원래 종선님 entity.ContainerItemLogRepository에 있던 메서드를 domain 버전으로 그대로 옮김)
    Optional<ContainerItemLog> findFirstByContainerIdAndUnloadedAtIsNullOrderByLoadedAtDesc(Integer containerId);

}
