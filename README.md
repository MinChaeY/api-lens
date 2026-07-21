# API Lens

> Swagger/OpenAPI 명세를 기반으로 API 테스트 케이스를 관리하고  
> 테스트 실행 결과를 확인할 수 있는 API 테스트 관리 플랫폼

## 프로젝트 소개

API Lens는 Swagger/OpenAPI 문서를 등록하면 API 정보를 분석하고,
API별 테스트 케이스를 작성·실행할 수 있도록 지원하는 서비스입니다.

반복적인 API 검증과 테스트 결과 정리를 효율적으로 관리하는 것을 목표로 합니다.

## 주요 기능

- Swagger/OpenAPI 문서 등록
- API 엔드포인트 자동 분석
- API별 테스트 케이스 관리
- 실제 API 요청 실행
- 예상 결과와 실제 응답 비교
- 테스트 성공·실패 이력 관리
- AI 기반 테스트 케이스 초안 생성

> 현재 개발 중인 프로젝트이며 기능은 순차적으로 구현할 예정입니다.

## 기술 스택

### Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Gradle

### Database

- MySQL

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

- [x] Spring Boot 프로젝트 초기 설정
- [ ] MySQL 연결
- [ ] 회원가입 및 로그인
- [ ] JWT 인증
- [ ] 프로젝트 관리
- [ ] Swagger/OpenAPI 분석
- [ ] 테스트 케이스 관리
- [ ] API 테스트 실행
- [ ] 테스트 결과 관리
- [ ] AI 테스트 케이스 생성
- [ ] 서비스 배포

## 프로젝트 구조

```text
src
├── main
│   ├── java
│   │   └── com.apilens
│   └── resources
└── test