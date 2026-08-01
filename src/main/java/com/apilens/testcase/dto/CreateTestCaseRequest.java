package com.apilens.testcase.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTestCaseRequest(

        @NotBlank(message = "테스트 케이스 이름은 필수입니다.")
        @Size(max = 200, message = "테스트 케이스 이름은 200자 이하여야 합니다.")
        String name,

        String requestHeaders,

        String requestBody,

        @NotNull(message = "예상 상태 코드는 필수입니다.")
        @Min(value = 100, message = "상태 코드는 100 이상이어야 합니다.")
        @Max(value = 599, message = "상태 코드는 599 이하여야 합니다.")
        Integer expectedStatus,

        String expectedBody
) {
}