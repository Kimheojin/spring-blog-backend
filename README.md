# 부하 테스트

- 좀 걸릴듯


### 할꺼 정리

- Docker 네트워크 새로 파기 (완)
```
 load-test-net
```
- container 명, compose project 명 변경 (완)

- 서버 포트 변경하기
```
9003 변경 완
```
- nginx 엔드포인트 새로파기
```
프록시 완
```
- JFR은 필요할때만 (완)
```
베이스 이미지는 변경 해놈
```
- 프로메테우스 , 그라파나 띄우기
  - 공유기 포트포워딩 확인하기
  - 5123
- 이거 중요할수도
 - mysql 부하 테스트 user 로 변경
   - db 도 변경
   - docker compsoe 에서 매핑한듯

- docker compseo 매핑한 시크릿 값들 application.yml 새로 파서 매핑하기

- mysql 라이브 DB 랑 스키마 일치하는 지 확인하기

- 

- JFR 켜기
  - docker file 작성 단계 에서 확인하기

- container 띄우기 전에 apring actuator 랑 프로메테우스 관련 설정 확인하기

- mysql 슬로우 쿼리 켜져있는 지 확인
  - persist 가능 변수면 박기
  - 1초

---
- 그라파나 5123 으로 열기




### 단독 테스트
./gradlew test --tests "*SyncControllerDocTest" --info > test-log.txt
 -> 이런식으로 하기

```aiexclude
rsync -avzP -e "ssh -p 1225" spring-blog-backend/ jin@heojineee.ddnsking.com:~/blog-test-server/
```

```aiexclude
rsync -avzP -e "ssh -i ~/.ssh/id_ed25519_server -p 1225" \
spring-blog-backend \
jin@heojineee.ddnsking.com:~/blog-test-server/
```


