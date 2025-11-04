package HeoJin.demoBlog.configuration.Integration;

public class TestDataProvider {

    // 카테고리 데이터셋
    public String[] getCategoryDataSet() {
        return new String[]{
                "Java1", "Java2"
        };
    }

    // 포스트 데이터셋 [제목, 내용]
    public String[][] getPostDataSet() {
        return new String[][]{
                {"Java 기초 문법 정리", "Java의 기본 문법과 객체지향 프로그래밍에 대해 알아보겠습니다."},
                {"Spring Boot 시작하기", "Spring Boot로 웹 애플리케이션을 개발하는 방법을 소개합니다."},
                {"React Hook 사용법", "React의 useState, useEffect 등 주요 Hook들의 사용법을 정리했습니다."},
                {"JavaScript ES6+ 문법", "최신 JavaScript 문법과 기능들을 예제와 함께 설명합니다."},
                {"MySQL 최적화 팁", "데이터베이스 성능을 향상시키는 다양한 방법들을 알아봅시다."},
                {"알고리즘 문제 해결", "코딩테스트에서 자주 나오는 알고리즘 문제 풀이법입니다."},
                {"Docker 컨테이너 활용", "Docker를 이용한 개발 환경 구축과 배포 방법을 다룹니다."},
                {"AWS EC2 배포하기", "AWS EC2 인스턴스에 애플리케이션을 배포하는 과정을 설명합니다."},
                {"Git 브랜치 전략", "효율적인 Git 브랜치 관리와 협업 방법을 소개합니다."},
                {"DevOps 파이프라인", "CI/CD 파이프라인 구축과 자동화에 대해 알아봅시다."}
        };
    }

    // 댓글 데이터셋
    public String[] getCommentDataSet() {
        return new String[]{
                "정말 유익한 글이네요! 감사합니다.",
                "이해하기 쉽게 설명해주셔서 고맙습니다.",
                "궁금했던 내용인데 도움이 많이 되었어요.",
                "실무에서 바로 적용해볼 수 있을 것 같습니다.",
                "더 자세한 설명도 부탁드립니다.",
                "예제 코드가 정말 도움이 되었습니다.",
                "이런 내용을 찾고 있었는데 딱이네요!",
                "초보자도 이해하기 쉽게 작성해주셨네요.",
                "다음 편도 기대하겠습니다.",
                "공유해주셔서 감사합니다!"
        };
    }
}
