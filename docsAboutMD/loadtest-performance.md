# 부하 테스트 + 최적화 관련

## 부하테스트 전체 구조 

![부하 테스트 구조](image/testarchi.png)

## 부하 테스트 환경 설정 관련

커널 수준 리소스 제한 활용 (docker cgroup)
  
### cgroup 설정 관련

| **컨테이너**        | **역할**    | **CPU Limit (상한)** | **Memory Limit (상한)** | **비고**             |
| ------------------- | ----------- | ------- | ------------ | -------------------- |
| **blog-loadtest**   | Java 앱     | 1.25    | 2GB          | 메인 테스트 대상     |
| **mysql-container** | DB          | 1.25    | 2GB          | 쿼리 및 I/O 처리     |
| **prometheus**      | 지표 수집   | 1.0     | 1.5GB        | 수집 누락 방지       |
| **grafana**         | 시각화      | 0.5     | 1GB          | 원활한 그래프 렌더링 |
| **my_nginx_server** | 프록시      | 0.4     | 512MB        | 트래픽 중계          |
| **exporter 계열**   | 메트릭 노출 | 0.2     | 128MB        | 지표 데이터 제공     |

#### 제한 확인 체크

![Docker Stats](image/aboutDockerStats.png)


## 더미 데이터 관련

### 더미 데이터 주입 시 사용한 Spring batch 코드

[Spring Batch git hub 코드 링크](https://github.com/Kimheojin/spring-batch-preprocessing?tab=readme-ov-file#2-dummydatajob-%EB%B6%80%ED%95%98-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EB%8C%80%EB%B9%84-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%A0%81%EC%9E%AC)

### 더미데이터 주요 테이블 row 갯수 

| post_cnt | post_tag_cnt | tag_cnt | category_cnt |
| :--- | :--- | :--- | :--- |
| 3376000 | 5064604 | 50 | 100 |

![더미데이터쿼리결과](image/dummyCount.png)

## 부하 테스트 및 주요 수정 사항

- nginx keep alive (nginx <-> Srping)을 통한 서버 부하 감소
- 서버 spec 대비 과도한 데이터 부하 환경 시 역 정규화를 통한 최적화
- connection 갯수 조정

### 



---
## 기타 링크

- [리드미로 이동](../README.md) 
- [부하테스트 관련 링크](loadtest-performance.md)
