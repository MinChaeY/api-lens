# API Lens

> Swagger/OpenAPI 명세를 기반으로 API 테스트 케이스를 관리하고  
> 테스트 실행 결과를 확인할 수 있는 API 테스트 관리 플랫폼

## 프로젝트 소개

API Lens는 Swagger/OpenAPI 문서를 등록하면 문서에 정의된 API 정보를 자동으로 분석하고, API별 테스트 케이스를 작성·실행할 수 있도록 지원하는 서비스입니다.

반복적인 API 검증 과정을 체계적으로 관리하고, 예상 결과와 실제 응답을 비교하여 테스트 결과를 효율적으로 확인하는 것을 목표로 합니다.

현재 사용자 인증, 프로젝트 관리, OpenAPI 문서 등록, API 엔드포인트 추출 및 조회 기능까지 구현하였습니다.

## 주요 기능

- 이메일 기반 회원가입 및 로그인
- JWT 기반 사용자 인증
- 사용자별 프로젝트 접근 제어
- API 프로젝트 등록·조회·수정·삭제
- Swagger/OpenAPI 문서 주소 등록
- OpenAPI 문서의 엔드포인트 자동 추출
- 프로젝트별 API 엔드포인트 목록 및 상세 조회
- API별 테스트 케이스 관리
- 실제 API 요청 실행
- 예상 결과와 실제 응답 비교
- 테스트 성공·실패 이력 관리
- AI 기반 테스트 케이스 초안 생성

> 현재 개발 중인 프로젝트이며 테스트 케이스 관리, API 실행 및 결과 비교 기능을 순차적으로 구현하고 있습니다.

## 기술 스택

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Bean Validation
- JWT
- Swagger Parser
- Gradle

### Database

- MySQL
- Hibernate

### Test

- JUnit 5
- RestAssured

### 예정 기술

- React
- TypeScript
- Docker
- GitHub Actions
- AWS

## 개발 진행 상황

### 개발 환경

- [x] Spring Boot 프로젝트 초기 설정
- [x] MySQL 데이터베이스 연결
- [x] 도메인별 패키지 구조 구성
- [x] 전역 예외 처리 구조 구현
- [x] 환경변수를 이용한 DB 및 JWT 설정 관리

### 사용자 인증

- [x] 사용자 엔티티 및 Repository 구현
- [x] 이메일 회원가입 API 구현
- [x] 회원가입 입력값 검증
- [x] BCrypt 비밀번호 암호화
- [x] 이메일 중복 검사
- [x] 중복 이메일 예외 처리
- [x] 로그인 API 구현
- [x] JWT 액세스 토큰 발급
- [x] Spring Security 기반 JWT 인증
- [x] 로그인 사용자 정보 조회 API 구현
- [x] 사용자별 데이터 접근 제어

### 프로젝트 관리

- [x] API 프로젝트 엔티티 및 Repository 구현
- [x] 프로젝트 등록 API 구현
- [x] 사용자별 프로젝트 목록 조회
- [x] 프로젝트 단건 조회
- [x] 프로젝트 수정
- [x] 프로젝트 삭제
- [x] 프로젝트 소유권 검증

### Swagger/OpenAPI 관리

- [x] 프로젝트별 OpenAPI 문서 주소 등록
- [x] Swagger Parser를 이용한 OpenAPI 문서 분석
- [x] HTTP 메서드 및 경로 정보 자동 추출
- [x] 엔드포인트 요약·설명·operationId 저장
- [x] 기존 엔드포인트 데이터 갱신 처리
- [x] 프로젝트별 엔드포인트 목록 조회
- [x] 엔드포인트 단건 상세 조회
- [x] 프로젝트 및 엔드포인트 소속 검증

### 테스트 케이스 관리

- [x] 테스트 케이스 엔티티 구현
- [x] 테스트 케이스 Repository 구현
- [ ] 테스트 케이스 등록 API
- [ ] 테스트 케이스 목록 및 상세 조회
- [ ] 테스트 케이스 수정
- [ ] 테스트 케이스 삭제
- [ ] 요청 헤더 및 요청 본문 유효성 검증

### API 테스트 실행

- [ ] 등록된 테스트 케이스 기반 API 요청 실행
- [ ] 경로 변수 및 쿼리 파라미터 처리
- [ ] 요청 헤더 및 요청 본문 적용
- [ ] 실제 HTTP 상태 코드 및 응답 본문 저장
- [ ] 예상 결과와 실제 응답 비교
- [ ] 테스트 성공·실패 판정

### 테스트 결과 및 확장 기능

- [ ] 테스트 실행 결과 저장
- [ ] 테스트 결과 목록 및 상세 조회
- [ ] 프로젝트별 테스트 성공률 집계
- [ ] AI 기반 테스트 케이스 초안 생성
- [ ] 프론트엔드 화면 구현
- [ ] Docker 기반 실행 환경 구성
- [ ] GitHub Actions CI/CD 구성
- [ ] AWS 배포

## 현재 구현된 API

### 사용자 인증

```http
POST /api/auth/signup
POST /api/auth/login
GET  /api/users/me
```

### 프로젝트 관리

```http
POST   /api/projects
GET    /api/projects
GET    /api/projects/{projectId}
PUT    /api/projects/{projectId}
DELETE /api/projects/{projectId}
```

### OpenAPI 및 엔드포인트 관리

```http
PUT  /api/projects/{projectId}/openapi
POST /api/projects/{projectId}/endpoints/import
GET  /api/projects/{projectId}/endpoints
GET  /api/projects/{projectId}/endpoints/{endpointId}
```

## 프로젝트 구조

```text
src
├── main
│   ├── java
│   │   └── com
│   │       └── apilens
│   │           ├── global
│   │           │   ├── config
│   │           │   │   └── SecurityConfig.java
│   │           │   ├── exception
│   │           │   │   ├── ErrorResponse.java
│   │           │   │   └── GlobalExceptionHandler.java
│   │           │   └── security
│   │           │       ├── JwtConfig.java
│   │           │       └── JwtTokenProvider.java
│   │           ├── user
│   │           │   ├── controller
│   │           │   ├── domain
│   │           │   ├── dto
│   │           │   ├── exception
│   │           │   ├── repository
│   │           │   └── service
│   │           ├── project
│   │           │   ├── controller
│   │           │   ├── domain
│   │           │   ├── dto
│   │           │   ├── exception
│   │           │   ├── repository
│   │           │   └── service
│   │           ├── endpoint
│   │           │   ├── controller
│   │           │   ├── domain
│   │           │   ├── dto
│   │           │   ├── exception
│   │           │   ├── repository
│   │           │   └── service
│   │           ├── testcase
│   │           │   ├── domain
│   │           │   └── repository
│   │           └── ApiLensApplication.java
│   └── resources
│       └── application.properties
└── test
    └── java
        └── com
            └── apilens
```

## 로컬 실행 환경변수

API Lens는 데이터베이스 접속 정보와 JWT 비밀키를 환경변수로 관리합니다.

```powershell
$env:DB_USERNAME = "apilens_user"
$env:DB_PASSWORD = "데이터베이스 비밀번호"
$env:JWT_SECRET = "Base64 형식의 JWT 비밀키"

.\gradlew.bat bootRun
```

> 환경변수 값은 저장소에 커밋하지 않습니다.