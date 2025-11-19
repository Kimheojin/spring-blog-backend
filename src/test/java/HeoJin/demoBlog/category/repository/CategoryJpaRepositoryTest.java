package HeoJin.demoBlog.category.repository;


import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.global.config.QuerydslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;



@DataJpaTest // 이건 자동 롤백 되는듯
@Import(QuerydslConfig.class) // 쿼리 팩토리 가져오기 위해
public class CategoryJpaRepositoryTest {


    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CategoryRepository categoryRepository;



    @Test
    @DisplayName("findByCategoryName -> 정상 작동 테스트")
    void test1() {
        //given
        final String testCategoryName = "테스트 카테고리" ;
        Category category1 = Category.builder()
                .categoryName(testCategoryName)
                .priority(1L)
                .build();
        Category category2 = Category.builder()
                .categoryName("테스트 카테고리 2")
                .priority(2L)
                .build();

        entityManager.persist(category1);
        entityManager.persist(category2);

        entityManager.flush();

        //when
        Optional<Category> result = categoryRepository.findByCategoryName(testCategoryName);

        //then
        assertThat(result).isPresent(); // Optional 먼저 검증
        assertThat(result.get().getCategoryName()).isEqualTo(testCategoryName);
    }
    @Test
    @DisplayName("findByCategoryName -> 비었을 떄 optional false 정상 반환 하는지")
    void test2() {
        //given
        final String testCategoryName = "테스트 카테고리" ;
        Category category1 = Category.builder()
                .categoryName(testCategoryName)
                .build();
        entityManager.persist(category1);
        entityManager.flush();
        //when
        Optional<Category> result = categoryRepository.findByCategoryName(testCategoryName + "1");

        //then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    @DisplayName("findByCategoryName -> 공백 문자, null 값 조회시 빈 Optional 반환")
    void test3() {
        //given
        final String testCategoryName = "테스트 카테고리";

        Category category1 = Category.builder()
                .categoryName(testCategoryName)
                .build();

        entityManager.persist(category1);
        entityManager.flush();

        //when
        Optional<Category> result1 = categoryRepository.findByCategoryName(null);
        Optional<Category> result2 = categoryRepository.findByCategoryName("");

        //then
        assertThat(result1.isPresent()).isFalse();
        assertThat(result2.isPresent()).isFalse();
    }


    @Test
    @DisplayName("findById -> CategoryId로 조회 시 정상 작동 하는 지")
    void test4() {
        //given
        final String testCategoryName = "테스트 카테고리";

        Category category1 = Category.builder()
                .categoryName(testCategoryName)
                .build();

        // 이거 외우면 좋을 듯
        Category savedCategory = entityManager.persistAndFlush(category1);
        Long testId = savedCategory.getId();

        //when
        Optional<Category> testCategory = categoryRepository.findById(testId);

        //then
        assertThat(testCategory).isPresent();
        assertThat(testCategory.get().getCategoryName()).isEqualTo(testCategoryName);

    }

    @Test
    @DisplayName("findById -> 임의로 존재하지 않는 수 넣고 false 반환하는 지 ")
    void test5() {
        //given
        final String testCategoryName = "테스트 카테고리";

        Category category1 = Category.builder()
                .categoryName(testCategoryName)
                .build();

        entityManager.persistAndFlush(category1);

        //when
        Optional<Category> testCategory = categoryRepository.findById(-999L);

        //then
        assertThat(testCategory).isEmpty();

    }

    @Test
    @DisplayName("findById -> 빈 문자열 + null 값 넣고 조회 시 예외 발생 시키는 지")
    void test6() {
        //given
        final String testCategoryName = "테스트 카테고리";

        Category category1 = Category.builder()
                .categoryName(testCategoryName)
                .build();

        entityManager.persistAndFlush(category1);

        //when + then
        assertThatThrownBy(() -> categoryRepository.findById(null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);

    }

}
