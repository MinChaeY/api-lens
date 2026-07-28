package com.apilens.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterOpenApiRequest(

        @NotBlank(message = "OpenAPI 문서 주소는 필수입니다.")
        @Size(
                max = 1000,
                message = "OpenAPI 문서 주소는 1000자 이하여야 합니다."
        )
        @Pattern(
                regexp = "^https?://.+",
                message = "OpenAPI 문서 주소는 http:// 또는 https://로 시작해야 합니다."
        )
        String openApiUrl

) {
}