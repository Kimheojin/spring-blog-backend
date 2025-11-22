package HeoJin.demoBlog.seo.service;

public class TestConstants {

    public static final String LONG_STRING = """
            ## 개요
            MySQL 서버에 있는 Spring Batch 메타 데이터 스키마 테이블을 삭제 하려던 차, 블로그에 개념 정도 한번 정리하면 어떨까 해 글을 적성하게 되었다.

            ## 메타 데이터 스키마 테이블 역할
            주관적으로 봤을 때 메타 데이터 스키마를 사용할 많은 장점을 가진다.
            - 분산 환경에서의 잡 관리 효율성
              - 여러 서버 인스턴스가 단일 메타데이터 저장소를 공유하여, 어떤 서버에서 잡이 실행되더라도 상태와 이력 데이터의 일관성을 확보
            - 잡 인스턴스(JobInstance) + 파라미터의 유일성 보장
              - Spring Batch는 **Job 이름**과 **JobParameter**를 조합하여 `JobInstance`를 식별한다.
              - 메타데이터 테이블은 이 `JobInstance`의 실행 이력을 관리하여, 동일한 파라미터로는 잡이 단 한 번만 실행되도록 보장
            - 가로 확장(Scale-Out)의 용이성
              - 메타데이터를 DB 등 중앙 저장소에서 관리하므로, 애플리케이션 서버가 몇 대로 늘어나도 각 서버는 동일한 잡의 상태를 일관되게 조회하고 갱신
              - 이는 복잡한 동기화 처리 없이 시스템 확장을 가능하게 한다.

            ## SEQ 테이블 관련 내용
            ### Spring Batch 공식 문서에서의 스키마 테이블 구조
            ![](https://res.cloudinary.com/dtrxriyea/image/upload/v1760605158/blog-27/eckqksa0iv4qcdjdoasi.avif)

            Spring Batch 에서 설명하는 메타 테이블 구조를 보며 `*_SEQ` 테이블에 대한 모습이 보이지는 않는데, 실제로 생성해 보면
            ![](https://res.cloudinary.com/dtrxriyea/image/upload/v1760605179/blog-27/tiqz5ozofrgzmq1w8mpz.avif)

            `*_SEQ` 테이블에 대한 내용을 찾아볼 수 있다.
            이렇게 `*_SEQ`  테이블이 존재하는 이유를 간단히 흐름대로 정리해보면
            - Spring Batch의 경우 내부 데이터 관리를 위해 대리키를 사용하기로 결정
            - 그런데, 특정 데이터베이스의 `AUTO_INCREMENT` 같은 기능에 종속되지 않고 모든 DB 에서 일관되게 유지 하려고 한 것 같다.
              -  `AUTO_INCREMENT` 의 경우 공식 표준이 존재하는 형태는 아니라 DB 마다 다르게 동작하므로
            - 위 이유 때문에 표준방식으로 SEQ를 통한 ID 생성을 채택
              - 문제는 SEQ 방식을 지원하지 않는 DB 도 존재 (예를 들어 MySQL)
            - 따라서 위와 같은 이유 및 시퀀스 방식 유지를 위해 `SEQ` 테이블을 만들어 우회 하는 방식을 채택
            간단히 한줄로 정리하면, **DB 종류에 종속적이지 않는 구조를 만들기 위해 `*_SEQ` 테이블이 존재**한다.

            ## 테이블 별 역할 정리
            이해하기 쉽게 실제 사용했던 테이블을 예시로 글을 작성하겠다.
            ### BATCH_JOB_INSTANCE 테이블
            한줄로 요약하면 **어떤 잡**을 실행했나? (Job + Parameters) 라는 내용을 담고 있다.
            ![](https://res.cloudinary.com/dtrxriyea/image/upload/v1760605189/blog-27/xm60tdh9km4albjltcvt.avif)

            - JOB_INSTANCE_ID
              - 역할 : 기본키 역할을 한다
              - 각 Job 인스턴스를 구분하는 고유 식별자 역할을 한다.
              - BATCH_JOB_EXECUTION 같은 다른 테이블에서 특정 Job 의 실행 기록을 참조
            - VERSION
              - 역할 : 낙관적 잠금에 사용되는 버전 번호라고 한다.
              - Spring Batch 프레임워크 내부에서 데이터 일관성을 유지하기 위해 사용
              - 참고사항으로 JobParameter 에 담는 version 값과는 관계가 없다.
            - JOB_NAME
              - 역할 : 실행된 Job의 이름
              - 참고사항으로 파라미터 값이 포함된 개념은 X
            - JOB_KEY
              - 역할 : Job을 식별하는 파라미터를 기반으로 기반횐 해시 값
              - JobParameter 들을 조합하여 고유한 MD% gotlzlfmf todtjd
              - JOB_NAME + JOB_KEY 의 조합을 하나의 JobInstance fmf rufwjd
              - 그림의 경우 파라미터가 동일하여 해시값이 모두 같은 모습

            ### BATCH_JOB_EXECUTION
            잡을 **언제, 어떻게 실행**했고 결과에 대한 기록을 담고 있다. (시도 기록)
            ![](https://res.cloudinary.com/dtrxriyea/image/upload/v1760605199/blog-27/mhiz94gp9s2yeao8taqo.avif)

            - JOB_EXECUTION_ID
              - 역할 : 기본키 역할을 한다
            - VERSION
              - 역할 : 프레임워크가 내부적으로 사용하는 버전 번호
            - CREATE_TIME
              - 역할 : 실행 기록이  데이터베이스에 생성된 시간
              - Job 실행을 요청하여 해당 레코드가 테이블에 처음 삽입된 시점을 나타낸다.
            - START_TIME
              - 역할 : Job 실행이 실제로 시작된 시간이다.
              - CREATE_TIME 과 약간의 차이가 있을 수 있다.
            - END_TIME
              - 역할 : Job 실행이 종료된 시간
            - STATUS
              - 역할 : Job 실행의 최종 상태를 나타낸다.
              - COMPLETED, FAILED, STARTING, STOPPED 등의 상태를 가진다.
            - EXIT_CODE
              - 역할 : Job 실행의 결과에 대한 조금 더 상세한 코드를 가진다.
              - 기본적으로 STATUS 값을 따라간다.
              - 개발자가 임의로 변경할 경우 추가 정의가 가능하다.
              - 이미지의 NOOP 의 경우 아무 작업도 실행하지 않고 정상 종료 되었음을 의미
            - EXIT_MESSAGE
              - 역할 : Job 실행 결과에 대한 상세 메시지
              - Job 이 실패했을 경우, 주로 에러 스택 트레이스의 일부가 이곳에 저장된다.
            - LAST_UPDATED
              - 역할 : 이 실행 기록이 마지막으로 업데이트된 시간
              - Job이 실행되는 동안 STATUS가 바뀔때마다 프레임워크가 업데이트 하는 타임 스탬프 이다.

            ### BATCH_STEP_EXECUTION
            실행 과정에서 **각 단계는 어땠나?** (상세한 작업 내용) 라는 기록을 담고 있다.
            ![](https://res.cloudinary.com/dtrxriyea/image/upload/v1760605212/blog-27/huyyuwxl5qhzmpz1fobt.avif)
            ![](https://res.cloudinary.com/dtrxriyea/image/upload/v1760605223/blog-27/ra8hz2xpkytnlgyyiwa9.avif)

            (너무 많기도 하고, 중복되는 내용이 많아 주관적으로 중요 필드에 대해서만 기입하겠다. )
            - READ_COUNT
              - ItemReader가 데이터 소스에서 성공적으로 읽어온 총 아이템 개수
              - 참고사항으로 chunk 단위를 나타내는 것은 아니다.
            - WRITE_COUNT
              - 필터링이나 스킵을 거쳐 최종적으로 ItemWriter에 전달되어 처리된 아이템 개수
              - 마찬가지로 chunk 단위를 나타내는 것이 아니다.
            - FILTER_COUNT
              - ItemProcessor가 `null`을 반환하여 필터링된(처리되지 않고 걸러진) 아이템 개수
            - COMMIT_COUNT
              - Chunk 단위의 트랜잭션이 성공적으로 커밋된 횟수
              - Step이 처리한 전체 Chunk의 수라고 볼 수 있다.
            - ROLLBACK_COUNT
              - Chunk 처리 중 예외가 발생하여 트랜잭션이 롤백된 횟수
              - 이 값이 1 이상이면 Step 실행 중 문제가 있었다는 의미이다.
            - PROCESS_SKIP_COUNT
              - ItemProcessor에서 처리 중 오류가 발생했지만, Skip 정책에 의해 무시하고 건너뛴 아이템 개수
            - WRITE_SKIP_COUNT
              - ItemWriter에서 처리 중 오류가 발생했지만, Skip 정책에 의해 무시하고 건너뛴 아이템 개수

            ### EXCUTIONCONEXT
            실행 도중 **기억해야 할 정보**는 무엇이었나? (상태 저장) 에 대한 기록을 담고 있다.
            ![](https://res.cloudinary.com/dtrxriyea/image/upload/v1760605234/blog-27/x559pujcv5mzh8yyi8r5.avif)

            - 보통 Job 단위 Excution Context 도 존재하고, Step 단위 Excution Context 도 존재한다.
            - 사용해 보신 분들은 알겠지만, Json + Map 형태로 데이터를 관리한다.

            ## 마무리
            - 개인적으로 Parameter 관련 기능을 사용하진 않았는데, 다음부턴 적극적으로 활용해야 겠다.
            - 장기적으로 Spring Batch 를 관리해야 하는 경우, 에러 관련 필드를 잘 활용하면 좋을 거 같다는 생각을 했다.
            - 아무래도 JobRepository 는 잘 짜여진 구조인 거 같아, 추가적인 정보를 관리하고 싶은 경우 ExcutionContext 에 관리하기보단 추가 테이블을 구성해 관리하는 게 더 좋을 거 같다.

            ## 참고 문헌
            https://docs.spring.io/spring-batch/reference/
            """;
}
