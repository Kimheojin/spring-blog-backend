package HeoJin.demoBlog.configuration.mongo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MongoConfigTest {
    // 원래 코드 mongoTemplate 사용 x, 테스트 전용
    @Autowired
    private MongoTemplate mongoTemplate;


    @Value("${mongo.collectionName}")
    private String collectionName;

    @Test
    @DisplayName("mongoTemplate 관련 주입 테스트")
    void test1() {

        // given

        // when
        // then
        assertThat(mongoTemplate).isNotNull();
        assertThat(mongoTemplate).isInstanceOf(MongoTemplate.class);
        assertThat(mongoTemplate.getDb()).isNotNull();
    }

    @Test
    @DisplayName("application-test Value 값 테스트")
    void test2() {
        // given

        // when

        // then
        assertThat(collectionName).isNotNull();
        assertThat(collectionName).isEqualTo("testCollection");
    }

    @Test
    @DisplayName("테스트용 MongoDb DB 관련 테스트")
    void test3() {
        // given

        // when
        String dbName = mongoTemplate.getDb().getName();

        // then
        assertThat(dbName).isNotNull();
        assertThat(dbName).isNotEmpty();
    }

    @Test
    @DisplayName("테스트용 Mongo collection 생성 및 확인")
    void test4() {
        // given
        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName);
        }

        // when
        boolean exists = mongoTemplate.collectionExists(collectionName);
        Set<String> collectionNames = mongoTemplate.getCollectionNames();

        // then
        assertThat(exists).isTrue();
        assertThat(collectionNames).contains("testCollection");
    }
}
