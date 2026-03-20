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

- docker 관련 
- 
## 로그인 기능

### 로그인 구현 도식도

### 구현 목표

### 구현 내용


## 게시글 

### 구현 목표
### 구현 내용

## SEO 구현

### 구현 목표

-

### 구현 목표

- 



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


