package HeoJin.demoBlog.comment.repository;


import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import HeoJin.demoBlog.comment.entity.Comment;
import HeoJin.demoBlog.comment.entity.CommentStatus;
import HeoJin.demoBlog.configuration.InitRepository.TestInitRepository;
import HeoJin.demoBlog.configuration.Integration.DataInitComponent;
import HeoJin.demoBlog.configuration.dataJpaTest.SaveDataJpaTest;
import HeoJin.demoBlog.global.config.QuerydslConfig;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@DataJpaTest
@Import({QuerydslConfig.class, DataInitComponent.class, TestInitRepository.class, BCryptPasswordEncoder.class})
public class CommentQuerydslRepositoryTest extends SaveDataJpaTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private DataInitComponent dataInitComponent;
    @BeforeEach
    void setUp(){
        EntityManager em = entityManager.getEntityManager();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        initializeTestData(em, encoder);
    }


    @Test
    @DisplayName("customFindCommentsByPostId -> 정상 동작")
    void test1() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();

        Category testCategory = categoryRepository.findAll().get(0);

        // 테스트용 포스트 생성
        Post targetPost = createPost(testMember, testCategory, PostStatus.PUBLISHED, "target");
        entityManager.persist(targetPost);
        entityManager.flush();

        // 해당 포스트에 댓글 3개 생성
        Comment comment1 = createComment("TestEmail@naver.com", targetPost, "댓글 내용 1",CommentStatus.ACTIVE);
        Comment comment2 = createComment("TestEmail@naver.com", targetPost, "댓글 내용 2",CommentStatus.ACTIVE);
        Comment comment3 = createComment("TestEmail@naver.com", targetPost, "댓글 내용 3",CommentStatus.ACTIVE);

        entityManager.persist(comment1);
        entityManager.persist(comment2);
        entityManager.persist(comment3);
        entityManager.flush();
        entityManager.clear();

        // when
        List<Comment> comments = commentRepository.customFindCommentsByPostId(targetPost.getId());

        // then
        Assertions.assertFalse(comments.isEmpty());
        Assertions.assertEquals(3, comments.size());

        // 모든 댓글이 해당 포스트의 것인지 확인
        comments.forEach(comment ->
                Assertions.assertEquals(targetPost.getId(), comment.getPost().getId()));
    }

    @Test
    @DisplayName("customFindCommentsByPostId -> ADMIN_DELETED 경우 COUNT 확인")
    void test2() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();

        Category testCategory = categoryRepository.findAll().get(0);

        Post targetPost = createPost(testMember, testCategory, PostStatus.PUBLISHED, "target");
        entityManager.persist(targetPost);
        entityManager.flush();

        Comment comment1 = createComment("TestEmail@naver.com", targetPost, "댓글 내용 1",CommentStatus.ADMIN_DELETED);
        Comment comment2 = createComment("TestEmail@naver.com", targetPost, "댓글 내용 2",CommentStatus.ADMIN_DELETED);
        Comment comment3 = createComment("TestEmail@naver.com", targetPost, "댓글 내용 3",CommentStatus.ACTIVE);
        Comment comment4 = createComment("TestEmail@naver.com", targetPost, "댓글 내용 3",CommentStatus.ACTIVE);

        entityManager.persist(comment1);
        entityManager.persist(comment2);
        entityManager.persist(comment3);
        entityManager.persist(comment4);
        entityManager.flush();
        entityManager.clear();

        // when

        List<Comment> comments = commentRepository.customFindCommentsByPostId(targetPost.getId());

        // then

        Assertions.assertFalse(comments.isEmpty());
        Assertions.assertEquals(2, comments.size());

        comments.forEach(comment ->
                Assertions.assertEquals(targetPost.getId(), comment.getPost().getId()));
    }

    @Test
    @DisplayName("customFindCommentsByPostId -> 존재하지 않는 PostId로 조회시 빈 리스트 반환")
    void test3() {
        // given
        Long nonExistentPostId = -999999L;

        // when
        List<Comment> comments = commentRepository.customFindCommentsByPostId(nonExistentPostId);

        // then
        Assertions.assertTrue(comments.isEmpty());
    }

    @Test
    @DisplayName("customFindCommentsByPostId -> 댓글이 없는 포스트 조회시 빈 리스트 반환")
    void test4() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        Category testCategory = categoryRepository.findAll().get(0);

        // 댓글이 없는 포스트 생성
        Post postWithoutComments = createPost(testMember, testCategory, PostStatus.PUBLISHED, "no-comments");
        entityManager.persist(postWithoutComments);
        entityManager.flush();
        entityManager.clear();

        // when
        List<Comment> comments = commentRepository.customFindCommentsByPostId(postWithoutComments.getId());

        // then
        Assertions.assertTrue(comments.isEmpty());
    }

    @Test
    @DisplayName("customFindCommentsByPostId -> 대댓글(parent-child) 관계 정상 조회")
    void test5() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        Category testCategory = categoryRepository.findAll().get(0);

        Post targetPost = createPost(testMember, testCategory, PostStatus.PUBLISHED, "target");
        entityManager.persist(targetPost);
        entityManager.flush();

        // 부모 댓글 생성
        Comment parentComment = createComment("parent@test.com", targetPost, "부모 댓글", CommentStatus.ACTIVE);
        entityManager.persist(parentComment);
        entityManager.flush();

        // 자식 댓글들 생성
        Comment childComment1 = createCommentWithParent("child1@test.com", targetPost, "자식 댓글 1", CommentStatus.ACTIVE, parentComment);
        Comment childComment2 = createCommentWithParent("child2@test.com", targetPost, "자식 댓글 2", CommentStatus.ACTIVE, parentComment);

        entityManager.persist(childComment1);
        entityManager.persist(childComment2);
        entityManager.flush();
        entityManager.clear();

        // when
        List<Comment> comments = commentRepository.customFindCommentsByPostId(targetPost.getId());

        // then
        Assertions.assertEquals(3, comments.size()); // 부모 1개 + 자식 2개

        // 부모-자식 관계 확인
        long parentComments = comments.stream().filter(c -> c.getParent() == null).count();
        long childComments = comments.stream().filter(c -> c.getParent() != null).count();

        Assertions.assertEquals(1, parentComments);
        Assertions.assertEquals(2, childComments);
    }

    @Test
    @DisplayName("customFindAllCommentByPostIdForAdmin -> 상태 상관없이 정상 조회")
    void test6() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();

        Category testCategory = categoryRepository.findAll().get(0);

        // 테스트용 포스트 생성
        Post targetPost = createPost(testMember, testCategory, PostStatus.PUBLISHED, "target");
        entityManager.persist(targetPost);
        entityManager.flush();

        // 해당 포스트에 댓글 3개 생성
        Comment comment1 = createComment("TestEmail@naver.com", targetPost, "댓글 내용 1",CommentStatus.ACTIVE);
        Comment comment2 = createComment("TestEmail@naver.com", targetPost, "댓글 내용 2",CommentStatus.DELETED);
        Comment comment3 = createComment("TestEmail@naver.com", targetPost, "댓글 내용 3",CommentStatus.ADMIN_DELETED);

        entityManager.persist(comment1);
        entityManager.persist(comment2);
        entityManager.persist(comment3);
        entityManager.flush();
        entityManager.clear();

        // when
        List<Comment> comments = commentRepository.customFindAllCommentByPostIdForAdmin(targetPost.getId());

        // then
        Assertions.assertFalse(comments.isEmpty());
        Assertions.assertEquals(3, comments.size());

        // 모든 댓글이 해당 포스트의 것인지 확인
        comments.forEach(comment ->
                Assertions.assertEquals(targetPost.getId(), comment.getPost().getId()));

    }
    @Test
    @DisplayName("customFindAllCommentByPostIdForAdmin -> 존재하지 않는 PostId로 조회시 빈 리스트 반환")
    void test7() {
        // given
        Long nonExistentPostId = -999999L;

        // when
        List<Comment> comments = commentRepository.customFindAllCommentByPostIdForAdmin(nonExistentPostId);

        // then
        Assertions.assertTrue(comments.isEmpty());
    }

    @Test
    @DisplayName("customFindAllCommentByPostIdForAdmin -> 댓글이 없는 포스트 조회시 빈 리스트 반환")
    void test8() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        Category testCategory = categoryRepository.findAll().get(0);

        // 댓글이 없는 포스트 생성
        Post postWithoutComments = createPost(testMember, testCategory, PostStatus.PUBLISHED, "no-comments");
        entityManager.persist(postWithoutComments);
        entityManager.flush();
        entityManager.clear();

        // when
        List<Comment> comments = commentRepository.customFindAllCommentByPostIdForAdmin(postWithoutComments.getId());

        // then
        Assertions.assertTrue(comments.isEmpty());
    }

    @Test
    @DisplayName("customFindAllCommentByPostIdForAdmin -> 대댓글(parent-child) 관계 정상 조회")
    void test9() {
        // given
        Member testMember = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        Category testCategory = categoryRepository.findAll().get(0);

        Post targetPost = createPost(testMember, testCategory, PostStatus.PUBLISHED, "target");
        entityManager.persist(targetPost);
        entityManager.flush();

        // 부모 댓글 생성
        Comment parentComment = createComment("parent@test.com", targetPost, "부모 댓글", CommentStatus.ACTIVE);
        entityManager.persist(parentComment);
        entityManager.flush();

        // 자식 댓글들 생성
        Comment childComment1 = createCommentWithParent("child1@test.com", targetPost, "자식 댓글 1", CommentStatus.ACTIVE, parentComment);
        Comment childComment2 = createCommentWithParent("child2@test.com", targetPost, "자식 댓글 2", CommentStatus.DELETED, parentComment);

        entityManager.persist(childComment1);
        entityManager.persist(childComment2);
        entityManager.flush();
        entityManager.clear();

        // when
        List<Comment> comments = commentRepository.customFindAllCommentByPostIdForAdmin(targetPost.getId());

        // then
        Assertions.assertEquals(3, comments.size()); // 부모 1개 + 자식 2개

        // 부모-자식 관계 확인
        long parentComments = comments.stream().filter(c -> c.getParent() == null).count();
        long childComments = comments.stream().filter(c -> c.getParent() != null).count();

        Assertions.assertEquals(1, parentComments);
        Assertions.assertEquals(2, childComments);
    }


}
