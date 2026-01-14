# 부하 테스트 + 최적화 관련

## 부하테스트 전체 구조 

![부하 테스트 구조](image/testarchi.png)

## 부하 테스트 환경 설정 관련

- 자원 제약 환경에서의 안정성 검증 위해
    - 커널 수준에서의 리소스 제한 기능 활용
  
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

## Prometheus, Grafana 부하테스트 모니터링 환경


