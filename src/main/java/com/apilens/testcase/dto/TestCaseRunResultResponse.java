package com.apilens.testcase.dto;

import java.util.List;
import java.util.Map;

public record TestCaseRunResultResponse(
        Long resultId,
        boolean success,
        Integer actualStatus,
        String actualBody,
        Map<String, List<String>> actualHeaders,
        Long responseTimeMs,
        String errorMessage
) {
}
