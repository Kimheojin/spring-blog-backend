package HeoJin.demoBlog.category.repository;


import HeoJin.demoBlog.category.dto.data.CategoryWithCountDto;
import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.configuration.InitRepository.TestInitRepository;
import HeoJin.demoBlog.configuration.Integration.DataInitComponent;
import HeoJin.demoBlog.configuration.dataJpaTest.SaveDataJpaTest;
import HeoJin.demoBlog.global.config.QuerydslConfig;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.post.entity.Post;
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
import java.util.Optional;

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
        dataInitComponent.saveAllCategories();

        Category testCategory1 = categoryRepository.findAll().get(0);
        Category testCategory2 = categoryRepository.findAll().get(1);

        Post publishedPost1 = createPost(testMember, testCategory1, PostStatus.PUBLISHED, "published1");
        Post publishedPost2 = createPost(testMember, testCategory1, PostStatus.PUBLISHED, "published2");
        Post publishedPost3 = createPost(testMember, testCategory1, PostStatus.PUBLISHED, "published3");
        Post privatePost1 = createPost(testMember, testCategory1, PostStatus.PRIVATE, "private1");
        Post privatePost2 = createPost(testMember, testCategory1, PostStatus.PRIVATE, "private2");

        // testCategory2에 PUBLISHED 포스트 1개 생성
        Post publishedPost4 = createPost(testMember, testCategory2, PostStatus.PUBLISHED, "published4");

        entityManager.persist(publishedPost1);
        entityManager.persist(publishedPost2);
        entityManager.persist(publishedPost3);
        entityManager.persist(privatePost1);
        entityManager.persist(privatePost2);
        entityManager.persist(publishedPost4);
        entityManager.flush();
        entityManager.clear();

        // when
        List<CategoryWithCountDto> allCategoriesWithCount = categoryRepository.findAllCategoriesWithCount();
        List<Category> categories = categoryRepository.findAll();

        // then
        assertThat(allCategoriesWithCount).isNotEmpty();
        assertThat(categories).isNotEmpty();
        assertThat(allCategoriesWithCount.size()).isEqualTo(categories.size());

        CategoryWithCountDto category1Result = allCategoriesWithCount.stream()
                .filter(cat -> cat.getCategoryName().equals(testCategory1.getCategoryName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("testCategory1을 찾을 수 없습니다"));

        CategoryWithCountDto category2Result = allCategoriesWithCount.stream()
                .filter(cat -> cat.getCategoryName().equals(testCategory2.getCategoryName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("testCategory2를 찾을 수 없습니다"));

        assertThat(category1Result.getPostCount()).isEqualTo(3L);
        assertThat(category2Result.getPostCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findAllCategoriesWithCount -> PRIVATE 상태 post는 카운트에서 제외되는지 확인")
    void test2(){
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        Category testCategory = categoryRepository.findAll().get(0);

        // PRIVATE 상태의 포스트 생성
        Post privatePost = createPost(testMember, testCategory, PostStatus.PRIVATE, "private");

        entityManager.persist(privatePost);
        entityManager.flush();
        entityManager.clear();

        // when
        List<CategoryWithCountDto> categoriesWithCount = categoryRepository.findAllCategoriesWithCount();

        // PRIVATE 포스트가 있는 카테고리 찾기
        Optional<CategoryWithCountDto> targetCategoryResult = categoriesWithCount.stream()
                .filter(categoryDto -> categoryDto.getCategoryName().equals(testCategory.getCategoryName()))
                .findFirst();

        // then
        assertThat(targetCategoryResult).isPresent(); // 카테고리는 존재
        assertThat(targetCategoryResult.get().getPostCount()).isEqualTo(0L); //포스트 카운트는 0

    }

    @Test
    @DisplayName("findAllCategoriesWithCount -> PUBLISHED 상태 post는 카운트에 포함되는지 확인")
    void test3(){
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        Category testCategory = categoryRepository.findAll().get(0);

        // PUBLISHED 상태의 포스트 생성
        Post publicPost = createPost(testMember, testCategory, PostStatus.PUBLISHED, "public");

        entityManager.persist(publicPost);
        entityManager.flush();
        entityManager.clear();

        // when
        List<CategoryWithCountDto> categoriesWithCount = categoryRepository.findAllCategoriesWithCount();

        Optional<CategoryWithCountDto> targetCategoryResult = categoriesWithCount.stream()
                .filter(categoryDto -> categoryDto.getCategoryName().equals(testCategory.getCategoryName()))
                .findFirst();

        // then
        assertThat(targetCategoryResult).isPresent();
        assertThat(targetCategoryResult.get().getPostCount()).isEqualTo(1L); // PUBLIC 포스트는 카운트됨
    }

    @Test
    @DisplayName("findAllCategoriesWithCount -> 여러 상태의 post가 섞여있을 때 PUBLIC만 카운트되는지 확인")
    void test4(){
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        Category testCategory = categoryRepository.findAll().get(0);

        // PRIVATE 포스트 2개 생성
        Post privatePost1 = createPost(testMember, testCategory, PostStatus.PRIVATE, "private1");
        Post privatePost2 = createPost(testMember, testCategory, PostStatus.PRIVATE, "private2");

        Post publishedPost1 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "published1");
        Post publishedPost2 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "published2");
        Post publishedPost3 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "published3");

        entityManager.persist(privatePost1);
        entityManager.persist(privatePost2);
        entityManager.persist(publishedPost1);
        entityManager.persist(publishedPost2);
        entityManager.persist(publishedPost3);
        entityManager.flush();
        entityManager.clear();

        // when
        List<CategoryWithCountDto> categoriesWithCount = categoryRepository.findAllCategoriesWithCount();

        Optional<CategoryWithCountDto> targetCategoryResult = categoriesWithCount.stream()
                .filter(categoryDto -> categoryDto.getCategoryName().equals(testCategory.getCategoryName()))
                .findFirst();

        // then
        assertThat(targetCategoryResult).isPresent();
        assertThat(targetCategoryResult.get().getPostCount()).isEqualTo(3L); // PUBLIC 포스트만 카운트
    }

}