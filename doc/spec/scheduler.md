# Scheduler Spec

- 작성일시: 2026-07-29 17:12:55 KST
- 대상 디렉터리: `src/main/java/HeoJin/demoBlog/global/scheduler`
- 예약 게시글 발행 스케줄러는 매일 05:00에 실행한다.
- 예약 게시글 발행 대상은 `PostStatus.SCHEDULED`이고 `regDate`가 실행 기준 시각보다 이전인 게시글이다.
- `regDate`가 실행 기준 시각과 같은 게시글은 기존 `isBefore` 정책과 동일하게 발행 대상에서 제외한다.
- 발행 대상 조회는 `PostRepository.findByStatusAndRegDateBefore()`에서 DB 조건으로 필터링한다.
- 발행된 게시글은 `PostStatus.PUBLISHED`로 변경하고 `regDate`를 실행 기준 시각으로 갱신한다.
- 발행된 게시글의 카테고리 ID를 중복 제거한 뒤 `CategoryRepository.syncPostCounts()`를 호출한다.
- 예약 발행 기준 시간은 `Clock` bean을 통해 주입해 테스트에서 고정할 수 있게 한다.
- 카테고리 게시글 수 정합성 스케줄러는 매일 06:00에 실행하며 전체 카테고리에 대해 `syncPostCounts()`를 호출한다.
- Mongo 검색 인덱스 동기화 스케줄러는 매일 04:00에 실행하며 `SyncService.triggerSync()`를 호출한다.
