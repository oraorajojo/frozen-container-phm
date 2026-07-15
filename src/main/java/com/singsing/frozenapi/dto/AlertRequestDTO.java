package com.singsing.frozenapi.dto;

import com.singsing.frozenapi.domain.AlertGrade;
import com.singsing.frozenapi.domain.AlertSource;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 알림 등록 요청 DTO
// is_read/created_at은 클라이언트가 정하지 않는다 (is_read는 항상 false로 시작, created_at은 서버가 자동으로 채움)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRequestDTO {

    @NotNull(message = "컨테이너 ID는 필수입니다.")
    private Integer containerId;

    @NotNull(message = "알림 발생 소스는 필수입니다.")
    private AlertSource source; // 요청 JSON의 "COMPRESSOR" 또는 "SHELF_LIFE" 문자열이 자동으로 enum에 매핑됨

    @NotNull(message = "알림 등급은 필수입니다.")
    private AlertGrade grade;   // "GREEN" / "YELLOW" / "RED"

    @Size(max = 255, message = "메시지는 255자 이하로 입력해주세요.")
    private String message; // 선택 입력 (Null 허용)

}
