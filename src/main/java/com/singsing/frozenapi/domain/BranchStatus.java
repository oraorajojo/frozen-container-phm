package com.singsing.frozenapi.domain;

// 지점의 운영 상태를 나타내는 enum
// 회의록 기준: 지점은 삭제 대신 폐점/휴점으로만 상태를 바꾼다 (물리 삭제 금지)
public enum BranchStatus {
    ACTIVE, // 운영중
    CLOSED, // 폐점
    PAUSED; // 휴점
}
