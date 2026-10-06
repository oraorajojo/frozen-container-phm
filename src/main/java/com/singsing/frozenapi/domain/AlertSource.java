package com.singsing.frozenapi.domain;

// 알림이 어디서(어떤 원인으로) 발생했는지를 나타내는 enum
// DB에는 문자열("COMPRESSOR", "SHELF_LIFE")로 저장됨 (User의 Role/UserStatus와 동일한 대문자 저장 컨벤션)
public enum AlertSource {
    COMPRESSOR,  // 압축기(컴프레서) 이상 감지 (sensor_reading 기반)
    SHELF_LIFE;  // 유통기한 임박/초과 감지 (container_item_log + items 기반)
}
