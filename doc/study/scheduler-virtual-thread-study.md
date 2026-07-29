# Scheduler Virtual Thread Study

- 작성일시: 2026-07-29 16:41:59 KST
- 대상 디렉터리: `src/main/java/HeoJin/demoBlog/global/scheduler`

## 현재 스케줄러 구성

- `seo/MongoScheduler`
  - 매일 04:00에 `SyncService.triggerSync()` 실행
  - MySQL 원본 데이터와 MongoDB 검색 데이터를 동기화하는 작업
  - DB와 MongoDB I/O 대기 시간이 발생할 수 있는 blocking 작업 성격

- `post/PostScheduler`
  - 매일 05:00에 예약 게시글 발행 처리
  - `PostStatus.SCHEDULED`이면서 `regDate`가 현재 시각보다 이전인 게시글을 조회한 뒤 `PUBLISHED`로 변경
  - 변경된 게시글의 카테고리 ID를 모아 `CategoryRepository.syncPostCounts()` 실행
  - `Clock` bean을 주입해 테스트에서 실행 기준 시각을 고정할 수 있음
  - `PostRepository.findByStatusAndRegDateBefore()`로 조회 조건을 DB에 위임함

- `post/PostCountScheduler`
  - 매일 06:00에 전체 카테고리의 게시글 수 정합성 보정
  - `categoryRepository.findAll()`로 전체 카테고리를 조회하고 각 카테고리마다 `syncPostCounts()` 실행
  - 카테고리 수가 많아지면 반복 쿼리 비용과 트랜잭션 시간이 늘어날 수 있음

## 현재 Java 17 방식

- 현재 프로젝트는 Gradle toolchain, Dockerfile, GitHub Actions 테스트 워크플로가 모두 Java 17 기준이다.
- Java의 정식 virtual thread는 Java 21에서 사용할 수 있으므로, Java 17 유지 상태에서는 스케줄러에 virtual thread를 직접 적용할 수 없다.
- Spring `@Scheduled`는 별도 설정이 없으면 Spring의 기본 스케줄링 실행기를 사용한다.
- 현재 스케줄러 작업은 대부분 DB 또는 MongoDB 호출을 포함하므로 CPU 연산보다 blocking I/O 대기가 주요 비용이 될 가능성이 높다.
- 플랫폼 스레드 기반 실행에서는 blocking I/O 동안 해당 스레드가 점유된다.
- 다만 현재 스케줄러 실행 시간이 04:00, 05:00, 06:00으로 분리되어 있어 동시에 많은 스케줄러가 몰리는 구조는 아니다.

## Java 21 업그레이드 후 Virtual Thread 사용 이점

- blocking I/O 대기 중 플랫폼 스레드 점유를 줄일 수 있다.
- DB, MongoDB, 외부 API 호출처럼 대기 시간이 긴 작업을 실행할 때 스레드 운용 비용이 낮아진다.
- 향후 스케줄러 작업이 늘어나거나 일부 작업을 병렬화할 때 플랫폼 스레드 풀 고갈 위험을 줄일 수 있다.
- Spring Boot 3.4.x는 Java 21 환경에서 운영할 수 있으므로 LTS 기준 업그레이드 경로로 적합하다.
- 스케줄러 실행기를 virtual thread 기반으로 분리하면 웹 요청 처리 스레드와 배치성 작업 실행 정책을 더 명확히 나눌 수 있다.
- 작업 단위가 많고 각각이 blocking I/O 중심일수록 virtual thread의 이점이 커진다.

## Virtual Thread 적용 시 한계와 주의점

