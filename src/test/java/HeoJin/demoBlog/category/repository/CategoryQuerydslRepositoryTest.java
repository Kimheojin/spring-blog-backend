package HeoJin.demoBlog.category.repository;


import HeoJin.demoBlog.category.dto.data.CategoryWithCountDto;
import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.configuration.InitRepository.TestInitRepository;
import HeoJin.demoBlog.configuration.Integration.DataInitComponent;
import HeoJin.demoBlog.configuration.dataJpaTest.SaveDataJpaTest;
import HeoJin.demoBlog.global.config.QuerydslConfig;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.post.entity.PostStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, DataInitComponent.class, TestInitRepository.class, BCryptPasswordEncoder.class})
public class CategoryQuerydslRepositoryTest extends SaveDataJpaTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private DataInitComponent dataInitComponent;

    @BeforeEach
    void setUp(){
        EntityManager em = entityManager.getEntityManager();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        initializeTestData(em, encoder);
    }

    @Test
    @DisplayName("findAllCategoriesWithCount -> 정상 작동 테스트")
    void test1(){
        // given
        Member testMember = dataInitComponent.createTestMember();
        Category testCategory1 = Category.builder().categoryName("Cat1").priority(1L).postCount(0L).build();
        Category testCategory2 = Category.builder().categoryName("Cat2").priority(2L).postCount(0L).build();
        entityManager.persist(testCategory1);
        entityManager.persist(testCategory2);

        entityManager.persist(createPost(testMember, testCategory1, PostStatus.PUBLISHED, "p1"));
        entityManager.persist(createPost(testMember, testCategory1, PostStatus.PUBLISHED, "p2"));
        entityManager.persist(createPost(testMember, testCategory1, PostStatus.PUBLISHED, "p3"));
        entityManager.persist(createPost(testMember, testCategory1, PostStatus.PRIVATE, "pr1"));
        entityManager.persist(createPost(testMember, testCategory2, PostStatus.PUBLISHED, "p4"));

        entityManager.flush();
        categoryRepository.syncPostCounts(testCategory1.getId());
        categoryRepository.syncPostCounts(testCategory2.getId());
        entityManager.clear();

        // when
        List<CategoryWithCountDto> results = categoryRepository.findAllCategoriesWithCount();

        // then
        CategoryWithCountDto res1 = results.stream().filter(c -> c.getCategoryName().equals("Cat1")).findFirst().orElseThrow();
        CategoryWithCountDto res2 = results.stream().filter(c -> c.getCategoryName().equals("Cat2")).findFirst().orElseThrow();

        assertThat(res1.getPostCount()).isEqualTo(3L);
        assertThat(res2.getPostCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findAllCategoriesWithCount -> PRIVATE 상태 post는 카운트에서 제외되는지 확인")
    void test2(){
        // given
        Member testMember = dataInitComponent.createTestMember();
        Category testCategory = Category.builder().categoryName("PrivateTest").priority(1L).postCount(0L).build();
        entityManager.persist(testCategory);

        entityManager.persist(createPost(testMember, testCategory, PostStatus.PRIVATE, "private"));
        
        entityManager.flush();
        categoryRepository.syncPostCounts(testCategory.getId());
        entityManager.clear();

        // when
        List<CategoryWithCountDto> results = categoryRepository.findAllCategoriesWithCount();

        // then
        CategoryWithCountDto target = results.stream().filter(c -> c.getCategoryName().equals("PrivateTest")).findFirst().orElseThrow();
        assertThat(target.getPostCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("findAllCategoriesWithCount -> PUBLISHED 상태 post는 카운트에 포함되는지 확인")
    void test3(){
        // given
        Member testMember = dataInitComponent.createTestMember();
        Category testCategory = Category.builder().categoryName("PublicTest").priority(1L).postCount(0L).build();
        entityManager.persist(testCategory);

        entityManager.persist(createPost(testMember, testCategory, PostStatus.PUBLISHED, "public"));

        entityManager.flush();
        categoryRepository.syncPostCounts(testCategory.getId());
        entityManager.clear();

        // when
        List<CategoryWithCountDto> results = categoryRepository.findAllCategoriesWithCount();

        // then
        CategoryWithCountDto target = results.stream().filter(c -> c.getCategoryName().equals("PublicTest")).findFirst().orElseThrow();
        assertThat(target.getPostCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findAllCategoriesWithCount -> 여러 상태의 post가 섞여있을 때 PUBLIC만 카운트되는지 확인")
    void test4(){
        // given
        Member testMember = dataInitComponent.createTestMember();
        Category testCategory = Category.builder().categoryName("MixedTest").priority(1L).postCount(0L).build();
        entityManager.persist(testCategory);

        entityManager.persist(createPost(testMember, testCategory, PostStatus.PRIVATE, "pr1"));
        entityManager.persist(createPost(testMember, testCategory, PostStatus.PRIVATE, "pr2"));
        entityManager.persist(createPost(testMember, testCategory, PostStatus.PUBLISHED, "pu1"));
        entityManager.persist(createPost(testMember, testCategory, PostStatus.PUBLISHED, "pu2"));
        entityManager.persist(createPost(testMember, testCategory, PostStatus.PUBLISHED, "pu3"));

        entityManager.flush();
        categoryRepository.syncPostCounts(testCategory.getId());
        entityManager.clear();

        // when
        List<CategoryWithCountDto> results = categoryRepository.findAllCategoriesWithCount();

        // then
        CategoryWithCountDto target = results.stream().filter(c -> c.getCategoryName().equals("MixedTest")).findFirst().orElseThrow();
        assertThat(target.getPostCount()).isEqualTo(3L);
    }

}