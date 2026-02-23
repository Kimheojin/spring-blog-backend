### postCount 추가

#### 진행 순서

- green 배포로 처리하기 (현재 blue 운영중)
  - 사실 postCount 칼럼만 추가하는 방식
- q객체 먼저 생성하고 하는 게 편할거 같음
- 정합성 스케쥴러로 돌리는 식으로 
- 1차로 두개 기능만 추가하기
  - 그럼 될거 같은데

### sql alter 문


```
-- 1. 컬럼 추가
ALTER TABLE category ADD COLUMN post_count INT DEFAULT 0;

-- 2. 기존 데이터 업데이트 (1회성)
UPDATE category c SET c.post_count = (
    SELECT COUNT(*) FROM post p 
    WHERE p.category_id = c.category_id AND p.status = 'PUBLISHED'
);

-- 3. 조회 테스트 쿼리 
SELECT category_id, category_name, post_count, priority 
FROM category 
ORDER BY priority ASC, category_name ASC;
```

---
이런 방향으로 하는 게 맞는 거 같

---
수정이 필요한 메서드
(완료하면 지우기)


- PostWriteService.java
- `writePost`: 새 게시글이 PUBLISHED 상태로 저장될 때, 해당 카테고리의 postCount를 증가
- `updatePost`:
  - 상태 변경 시: (예: DRAFT -> PUBLISHED 면 증가, PUBLISHED -> PRIVATE 면 감소)
  - 카테고리 변경 시: 기존 카테고리 감소, 새 카테고리 증가 (단, PUBLISHED 상태일 때만)
- `deletePost`: 삭제되는 게시글이 PUBLISHED 상태였다면, 해당 카테고리의 postCount를 감소 필요

- PostScheduler.java (예약 발행)
- `runTask5AM`: 예약된 게시글(SCHEDULED)이 PUBLISHED로 상태가 변경될 때, 해당 카테고리의 postCount를 증가 필요

- CategoryRepositoryImpl.java (QueryDSL & 동기화)
- `findAllCategoriesWithCount`: 이제 매번 Join과 Count 쿼리를 날릴 필요 없이, Category 엔티티의 postCount 필드를 바로 조회하도록 수정하여 성능을 최적화할 수 있음
- `syncPostCounts` (신규): 사용자가 언급하신 '정합성 스케줄러'에서 사용할 메서드입니다. 전체 카테고리의 실제 게시글 수와 필드 값을 일치시키는 벌크 업데이트 로직이 필요

- Category.java (엔티티)
- 이미 increasePostCount(), decreasePostCount() 메서드가 있으므로, 위 서비스 로직에서 이를 적절히 호출해주기만 하면 될듯
