package com.singsing.frozenapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 컨테이너 등록(생성)/수정 요청으로 클라이언트가 보내는 JSON 데이터를 담는 DTO
// 예: { "modelName": "FRZ-2000", "installLocation": "냉동창고 A동 1구역" }
//
// 등록(POST)과 수정(PUT)에 필요한 값이 동일(model_name, install_location)해서 DTO 하나를 같이 사용한다.
// registered_at은 클라이언트가 정하지 않고 서버가 저장 시점에 자동으로 채우므로(@CreatedDate) 여기에는 포함하지 않는다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainerRequestDTO {

    // Bean Validation: 컨트롤러에서 @Valid와 함께 쓰이면
    // 조건을 만족하지 않을 경우 MethodArgumentNotValidException이 발생 -> CustomControllerAdvice가 400으로 변환
    @NotBlank(message = "모델명은 필수입니다.") // null, "", 공백만 있는 문자열을 모두 막음
    private String modelName;

    @NotBlank(message = "설치 위치는 필수입니다.")
    private String installLocation;

}
