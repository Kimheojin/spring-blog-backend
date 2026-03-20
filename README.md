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

![GitHub Actions 러너 상태](https://res.cloudinary.com/dtrxriyea/image/upload/v1773996575/etc/prwxu1udfsbabdcnaiiz.avif)

##### 배포 자동화 구성

환경 변수 사전 검증 (Fail-Fast)

```yaml
- name: Validate secrets
  run: |
    if [ -z "${{ secrets.DB_NAME }}" ] || [ -z "${{ secrets.DB_PASSWORD }}" ]; then
      echo "Required database secrets are missing"
      exit 1
    fi
```
동적 네트워크 관리 및 연결

```yaml
- name: Setup network
  run: |
    docker network inspect blog-network >/dev/null 2>&1 || docker network create blog-network
    docker network connect blog-network mysql-container || true
```

프로젝트 기반 격리 배포 (Blue/Green 배포 관련)

```yaml
- name: Deploy with Docker Compose
  run: |
    docker compose --project-name blog-blue up -d --build
``` 

#### 

##### 

### Docker + Docker compose 구성 



## 로그인 기능

### 로그인 구현 도식도
## 게시글 


## SEO 구현 

## 테스트 코드 전략


## 기타


