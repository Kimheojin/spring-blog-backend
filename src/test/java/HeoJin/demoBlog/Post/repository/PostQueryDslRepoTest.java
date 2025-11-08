package HeoJin.demoBlog.Post.repository;


import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import HeoJin.demoBlog.configuration.InitRepository.TestInitRepository;
import HeoJin.demoBlog.configuration.Integration.DataInitComponent;
import HeoJin.demoBlog.configuration.dataJpaTest.SaveDataJpaTest;
import HeoJin.demoBlog.global.config.QuerydslConfig;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.repository.PostRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@DataJpaTest
@Import({QuerydslConfig.class, DataInitComponent.class, TestInitRepository.class, BCryptPasswordEncoder.class})
public class PostQueryDslRepoTest extends SaveDataJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DataInitComponent dataInitComponent;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp(){
        EntityManager em = entityManager.getEntityManager();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        initializeTestData(em, encoder);
    }

    @Test
    @DisplayName("findPublishedPostWithFetch -> 정상 작동")
    void test1() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();

        Category testCategory = categoryRepository.findAll().get(0);
        Pageable pageable = PageRequest.of(0, 5);

        Post post1 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "1번");
        Post post2 = createPost(testMember, testCategory, PostStatus.PRIVATE, "안나와야 할거");
        Post post3 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "3번");
        Post post4 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "4번");

        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.persist(post3);
        entityManager.persist(post4);

        entityManager.flush();
        entityManager.clear();

        // when
        Page<Post> testResult = postRepository.findPublishedPostsWithFetch(pageable);
        // then
        Assertions.assertFalse(testResult.isEmpty());
        Assertions.assertEquals(3, testResult.getTotalElements() );

        Assertions.assertEquals(3, testResult.getContent().size());
        Assertions.assertEquals(0, testResult.getNumber());
        Assertions.assertEquals(5, testResult.getSize());
        Assertions.assertEquals(1, testResult.getTotalPages());

        testResult.getContent().forEach(post ->
                Assertions.assertEquals(PostStatus.PUBLISHED, post.getStatus())
        );

    }

    @Test
    @DisplayName("findPublishedPostWithFetch -> 빈 결과 처리")
    void test2() {
        // given
        Pageable pageable = PageRequest.of(0, 5);

        // when
        Page<Post> testResult = postRepository.findPublishedPostsWithFetch(pageable);

        // then
        Assertions.assertTrue(testResult.isEmpty());
        Assertions.assertEquals(0, testResult.getTotalElements());
        Assertions.assertEquals(0, testResult.getContent().size());
    }

    @Test
    @DisplayName("findPublishedPostWithFetch -> 페이징 처리 확인")
    void test3() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        Category testCategory = categoryRepository.findAll().get(0);

        // 10개의 PUBLISHED 포스트 생성
        for (int i = 1; i <= 10; i++) {
            Post post = createPost(testMember, testCategory, PostStatus.PUBLISHED, i + "번");
            entityManager.persist(post);
        }

        entityManager.flush();
        entityManager.clear();

        // when - 첫 번째 페이지 (5개씩)
        Pageable firstPage = PageRequest.of(0, 5);
        Page<Post> firstResult = postRepository.findPublishedPostsWithFetch(firstPage);

        // when - 두 번째 페이지 (5개씩)
        Pageable secondPage = PageRequest.of(1, 5);
        Page<Post> secondResult = postRepository.findPublishedPostsWithFetch(secondPage);

        // then
        Assertions.assertEquals(5, firstResult.getContent().size());
        Assertions.assertEquals(5, secondResult.getContent().size());
        Assertions.assertEquals(10, firstResult.getTotalElements());
        Assertions.assertEquals(10, secondResult.getTotalElements());
        Assertions.assertEquals(2, firstResult.getTotalPages());
        Assertions.assertTrue(firstResult.hasNext());
        Assertions.assertFalse(secondResult.hasNext());
    }

    @Test
    @DisplayName("findPublishedCategoryWithFetch -> 카테고리로 조회")
    void test4() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        Category testCategory = categoryRepository.findAll().get(0);
        Category testCategory2 = categoryRepository.findAll().get(1);

        Post post1 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "1번");
        Post post2 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "2번");
        Post post3 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "3번");
        Post post4 = createPost(testMember, testCategory2, PostStatus.PUBLISHED, "4번");
        Post post5 = createPost(testMember, testCategory, PostStatus.PRIVATE, "5번");

        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.persist(post3);
        entityManager.persist(post4);
        entityManager.persist(post5);

        entityManager.flush();
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 5);

        // when 
        Page<Post> result = postRepository.findPublishedCategoryWithFetch(testCategory.getCategoryName(), pageable);

        // then
        Assertions.assertFalse(result.isEmpty());
        result.getContent().forEach(post ->
                Assertions.assertEquals(PostStatus.PUBLISHED, post.getStatus())
        );
        Assertions.assertEquals(3, result.getTotalElements() ); // 전체 페이지

        Assertions.assertEquals(3, result.getContent().size()); // 현재 페이지

    }
    @Test
    @DisplayName("findPublishedCategoryWithFetch -> 존재하지 않는 카테고리")
    void test5() {
        // given
        Pageable pageable = PageRequest.of(0, 5);

        // when
        Page<Post> result = postRepository.findPublishedCategoryWithFetch("존재하지않는카테고리", pageable);

        // then
        Assertions.assertTrue(result.isEmpty());
        Assertions.assertEquals(0, result.getTotalElements());
    }
    @Test
    @DisplayName("findPostsWithFilters -> 정상 작동 테스트")
    void test6() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        Category testCategory = categoryRepository.findAll().get(0);

        Post post1 = createPost(testMember, testCategory, PostStatus.PRIVATE, "2번");
        Post post2 = createPost(testMember, testCategory, PostStatus.PRIVATE, "1번");
        Post post3 = createPost(testMember, testCategory, PostStatus.PRIVATE, "3번");
        Post post4 = createPost(testMember, testCategory, PostStatus.DRAFT, "2번");

        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.persist(post3);
        entityManager.persist(post4);

        entityManager.flush();
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 5);

        // when
        Page<Post> result = postRepository.findPostsWithFilters(testCategory.getCategoryName(),
                PostStatus.PRIVATE, pageable);

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(3, result.getTotalElements()); // PRIVATE 상태 포스트 3개
        Assertions.assertEquals(3, result.getContent().size()); // 현재 페이지에 3개

        result.getContent().forEach(post ->
                Assertions.assertEquals(PostStatus.PRIVATE, post.getStatus())
        );

        // 모든 포스트가 올바른 카테고리인지 확인
        result.getContent().forEach(post ->
                Assertions.assertEquals(testCategory.getCategoryName(), post.getCategory().getCategoryName())
        );


        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(5, result.getSize());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertFalse(result.hasNext()); // 다음 페이지 없음
        Assertions.assertFalse(result.hasPrevious()); // 이전 페이지 없음
    }


    @Test
    @DisplayName("findPostsWithFilters -> 필터 조건에 맞는 데이터가 없는 경우")
    void test7() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        Category testCategory = categoryRepository.findAll().get(0);

        // PUBLISHED 포스트만 생성
        Post post1 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "1번");
        entityManager.persist(post1);
        entityManager.flush();
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 5);

        // when - PRIVATE 상태로 검색 (존재하지 않음)
        Page<Post> result = postRepository.findPostsWithFilters(testCategory.getCategoryName(),
                PostStatus.PRIVATE, pageable);

        // then
        Assertions.assertTrue(result.isEmpty());
        Assertions.assertEquals(0, result.getTotalElements());
        Assertions.assertEquals(0, result.getContent().size());
    }

    @Test
    @DisplayName("findPostsWithFilters -> 다른 카테고리 필터링 테스트")
    void test8() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        Category testCategory1 = categoryRepository.findAll().get(0);
        Category testCategory2 = categoryRepository.findAll().get(1);

        Post post1 = createPost(testMember, testCategory1, PostStatus.PRIVATE, "카테고리1");
        Post post2 = createPost(testMember, testCategory2, PostStatus.PRIVATE, "카테고리2");

        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.flush();
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 5);

        // when - testCategory1으로만 필터링
        Page<Post> result = postRepository.findPostsWithFilters(testCategory1.getCategoryName(),
                PostStatus.PRIVATE, pageable);

        // then
        Assertions.assertEquals(1, result.getTotalElements()); // testCategory1의 포스트만 1개
        Assertions.assertEquals(testCategory1.getCategoryName(),
                result.getContent().get(0).getCategory().getCategoryName());
    }

}
