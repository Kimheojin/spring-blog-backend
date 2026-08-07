# 블로그 프로젝트

- Spring Boot와 Java 기반으로 블로그 서비스를 위한 REST API 서버를 개발하는 프로젝트
- 게시글, 댓글, 회원 인증을 비롯해 이미지 업로드, 검색 및 데이터 동기화 등 백엔드 전반의 기능을 설계하고 구현
- 인증 보안, 성능 최적화, 검색 품질, 테스트 및 배포 자동화와 유지보수성을 고려하여 개발


### ✅ 사용 기술

**Backend**
Java 17, Spring Boot 3.4.4, Spring Security, JJWT, Spring Data JPA, QueryDSL

**Database / Search**
MySQL, MongoDB Atlas Search

**Infra / CI·CD**
Docker, Docker Compose, GitHub Actions

**External Service**
Cloudinary

**Test / Documentation**
JUnit 5, Mockito, H2, Embedded Mongo, Spring Rest Docs, Asciidoctor

### ✅ Architecture

![Architecture](doc/image/blog-archi.png)


### ✅ 배포 환경 (스펙)

- 개인 서버 스펙

### ✅ 주요 기능

▶ 게시글

1. 게시글 작성 / 수정 / 삭제 / 조회
2. 카테고리 및 태그 기반 게시글 분류
3. 마크다운 본문 처리
4. 검색 인덱싱을 위한 Plain Text 변환

▶ 댓글

1. 댓글 작성 / 수정 / 삭제
2. 게시글 기준 댓글 조회
3. 계층형 댓글 구조 관리

▶ 회원 및 인증

1. 로그인 / 로그아웃
2. JWT 기반 인증 처리
3. HttpOnly Cookie 기반 토큰 전달
4. 역할 기반 접근 제어
5. Stateless Security 구조 적용

▶ 이미지

1. Cloudinary 기반 이미지 업로드
2. AVIF 포맷 변환
3. 이미지 품질 자동 최적화
4. 온프레미스 서버의 미디어 저장 부담 최소화

▶ 검색 및 SEO

1. MongoDB Atlas Search 기반 검색
2. 한국어 검색 품질 개선을 위한 Nori 형태소 분석기 활용
3. MySQL 원본 데이터와 MongoDB 검색 데이터 동기화
4. SHA-256 해시 비교를 통한 변경 데이터 선별 업데이트
5. 스케줄러 기반 자동 동기화

▶ API 문서

1. Spring Rest Docs 기반 API 문서 생성
2. Controller 테스트와 문서화를 함께 수행
3. Asciidoctor를 통한 정적 HTML 문서 변환
4. 빌드 시 `/docs/index.html` 경로로 API 문서 제공



### ✅ 프로젝트를 진행하며 고민한 Technical Issue

* Self-hosted Runner를 활용한 온프레미스 서버 배포 자동화
* GitHub Actions와 Docker Compose를 이용한 배포 파이프라인 구성
* Blue / Green 배포 전략을 고려한 브랜치별 배포 구조 설계
* Docker Multi-stage Build를 통한 이미지 크기 최적화
* BuildKit 캐시 마운트를 활용한 Docker 빌드 시간 단축
* JWT를 HttpOnly Cookie로 전달하여 XSS 위험을 줄이는 인증 구조 구현
* Stateless 기반 Spring Security 설정과 역할 기반 접근 제어 구성
* Cloudinary API를 활용한 AVIF 이미지 변환 및 용량 최적화
* MySQL N-gram 검색의 한계를 보완하기 위한 MongoDB Atlas Search 도입
* SHA-256 해시 기반 동기화로 불필요한 검색 인덱스 업데이트 최소화
* H2와 Embedded Mongo를 사용한 외부 인프라 의존 없는 테스트 환경 구성
* MockMvc와 Spring Rest Docs를 결합하여 API 검증과 문서화를 동시에 처리
* Asciidoctor를 빌드 프로세스에 포함하여 문서와 실제 API 간 불일치 방지



### ✅ 구현 결과

* Docker 배포 시간 72% 단축
  * 125초 → 35초

* Docker 이미지 크기 20% 개선
  * 420MB → 333MB

* AVIF 변환을 통한 이미지 용량 85.4% 절감
  * 142KB → 20.7KB

* 이미지 로딩 속도 약 5배 개선
  * 132ms → 26ms



### ✅ 프로젝트 구조

```text
src/main/java/HeoJin/demoBlog/
├── category/       # 카테고리 도메인
├── comment/        # 댓글 도메인
├── member/         # 회원 및 인증 도메인
├── post/           # 게시글 도메인
├── seo/            # 검색 인덱싱 및 데이터 동기화
├── tag/            # 태그 도메인
├── image/          # 이미지 업로드 및 Cloudinary 연동
└── global/         # 전역 설정, 예외, 보안, AOP, 유틸

### ✅ API 문서화 전략

- Controller 테스트에서 MockMvc와 Spring Rest Docs를 함께 사용
- 테스트 실행 시 API 명세 스니펫 자동 생성
- Asciidoctor를 통해 HTML 문서로 변환
- 빌드 결과물에 API 문서를 포함하여 서버에서 직접 확인 가능

/docs/index.html

### ✅ 테스트 전략

- Controller 테스트
    - MockMvc 기반 API 요청 / 응답 검증
    - Spring Rest Docs 기반 문서화

- Service 테스트
    - Mockito 기반 비즈니스 로직 검증
    - 외부 의존성을 분리한 단위 테스트

- Repository 테스트
    - @DataJpaTest 기반 JPA 쿼리 검증
    - H2를 활용한 독립적인 테스트 환경 구성

- 검색 동기화 테스트
    - Embedded Mongo를 활용하여 외부 MongoDB 의존 제거