- virtual thread는 쿼리 자체를 빠르게 만들지 않는다.
- DB 커넥션 풀 크기보다 많은 DB 작업을 동시에 실행하면 실제 처리량은 커넥션 풀이 제한한다.
- MongoDB 커넥션 풀, MySQL 커넥션 풀, 트랜잭션 범위가 병목이면 virtual thread만으로 성능 문제가 해결되지 않는다.
- `PostScheduler`의 OOM 위험은 virtual thread와 별개의 문제다.
- `findByStatusAndRegDateBefore(PostStatus.SCHEDULED, now)`로 발행 대상 조건은 DB 조회에 포함했지만, 예약 발행 대상 자체가 매우 많아지는 경우에는 여전히 페이징 또는 벌크 업데이트를 별도로 검토해야 한다.
- JPA 영속성 컨텍스트에 많은 엔티티가 쌓이는 문제도 virtual thread로 해결되지 않는다.
- Java 21 전환 시 Gradle toolchain, Dockerfile의 build/runtime 이미지, GitHub Actions의 JDK 버전을 함께 변경해야 한다.
- 운영 서버의 Java 런타임, 배포 스크립트, 모니터링 기준도 Java 21 기준으로 맞춰야 한다.

## 리팩토링 반영 내용

- 작성일시: 2026-07-29 17:16:00 KST
- `PostScheduler.runTask5AM()`을 `publishScheduledPosts()`로 변경해 실행 시각보다 작업 목적이 드러나도록 정리했다.
- 예약 발행 대상 조회를 `findByStatus(PostStatus.SCHEDULED)` 전체 조회에서 `findByStatusAndRegDateBefore(PostStatus.SCHEDULED, now)` 조건 조회로 변경했다.
- 기존 `regDate.isBefore(now)` 정책을 유지하기 위해 정확히 같은 시각의 예약글은 발행 대상에서 제외한다.
- 예약 발행 시 필요한 카테고리 접근 비용을 줄이기 위해 Repository 메서드에 category `EntityGraph`를 적용했다.
- `LocalDateTime.now()` 직접 호출을 `Clock` 주입 방식으로 바꿔 scheduler 단위 테스트에서 기준 시각을 고정할 수 있게 했다.
- `PostCountScheduler.runTask6AM()`은 `syncAllCategoryPostCounts()`로, `MongoScheduler.runTaskAt4AM()`은 `syncMongoSearchIndex()`로 변경했다.
- 각 scheduler의 cron 문자열은 클래스 내부 상수로 분리해 메서드 어노테이션의 매직 스트링을 줄였다.
- `doc/spec/scheduler.md`에 현재 스케줄러 동작 기준을 별도로 정리했다.
- 검증은 scheduler 단위 테스트, 예약 발행 대상 Repository 테스트, 전체 테스트로 수행했다.

## 권장 방향

- 단기적으로 Java 17을 유지한다면 virtual thread 적용보다 scheduler 작업의 조회 범위와 트랜잭션 범위를 먼저 줄이는 것이 우선이다.
- `PostScheduler`의 1차 조회 범위 축소는 반영되었으므로, 다음 개선은 발행 대상이 매우 많을 때의 페이징 처리 또는 bulk update 검토가 적합하다.
- 예약 게시글 수가 많을 수 있다면 페이징 처리 또는 bulk update를 검토한다.
- 카테고리 카운트 동기화는 변경된 카테고리만 수행하는 현재 방향을 유지하되, 반복 쿼리 비용이 커지면 카테고리별 count 집계 방식을 별도로 개선한다.
- 중기적으로 Java 21 전환이 가능해지면 scheduler 전용 `TaskScheduler` 또는 `SchedulingConfigurer`를 구성해 virtual thread 기반 실행기로 전환한다.
- virtual thread 전환 후에도 DB 커넥션 풀 크기, 스케줄러 실행 시간, 처리 건수, 실패 로그를 함께 관찰해야 한다.

## 결론

- 현재 Java 17 유지 조건에서는 `PostScheduler`를 virtual thread로 실행하는 것은 정식 적용 대상이 아니다.
- Java 21로 업그레이드하면 scheduler의 blocking I/O 대기 비용을 줄이고 향후 작업 확장성을 높일 수 있다.
- 하지만 현재 코드에서 가장 직접적인 위험은 virtual thread 부재가 아니라 scheduler 작업의 조회 범위, 처리 건수, 트랜잭션 범위다.
- 따라서 우선순위는 scheduler 조회/처리 방식 개선, 이후 Java 21 전환과 virtual thread scheduler 적용 검토 순서가 적합하다.
