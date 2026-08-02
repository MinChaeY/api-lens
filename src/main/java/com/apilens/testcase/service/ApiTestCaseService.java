package com.apilens.testcase.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apilens.endpoint.domain.ApiEndpoint;
import com.apilens.endpoint.exception.EndpointNotFoundException;
import com.apilens.endpoint.repository.ApiEndpointRepository;
import com.apilens.project.exception.ProjectNotFoundException;
import com.apilens.project.repository.ApiProjectRepository;
import com.apilens.testcase.domain.ApiTestCase;
import com.apilens.testcase.dto.CreateTestCaseRequest;
import com.apilens.testcase.dto.TestCaseResponse;
import com.apilens.testcase.exception.TestCaseNotFoundException;
import com.apilens.testcase.repository.ApiTestCaseRepository;
import com.apilens.testcase.repository.ApiTestResultRepository;
import com.apilens.testcase.dto.UpdateTestCaseRequest;
import com.apilens.testcase.dto.TestCaseRunResultResponse;
import com.apilens.testcase.domain.ApiTestResult;
import com.apilens.project.domain.ApiProject;

import java.net.URI;
import java.net.InetAddress;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiTestCaseService {

    private final ApiProjectRepository apiProjectRepository;
    private final ApiEndpointRepository apiEndpointRepository;
    private final ApiTestCaseRepository apiTestCaseRepository;
    private final ApiTestResultRepository apiTestResultRepository;

    @Transactional
    public TestCaseResponse createTestCase(
            Long ownerId,
            Long projectId,
            Long endpointId,
            CreateTestCaseRequest request
    ) {
        apiProjectRepository
                .findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(ProjectNotFoundException::new);

        ApiEndpoint endpoint = apiEndpointRepository
                .findByIdAndProjectId(endpointId, projectId)
                .orElseThrow(EndpointNotFoundException::new);

        ApiTestCase testCase = new ApiTestCase(
                endpoint,
                request.name(),
                request.requestHeaders(),
                request.requestBody(),
                request.expectedStatus(),
                request.expectedBody()
        );

        ApiTestCase savedTestCase =
                apiTestCaseRepository.save(testCase);

        return TestCaseResponse.from(savedTestCase);
    }

    @Transactional(readOnly = true)
    public List<TestCaseResponse> getTestCases(
            Long ownerId,
            Long projectId,
            Long endpointId
    ) {
        apiProjectRepository
                .findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(ProjectNotFoundException::new);

        apiEndpointRepository
                .findByIdAndProjectId(endpointId, projectId)
                .orElseThrow(EndpointNotFoundException::new);

        return apiTestCaseRepository
                .findAllByEndpointIdOrderByCreatedAtDesc(endpointId)
                .stream()
                .map(TestCaseResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TestCaseResponse getTestCase(
            Long ownerId,
            Long projectId,
            Long endpointId,
            Long testCaseId
    ) {
        apiProjectRepository
                .findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(ProjectNotFoundException::new);

        apiEndpointRepository
                .findByIdAndProjectId(endpointId, projectId)
                .orElseThrow(EndpointNotFoundException::new);

        ApiTestCase testCase = apiTestCaseRepository
                .findByIdAndEndpointId(testCaseId, endpointId)
                .orElseThrow(TestCaseNotFoundException::new);

        return TestCaseResponse.from(testCase);
    }
    @Transactional
    public TestCaseResponse updateTestCase(
            Long ownerId,
            Long projectId,
            Long endpointId,
            Long testCaseId,
            UpdateTestCaseRequest request
    ) {
        apiProjectRepository
                .findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(ProjectNotFoundException::new);

        apiEndpointRepository
                .findByIdAndProjectId(endpointId, projectId)
                .orElseThrow(EndpointNotFoundException::new);

        ApiTestCase testCase = apiTestCaseRepository
                .findByIdAndEndpointId(testCaseId, endpointId)
                .orElseThrow(TestCaseNotFoundException::new);

        testCase.update(
                request.name(),
                request.requestHeaders(),
                request.requestBody(),
                request.expectedStatus(),
                request.expectedBody()
        );

        return TestCaseResponse.from(testCase);
    }
    
    @Transactional
    public void deleteTestCase(
            Long ownerId,
            Long projectId,
            Long endpointId,
            Long testCaseId
    ) {
        apiProjectRepository
                .findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(ProjectNotFoundException::new);

        apiEndpointRepository
                .findByIdAndProjectId(endpointId, projectId)
                .orElseThrow(EndpointNotFoundException::new);

        ApiTestCase testCase = apiTestCaseRepository
                .findByIdAndEndpointId(testCaseId, endpointId)
                .orElseThrow(TestCaseNotFoundException::new);

        apiTestResultRepository.deleteAllByTestCaseId(testCaseId);
 
        apiTestCaseRepository.delete(testCase);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public TestCaseRunResultResponse runTestCase(
            Long ownerId,
            Long projectId,
            Long endpointId,
            Long testCaseId
    ) {
        ApiProject project = apiProjectRepository
                .findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(ProjectNotFoundException::new);

        ApiEndpoint endpoint = apiEndpointRepository
                .findByIdAndProjectId(endpointId, projectId)
                .orElseThrow(EndpointNotFoundException::new);

        ApiTestCase testCase = apiTestCaseRepository
                .findByIdAndEndpointId(testCaseId, endpointId)
                .orElseThrow(TestCaseNotFoundException::new);

        String path = endpoint.getPath();
        // 1. 미치환 경로 템플릿 검사
        if (path != null && path.contains("{")) {
            throw new IllegalArgumentException("치환되지 않은 경로 변수가 템플릿에 존재하여 실행할 수 없습니다: " + path);
        }

        String baseUrl = project.getBaseUrl();
        // URL/경로 구분선 처리
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            path = path.substring(1);
        } else if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            path = "/" + path;
        }
        String fullUrl = baseUrl + path;

        // 2. URL 유효성 검사 및 SSRF 차단
        validateSafeUrl(fullUrl);

        // 3. HttpClient 및 Request 빌드
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofSeconds(10));

        // Method & Body
        String method = endpoint.getHttpMethod().name();
        String requestBody = testCase.getRequestBody();
        HttpRequest.BodyPublisher bodyPublisher;
        if (requestBody == null || requestBody.isBlank()) {
            bodyPublisher = HttpRequest.BodyPublishers.noBody();
        } else {
            bodyPublisher = HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8);
        }
        requestBuilder.method(method, bodyPublisher);

        // Headers
        Map<String, List<String>> parsedHeaders = parseHeaders(testCase.getRequestHeaders());
        parsedHeaders.forEach((key, values) -> {
            for (String value : values) {
                try {
                    requestBuilder.header(key, value);
                } catch (IllegalArgumentException e) {
                    // 제한된 헤더의 경우 HttpClient 충돌 방지를 위해 생략
                }
            }
        });

        // 4. 요청 실행
        Integer actualStatus = null;
        String actualBody = null;
        Map<String, List<String>> responseHeaders = new HashMap<>();
        long responseTimeMs = 0;
        String errorMessage = null;
        boolean passed = false;

        long startTime = System.currentTimeMillis();
        try {
            HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            responseTimeMs = System.currentTimeMillis() - startTime;
            actualStatus = response.statusCode();
            actualBody = response.body();

            // 응답 헤더 추출
            response.headers().map().forEach(responseHeaders::put);

            // 5. 성공 여부 판정
            passed = (actualStatus.equals(testCase.getExpectedStatus()));
            if (passed && testCase.getExpectedBody() != null && !testCase.getExpectedBody().trim().isEmpty()) {
                String expectedBody = testCase.getExpectedBody().trim();
                String actualBodyTrimmed = actualBody != null ? actualBody.trim() : "";
                
                boolean isExpectedJson = expectedBody.startsWith("{") || expectedBody.startsWith("[");
                boolean isActualJson = actualBodyTrimmed.startsWith("{") || actualBodyTrimmed.startsWith("[");
                
                if (isExpectedJson && isActualJson) {
                    passed = isJsonSubset(expectedBody, actualBodyTrimmed);
                } else {
                    // JSON이 아니면 단순 포함(substring) 비교
                    passed = actualBodyTrimmed.contains(expectedBody);
                }
            }

        } catch (Exception e) {
            responseTimeMs = System.currentTimeMillis() - startTime;
            errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            passed = false;
        }

        // 6. 결과 이력 DB 적재
        ApiTestResult testResult = new ApiTestResult(
                testCase,
                fullUrl,
                testCase.getExpectedStatus(),
                actualStatus,
                testCase.getExpectedBody(),
                actualBody,
                passed,
                errorMessage,
                responseTimeMs
        );
        apiTestResultRepository.save(testResult);

        return new TestCaseRunResultResponse(
                testResult.getId(),
                passed,
                actualStatus,
                actualBody,
                responseHeaders,
                responseTimeMs,
                errorMessage
        );
    }

    private void validateSafeUrl(String urlString) {
        URI uri;
        try {
            uri = URI.create(urlString);
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 URL 형식입니다.");
        }

        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException("HTTP 또는 HTTPS 프로토콜만 허용됩니다.");
        }

        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("호스트가 존재하지 않습니다.");
        }

        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress address : addresses) {
                if (address.isLoopbackAddress() ||
                    address.isSiteLocalAddress() ||
                    address.isLinkLocalAddress() ||
                    address.isMulticastAddress() ||
                    address.isAnyLocalAddress()) {
                    throw new IllegalArgumentException("허용되지 않는 IP 대역(사설/루프백/링크로컬 등)입니다.");
                }
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) e;
            }
            throw new IllegalArgumentException("호스트 IP 주소를 확인할 수 없습니다: " + host);
        }
    }

    private boolean isJsonSubset(String expectedJson, String actualJson) {
        try {
            JsonNode expectedNode = objectMapper.readTree(expectedJson);
            JsonNode actualNode = objectMapper.readTree(actualJson);
            return containsNode(actualNode, expectedNode);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean containsNode(JsonNode actual, JsonNode expected) {
        if (expected.isObject()) {
            if (!actual.isObject()) {
                return false;
            }
            var fields = expected.fields();
            while (fields.hasNext()) {
                var field = fields.next();
                String fieldName = field.getKey();
                JsonNode expectedValue = field.getValue();
                if (!actual.has(fieldName)) {
                    return false;
                }
                JsonNode actualValue = actual.get(fieldName);
                if (!containsNode(actualValue, expectedValue)) {
                    return false;
                }
            }
            return true;
        } else if (expected.isArray()) {
            if (!actual.isArray()) {
                return false;
            }
            for (JsonNode expectedElem : expected) {
                boolean found = false;
                for (JsonNode actualElem : actual) {
                    if (containsNode(actualElem, expectedElem)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            return true;
        } else {
            return actual.equals(expected);
        }
    }

    private Map<String, List<String>> parseHeaders(String headersString) {
        Map<String, List<String>> headersMap = new HashMap<>();
        if (headersString == null || headersString.isBlank()) {
            return headersMap;
        }

        String trimmed = headersString.trim();
        if (trimmed.startsWith("{")) {
            try {
                JsonNode node = objectMapper.readTree(trimmed);
                var fields = node.fields();
                while (fields.hasNext()) {
                    var field = fields.next();
                    String key = field.getKey();
                    JsonNode valueNode = field.getValue();
                    List<String> values = new ArrayList<>();
                    if (valueNode.isArray()) {
                        for (JsonNode val : valueNode) {
                            values.add(val.asText());
                        }
                    } else {
                        values.add(valueNode.asText());
                    }
                    headersMap.put(key, values);
                }
                return headersMap;
            } catch (Exception e) {
                // JSON 파싱 실패 시 일반 Key-Value 파싱으로 전환
            }
        }

        String[] lines = trimmed.split("\\r?\\n");
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                if (!key.isEmpty()) {
                    headersMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                }
            }
        }

        return headersMap;
    }
}