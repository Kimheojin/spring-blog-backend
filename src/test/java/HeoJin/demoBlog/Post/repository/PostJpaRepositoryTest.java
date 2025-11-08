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
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@DataJpaTest
@Import({QuerydslConfig.class, DataInitComponent.class, TestInitRepository.class, BCryptPasswordEncoder.class})
public class PostJpaRepositoryTest extends SaveDataJpaTest{

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
    @DisplayName("findById -> 정상 작동 // postId 로 조회")
    void test1() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();

        Category testCategory = categoryRepository.findAll().get(0);
        Post post1 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "1번");
        entityManager.persistAndFlush(post1);
        entityManager.clear();

        Long testId = postRepository.findAll().get(0).getId();
        // when
        Optional<Post> testPost = postRepository.findById(testId);
        // then
        Assertions.assertTrue(testPost.isPresent());
        Assertions.assertEquals(testId, testPost.get().getId());
    }
    @Test
    @DisplayName("findById -> 존재하지 않는 id로 조회")
    void test2() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();

        Category testCategory = categoryRepository.findAll().get(0);
        Post post1 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "1번");
        entityManager.persistAndFlush(post1);
        entityManager.clear();

        // when
        Optional<Post> testPost = postRepository.findById(-999L);
        // then
        Assertions.assertFalse(testPost.isPresent());
    }

    @Test
    @DisplayName("findById -> null ID로 조회 시 예외 발생")
    void test3() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();

        Long testId = null;

        Category testCategory = categoryRepository.findAll().get(0);
        Post post1 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "1번");
        entityManager.persistAndFlush(post1);
        entityManager.clear();

        // when + then
        Assertions.assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            postRepository.findById(testId);
        });
    }
    @Test
    @DisplayName("findById -> 다양한 상태 조회 확인")
    void test4() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();

        Category testCategory = categoryRepository.findAll().get(0);
        Post post1 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "1번");
        Post post2 = createPost(testMember, testCategory, PostStatus.PRIVATE, "2번");
        Post post3 = createPost(testMember, testCategory, PostStatus.SCHEDULED, "3번");
        Post post4 = createPost(testMember, testCategory, PostStatus.DRAFT, "4번");
        Object o1 = entityManager.persistAndGetId(post1);
        Object o2 =entityManager.persistAndGetId(post2);
        Object o3 =entityManager.persistAndGetId(post3);
        Object o4 =entityManager.persistAndGetId(post4);
        entityManager.flush();
        entityManager.clear();

        // when
        Optional<Post> testPost1 = postRepository.findById((Long) o1);
        Optional<Post> testPost2 = postRepository.findById((Long) o2);
        Optional<Post> testPost3 = postRepository.findById((Long) o3);
        Optional<Post> testPost4 = postRepository.findById((Long) o4);
        // then
        Assertions.assertTrue(testPost1.isPresent());
        Assertions.assertTrue(testPost2.isPresent());
        Assertions.assertTrue(testPost3.isPresent());
        Assertions.assertTrue(testPost4.isPresent());
        // ID 일치 확인
        Assertions.assertEquals((Long) o1, testPost1.get().getId());
        Assertions.assertEquals((Long) o2, testPost2.get().getId());
        Assertions.assertEquals((Long) o3, testPost3.get().getId());
        Assertions.assertEquals((Long) o4, testPost4.get().getId());
    }

    @Test
    @DisplayName("existsByCategoryId -> 정상 동작 확인")
    void test5() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();

        Category testCategory = categoryRepository.findAll().get(0);
        Category testCategory2 = categoryRepository.findAll().get(1);
        Post testPost1 = createPost(testMember, testCategory2, PostStatus.PUBLISHED, "1번");
        entityManager.persistAndFlush(testPost1);
        entityManager.clear();
        // when
        boolean testResult1 = postRepository.existsByCategoryId(testCategory.getId());
        boolean testResult2 = postRepository.existsByCategoryId(testCategory2.getId());

        // then
        Assertions.assertFalse(testResult1);
        Assertions.assertTrue(testResult2);
    }
    @Test
    @DisplayName("existsByCategoryId -> 잘못된 categoryId")
    void Test6() {
        // given
        Long testId1 = -999L;
        Long testId2 = null;

        // when
        boolean testResult1 = postRepository.existsByCategoryId(testId1);
        boolean testResult2 = postRepository.existsByCategoryId(testId2);

        //then
        Assertions.assertFalse(testResult1);
        Assertions.assertFalse(testResult2);

    }

    @Test
    @DisplayName("findAll -> 정상 동작 테스트")
    void test7() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();

        Category testCategory = categoryRepository.findAll().get(0);
        Post testPost1 = createPost(testMember,testCategory, PostStatus.PUBLISHED, "1번");
        Post testPost2 = createPost(testMember, testCategory, PostStatus.PUBLISHED, "2번");
        Post testPost3 = createPost(testMember,testCategory, PostStatus.PUBLISHED, "3번");

        entityManager.persistAndFlush(testPost1);
        entityManager.persistAndFlush(testPost2);
        entityManager.persistAndFlush(testPost3);
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 5);


        // when
        Page<Post> testPage = postRepository.findAll(pageable);

        // then
        Assertions.assertFalse(testPage.getContent().isEmpty());
        Assertions.assertEquals(3, testPage.getContent().size());
        Assertions.assertEquals(3, testPage.getTotalElements());

    }

    @Test
    @DisplayName("findAll -> 비어 있을 경우 ")
    void test8() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        // when
        Page<Post> testPage = postRepository.findAll(pageable);

        // then
        Assertions.assertTrue(testPage.getContent().isEmpty());
    }

    @Test
    @DisplayName("findAll -> 페이징 경계 테스트")
    void test9() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        Category testCategory = categoryRepository.findAll().get(0);

        // 7개 데이터 생성
        for (int i = 1; i <= 7; i++) {
            Post post = createPost(testMember, testCategory, PostStatus.PUBLISHED, i + "번");
            entityManager.persistAndFlush(post);
        }
        entityManager.clear();

        // when
        Page<Post> firstPage = postRepository.findAll(PageRequest.of(0, 3)); // 첫 페이지
        Page<Post> secondPage = postRepository.findAll(PageRequest.of(1, 3)); // 두 번째 페이지
        Page<Post> thirdPage = postRepository.findAll(PageRequest.of(2, 3)); // 세 번째 페이지

        // then
        Assertions.assertEquals(3, firstPage.getContent().size());
        Assertions.assertEquals(3, secondPage.getContent().size());
        Assertions.assertEquals(1, thirdPage.getContent().size()); // 마지막 페이지

        Assertions.assertEquals(7, firstPage.getTotalElements());
        Assertions.assertEquals(3, firstPage.getTotalPages());
        Assertions.assertTrue(firstPage.isFirst());
        Assertions.assertTrue(thirdPage.isLast());
    }




}
