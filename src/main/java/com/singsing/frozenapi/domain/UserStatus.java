package com.singsing.frozenapi.domain;

// 회원 계정의 상태를 나타내는 enum
// ERD의 users.status 컬럼(varchar)에 대응되며, DB에는 문자열로 저장됨
public enum UserStatus {
    PENDING, // 승인 대기 상태 (회원가입 완료 시 기본값. 관리자 승인 전까지는 이 상태)
    ACTIVE;  // 관리자 승인 후 정상 활성화된 상태
}
