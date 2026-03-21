# 블로그 API SERVER

- Spring + 온프레미스 서버를 통한 배포 환경 구성
- 서비스 url : [heojin.vercel.app](https://heojin.vercel.app/)
  - 현재 post 업데이트 중단

## 프로젝트 배포 구조 요약

![image](https://res.cloudinary.com/dtrxriyea/image/upload/v1773992077/etc/bbhsvdjwmvwlf5elre7x.avif)

## 배포 관련

### GitHub Actions 관련

#### 구현 목표

- 배포 시 반복되는 `rsync` 명령어를 통한 코드 이동, `docker` 관련 명령어 등 제거 (배포 자동화)
- 브랜치 별 배포 전략 차별화 목적 

#### 주요 구현 내용

##### Self-hosted Runner를 통한 온프레미스 서버 연결 + 배포

![Github 이미지](https://res.cloudinary.com/dtrxriyea/image/upload/v1773994931/etc/nekd5kjpuwsgcxfjbga4.avif)

- Actions 로그 연결 확인

```text
Current runner version: '2.331.0'
Runner name: 'jin'
Runner group name: 'Default'
Machine name: 'jin'
GITHUB_TOKEN Permissions
  Contents: read
  Metadata: read
  Packages: read
Secret source: Actions
```


##### 배포 자동화 구성

- 환경 변수 사전 검증

```yaml
- name: Validate secrets
  run: |
    if [ -z "${{ secrets.DB_NAME }}" ] || [ -z "${{ secrets.DB_PASSWORD }}" ]; then
      echo "Required database secrets are missing"
      exit 1
    fi
```
- 동적 네트워크 관리 및 연결

```yaml
- name: Setup network
  run: |
    docker network inspect blog-network >/dev/null 2>&1 || docker network create blog-network
    docker network connect blog-network mysql-container || true
```

- 프로젝트 기반 격리 배포 (Blue/Green 배포)

```yaml
# 브랜치 지정
on:
  push:
    branches:
      - deploy/blue
  pull_request:
    branches:
      - deploy/blue
# Docker compose 프로젝트 기반 구분 
- name: Deploy with Docker Compose
  run: |
    docker compose --project-name blog-blue up -d --build
``` 

### DockerFile + Docker compose 구성 

#### 구현 목표

- 이미지 빌드 시간 및 크기 최적화 
- 환경 변수 및 Docker 네트워크 통합 관리

#### 구현 내용

- Multi-stage 빌드를 통한 이미지 크기 최적화

```dockerfile
# 빌드 스테이지
FROM eclipse-temurin:17-jdk AS build
# ---
# 실행 스테이지
FROM eclipse-temurin:17-jre
```

- BuildKit 캐시 마운트를 활용한 이미지 빌드 속도 최적화

```dockerfile
COPY src src
# 캐시 마운트를 이용한 빌드
RUN --mount=type=cache,target=/root/.gradle ./gradlew build -x test -x asciidoctor
```

- docker-compose를 통한 구성 관리

```yaml
services:
  blog:
    build: . # 로컬 빌드 및 이미지 생성
    ports: ["9000:9000"] # 포트 포워딩 설정
    environment: # DB, JWT, Cloudinary 등 .env 변수 및 KST 타임존 설정
    networks: ["blog-network", "my-network"] # docker 네트워크 연동
    volumes: ["/etc/localtime:/etc/localtime:ro"] # 호스트 시간 동기화
```

### 구현 결과

- Docker 배포 시간 72% 단축 (125s -> 35s)
- Docker 이미지 크기 20% 개선 (420MB -> 333MB)

## 로그인 기능

- `Spring Security`, `jjwt 0.13.0` 라이브러리 사용
- `jjwt` 사용 이유
  - Oauth2 사용 안하는 구조에서 제일 가볍고, 의존성이 적어 기능 확장시 이점

### 로그인 구현 요약 도식도 

![로그인 구현 도식도](https://res.cloudinary.com/dtrxriyea/image/upload/v1774080418/etc/am3idkgiipu0kyoxpuxr.avif)

### 구현 목표

- 역할 기반 관리
- 보안성 강화
- 쿠키 + jwt 구조 확립

### 주요 구현 내용

- 쿠키 정책 적용

```java
return ResponseCookie.from("accessToken", accessToken)
    .httpOnly(true)    // 자바스크립트를 통한 쿠키 접근 차단 (XSS 방지)
    .secure(true)      // HTTPS 통신 환경에서만 쿠키 전송
    .sameSite("None")  // CSR 구조라 강제
    .maxAge(60 * 60 * 24).build();
```

- Stateless 기반의 무상태 인증 구조

```java
httpSecurity
    .csrf(AbstractHttpConfigurer::disable) // 무상태 구조이므로 CSRF 보호 비활성화
    .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 미사용 명시
    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
```

- 역할 기반 권한 분리 및 접근 제어

```java
@EnableMethodSecurity(prePostEnabled = true) // 메서드 단위 보안 어노테이션 활성화
public class SecurityConfig {
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/auth/login", "/api/categories").permitAll() // 비인증 허용
        .anyRequest().authenticated()) // 그 외 모든 요청은 인증 필수
}
```

- 프로젝트 공통 예외 로직 추가

```java
.exceptionHandling(ex -> ex
    .authenticationEntryPoint((request, response, authException) ->
        handleSecurityException(response, objectMapper, "인증이 필요합니다.",
            HttpServletResponse.SC_UNAUTHORIZED, "AUTHENTICATION_REQUIRED"))
    .accessDeniedHandler(...)); // 권한 부족 시 커스텀 403 응답 핸들링
```

## 이미지 관련

### 구현 목표

- 온프레미스 서버 자원 사용을 최소화 하기 위해 외부 미디어 서버 (Cloudinary) 활용
- `AVIF`이미지 타입 도입, 동일 화질 대비 압축률이 가장 높음
  - 변환로직의 경우 로컬에서 하는 것을 고려했으나, c 관련 종속성 추가로 인한 Cloudinary API 사용

### 구현 내용 

- Cloudinary SDK 활용

```java
Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
        ObjectUtils.asMap("folder", "blog/posts"));
String imageUrl = (String) uploadResult.get("secure_url");
```
- AVIF 포맷 변환 및 최적화

```java
String optimizedUrl = cloudinary.url()
    .transformation(new Transformation().fetchFormat("avif").quality("auto"))
    .generate(publicId);
```

### 구현 결과 

- PNG 이미지 대비 용량 85.4% 절감 (142 KB -> 20.7 KB)
- 로딩 속도 5배 개선 (132ms -> 26ms)
## SEO 구현

### 구현 목표

- MySQL FullText 사용 시 한글 전용 분석기 부재 + MySQL 서버 부하 예쌍
  - Mongo Atlas Search 를 통한 SEO 도입
- 그에 따른 MongoDB <-> MySQL 간 동기화 로직 필요

### 구현 내용

- Apache Nori 한글 전용 분석기를 통한 인덱스 구축 (토큰 화) 
```yaml
  "analyzer": "lucene.nori",
  "searchAnalyzer": "lucene.nori"
```

- `SHA-256` 기반 Mongo <-> MySQL DB 간 동기화 로직 구축
```java
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(content.getBytes());
        return HexFormat.of().formatHex(hash);
    }
```

- 스케쥴러를 통한 동기화 로직 구축

```java
    @Scheduled(cron = "0 0 4 * * ?")
    public void runTaskAt4AM(){
        syncService.triggerSync();
    }
```

## 테스트 코드 전략

### 구현 목표

- 테스트 작성 비용 최소화
- 계층 간 테스트 코드 분리

### 구현 내용

- 

## asscidoc 관련 구현

### 구현 내용
### 구현 결과

## 기타


