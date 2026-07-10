package com.singsing.frozenapi.domain;

// 회원 계정의 상태를 나타내는 enum
// ERD의 users.status 컬럼(varchar)에 대응되며, DB에는 문자열로 저장됨
public enum UserStatus {
    ACTIVE,   // 정상 활성 상태 (회원가입 완료 시 기본값)
    INACTIVE; // 비활성 상태 (예: 탈퇴, 정지 등 추후 기능 확장 시 사용)
}
