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

