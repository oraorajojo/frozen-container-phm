package com.singsing.frozenapi.domain;

// 회원의 권한(등급)을 나타내는 enum
// DB에는 문자열("ADMIN", "MANAGER", "STAFF")로 저장됨 (User.java에서 @Enumerated(EnumType.STRING) 사용)
// 이 시스템은 일반 사용자(고객) 회원가입이 아니라, 냉동 컨테이너를 관리하는 내부 인력용 계정 구조
//
// 2026-07-16 회의록 기준 3단계 권한 체계:
// ADMIN(관리자): 모든 기능 + 관리자 전용 페이지
// MANAGER(매니저): 컨테이너 내부 수치 관리, 품목 입출고
// STAFF(스태프): 조회만 가능
public enum Role {
    ADMIN,   // 관리자
    MANAGER, // 매니저 (스태프가 권한 신청을 통해 승격되는 등급)
    STAFF;   // 일반 직원 (회원가입 시 기본으로 부여되는 권한. admin/manager는 셀프 가입으로 부여하지 않음)
}
