package com.apilens.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(

        @NotBlank(message = "프로젝트 이름은 필수입니다.")
        @Size(max = 100, message = "프로젝트 이름은 100자 이하여야 합니다.")
        String name,

        @NotBlank(message = "Base URL은 필수입니다.")
        @Size(max = 500, message = "Base URL은 500자 이하여야 합니다.")
        @Pattern(
                regexp = "^https?://.+",
                message = "Base URL은 http:// 또는 https://로 시작해야 합니다."
        )
        String baseUrl,

        @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
        String description

) {
}