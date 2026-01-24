# Self-Hosted Blog Backend Project

- Spring Boot, Java를 기반으로 구축한 블로그 백엔드 프로젝트
- 온프레미스 서버를 통한 배포 환경 구성

---
### 참고 링크

- [온프레미스 서버 스펙 정리](https://github.com/Kimheojin/archive/blob/main/projectReference/blog/spring-blog-backend/server-specifications.md)

- [부하 테스트 결과 및 정리](https://github.com/Kimheojin/archive/blob/main/projectReference/blog/spring-blog-backend/load-test.md)


## 배포 아키텍처

![Blog Architecture](docsAboutMD/image/blogarchi.png)

## 기술 스택

### 백엔드 

- **언어 및 프레임워크**: Java 17, Spring Boot
- **보안**: Spring Security, JWT
- **DB**: MySQL, MongoDB, H2 (테스트용)
- **기타**: Spring Boot Actuator, Commonmark, Spring Data JPA, QueryDSL, Spring Data MongoDB

### 인프라 및 DevOps

- **컨테이너**: Docker, Docker Compose
- **CI/CD**: GitHub Actions, Self-hosted runner
- **스토리지**: Cloudinary (이미지 관리 및 저장)

### 테스트 및 문서화

- **테스트**: JUnit5, MockMvc, Flapdoodle (테스트용 임베디드 MongoDB)
- **문서화**: Spring REST Docs, Asciidoctor

## 주요 기능

- 보안 및 인증
  - JWT, 보안 쿠키(HttpOnly), Refresh Token 자동 갱신
- 콘텐츠 관리
  - Markdown(Commonmark), 카테고리/태그 시스템, 댓글
- 검색 및 SEO
  - MySQL-MongoDB 하이브리드 구조, Atlas Search 전문 검색
- 미디어 관리
  - Cloudinary 연동, AVIF 포맷 변환 및 이미지 최적화
- 예외 처리
  - 전역 예외 처리기(GlobalExceptionHandler), 규격화된 에러 응답
- 인프라
  - Docker Multi-stage 빌드, Docker Compose 관리
- 배포
  - GitHub Actions, Self-hosted Runner, 블루-그린 무중단 배포
- 테스트/문서화
  - JUnit5 통합 테스트, Spring REST Docs 자동화


### 1. 보안 및 인증 

- **쿠키 기반 JWT 인증 체계:**
  - **보안 쿠키 적용:** Access Token 및 Refresh Token 을 `HttpOnly`, `Secure` 설정이 적용된 쿠키에 저장하여 XSS 및 CSRF 공격 위험을 최소화
    - 무상태(Stateless) 인증 구현
    
- **자동 토큰 갱신 시스템 (Refresh Token):**
  - **Persistence 기반 관리:** Refresh Token을 데이터베이스(MySQL)에 저장하여 관리
  - **인증 필터 내 자동 갱신:** Access Token이 만료되었을 때, `JwtAuthenticationFilter`에서 유효한 Refresh Token을 확인하여 자동으로 새로운 Access Token을 발급하고 쿠키를 업데이트
  
- **Spring Security:** 역할 기반(ADMIN, USER) 접근 제어 및 보안 예외 처리 커스터마이징

### 2. Content 관리 (Content Management)

- **포스트 관리:** 게시글 작성, 수정, 삭제 및 조회 (Markdown 지원 - CommonMark)
- **카테고리:** 게시글 분류를 위한 카테고리 생성 및 관리
- **태그 시스템:** 게시글 검색 및 분류를 위한 태그 기능
- **댓글:** 게시글에 대한 댓글 작성 및 관리자 삭제 기능
- **이미지 최적화 저장:** 게시글 내 포함되는 이미지를 효율적으로 관리하고 최적화된 상태로 저장

### 3. 검색 및 SEO (Search & SEO)

- **하이브리드 DB 구조:**
  - MySQL: 주요 데이터(회원, 게시글 등) 관리
  - MongoDB: 검색 성능 최적화 및 SEO 데이터를 위한 보조 저장소

- **통합 검색**: MongoDB Atlas Search를 활용한 통합 검색 기능 제공
  - **데이터 동기화 전략**:
    - **수동 동기화**: 관리자가 API 엔드포인트를 통해 즉시 MySQL 데이터를 MongoDB로 동기화 가능
    - **자동 스케줄링**: 데이터 최신화 및 일관성 유지를 위해 정해진 시각에 전체 데이터 자동 동기화 수행

### 4. Image 파일 관리 (Media Management)

- **Cloudinary 연동:** 이미지 업로드 및 관리를 위해 외부 스토리지 서비스인 Cloudinary를 연동
- **AVIF 포맷 자동 변환:** 업로드되는 모든 이미지는 높은 압축률과 품질을 보장하는 **AVIF 포맷**으로 자동 변환되어 저장
- **이미지 최적화:** Cloudinary 설정을 통해 시각적 품질을 유지하면서 파일 크기 최적화
- **이미지 관리 API:** 관리자가 업로드된 이미지 리스트를 조회하고, 특정 이미지를 삭제할 수 있는 관리 기능을 제공

### 5. 공통 예외 처리 (Global Exception Handling)

애플리케이션 전역에서 발생하는 예외를 일관된 형식으로 처리하여 클라이언트에게 명확한 에러 정보를 제공

#### 일관된 에러 응답 형식 (`ErrorResponse`)

모든 에러 응답은 아래와 같은 정형화된 JSON 구조를 가진다

- `message`: 사용자 친화적인 에러 메시지
- `statusCode`: HTTP 상태 코드
- `validation`: 입력값 검증 실패 시, 필드별 에러 상세 정보 (Map 형식)

#### 계층적 예외 구조

- **`CustomException` / `BusinessException`:** 서비스 로직 중 발생하는 비즈니스 예외를 처리하기 위한 최상위 클래스
- **세부 예외 클래스:** `NotFoundException`, `AuthException`, `ExternalServiceException` 등 상황에 맞는 구체적인 예외 클래스 구현

#### 전역 예외 처리기 (`GlobalExceptionHandler`)

- **비즈니스 예외 처리:** `CustomException` 발생 시 해당 예외에 정의된 상태 코드와 메시지를 응답
- **데이터 검증 에러 처리:** `@Valid` 검증 실패(`MethodArgumentNotValidException`) 시, 어떤 필드에서 어떤 문제가 발생했는지 `validation` 맵에 담아 400 에러를 반환
- **파라미터 누락 처리:** 필수 요청 파라미터가 없을 경우 명확한 안내와 함께 400 에러를 반환
- **데이터 무결성 처리:** DB 제약 조건 위반(중복 데이터 등) 시 409 Conflict 에러를 반환

### 6. 컨테이너 배포 (Docker)

프로젝트는 컨테이너 기반의 유연한 배포를 위해 Docker 환경을 지원

#### .dockerignore 설정 및 최적화 전략

- `.dockerignore` 파일을 통해 Dockerfile 빌드 시 특정 파일 제외 및 예상치 못한 레이어 캐시 무효화 방지
- **빌드 속도 향상:** `.git`, `.gradle`, `build/` 등 대용량 디렉토리를 제외하여 Docker 데몬으로 전송되는 컨텍스트 크기를 줄이고 빌드 속도 최적화
- **캐시 효율 향상:** 소스 코드와 무관한 파일(로그, 로컬 빌드 결과물)의 변경이 Docker Layer 캐시를 무효화시키는 것을 방지하여 재빌드 시간을 단축

#### Dockerfile

**Multi-Stage Build** 전략을 사용하여 빌드 이미지 용량을 최소화, 캐시 사용률을 높임

- **Build Stage:** `eclipse-temurin:17-jdk` 베이스 이미지를 사용하여 Gradle 빌드를 수행하며, Docker BuildKit의 캐시 기능을 활용(`--mount=type=cache`)하여 의존성 다운로드 시간을 단축
- **Run Stage:** 실행에 필요한 JAR 파일만 포함하여 경량화된 런타임 이미지를 생성

#### docker-compose.yml

애플리케이션과 필요한 환경 변수를 정의, 서비스 단위로 관리

- **Service:** `blog` 컨테이너가 9000번대 포트로 호스트에 매핑
- **환경 변수:** DB 접속 정보, JWT 설정, Cloudinary 키, MongoDB 설정 등을 환경 변수로 주입받음
- **Timezone:** `Asia/Seoul`로 타임존을 설정하여 로그 및 데이터 시간을 동기화
- **Networks:** 외부 네트워크를 사용하여 다른 컨테이너와의 연결을 지원

### 7. 배포 자동화 (CI/CD) 및 무중단 배포 전략 적용

GitHub Actions를 활용하여 `Deploy` 계열 브랜치에 변경 사항이 생길 시 자동으로 빌드 및 배포를 수행

#### 배포 자동화 프로세스

- **Self-hosted Runner:** 온프레미스 서버에서 직접 빌드 및 배포를 수행하여 배포 속도를 높이고 서버 자원을 효율적으로 사용
- **Secrets 유효성 검증:** 배포 전 DB 정보, JWT 설정, Cloudinary 키, MongoDB 연결 정보 등 필수적인 환경 변수가 올바르게 설정되었는지 자동으로 검사하여 배포 안정성을 확보
- **네트워크 자동 구성:** docker network를 자동으로 생성 및 관리하며, 외부 서비스(MySQL, Nginx 등)와의 컨테이너 연결 설정

#### 워크플로우 주요 단계

1. **Source Checkout:** 최신 코드를 배포 서버로  가져옴
2. **Secrets Validation:** 배포에 필요한 secret 키 설정 여부 사전 확인
3. **Container Management:** 기존 실행 중인 컨테이너를 제거하고 네트워크 환경을 점검
4. **Build & Deploy:** `docker compose up -d --build` 명령을 통해 애플리케이션을 자동으로 빌드하고 배포

#### Blue-Green Deployment

Blue-Green 배포 전략을 기반으로 무중단 배포 환경을 구축

##### 주요 특징 및 프로세스

- **격리된 배포 환경:** `deploy/green`, `deploy/blue` 브랜치 등을 통해 버전별로 독립된 배포 파이프라인을 운영
- **Docker Compose 프로젝트 분리:** `docker compose --project-name blog-green`과 같이 프로젝트 이름을 명시하여, 운영 중인 서비스(Blue)와 간섭 없이 새로운 버전(Green)을 컨테이너 배포 가능

### 8. 테스트 및 문서화 (Test & Docs)

안정적인 서비스 운영을 위해 Spring Boot Test와 RestDocs를 활용한 통합 테스트 환경을 구축

#### 테스트 설정 (`src/test/java`)

- **Base Test Class (`ApiDocTestBase`):**
  - `@SpringBootTest`, `@AutoConfigureMockMvc` 등을 적용하여 통합 테스트 환경을 제공
  - 추상클래스를 활용, 중복 코드 제거
  - `@Transactional` 및 `@Rollback`을 적용하여 테스트 간 데이터 격리성을 보장

- **Security Mocking (`@WithMockCustomUser`):**
  - 커스텀 어노테이션을 통해 인증된 사용자(ADMIN/USER) 정보를 손쉽게 Mocking 가능

- **Data Initialization (`DataInitComponent`):**
  - 테스트 실행 전, 필요한 기초 데이터를 초기화하는 컴포넌트
  - 데이터 간의 연관 관계를 자동으로 설정하여 현실적인 테스트 데이터를 제공

#### API 문서화 (Spring REST Docs)

- **테스트 주도 문서화:**
  - 모든 API 문서는 성공적인 테스트 통과를 전제로 생성
- **Snippet 자동 생성:** 요청/응답 필드, 쿠키, 경로 변수 등에 대한 문서 조각(Snippet)을 자동으로 생성
- **문서화 흐름:** `Test 실행` -> `Snippet 생성` -> `Asciidoctor 변환` -> `HTML 문서 생성` 

