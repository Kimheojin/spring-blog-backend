package HeoJin.demoBlog.configuration.Integration;

import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import HeoJin.demoBlog.comment.entity.Comment;
import HeoJin.demoBlog.comment.entity.CommentStatus;
import HeoJin.demoBlog.comment.repository.CommentRepository;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.entity.Role;
import HeoJin.demoBlog.member.repository.MemberRepository;
import HeoJin.demoBlog.member.repository.RoleRepository;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

public abstract class ApiDocTestSetup extends ApiDocTestBase {

    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected PostRepository postRepository;
    @Autowired
    protected CommentRepository commentRepository;
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;

    protected TestDataProvider testDataProvider = new TestDataProvider();

    @Value("${test.user.email}")
    protected String TEST_EMAIL;
    @Value("${test.user.password}")
    protected String TEST_PASSWORD;
    @Value("${test.user.name}")
    protected String TEST_MEMBERNAME;
    @Value("${test.user.role}")
    protected String TEST_ROLENAME;

    // Member 관련

    @Transactional
    protected Member createTestMember() {
        String email = TEST_EMAIL;
        String password = TEST_PASSWORD;
        String memberName = TEST_MEMBERNAME;
        String roleName = TEST_ROLENAME;

        // annotation과 동일 - 이미 존재하는지 확인 (중복 방지)
        return memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    // Role 생성 또는 조회
                    Role role = roleRepository.findByRoleName(roleName)
                            .orElseGet(() -> {
                                Role newRole = Role.builder()
                                        .roleName(roleName)
                                        .build();
                                return roleRepository.save(newRole);
                            });

                    // Member 생성
                    Member member = Member.builder()
                            .memberName(memberName)
                            .email(email)
                            .password(passwordEncoder.encode(password))
                            .role(role)
                            .build();

                    return memberRepository.save(member);
                });
    }

    // Category 관련

    @Transactional
    protected void saveAllCategories() {
        String[] categories = testDataProvider.getCategoryDataSet();
        for (String categoryName : categories) {
            saveCategory(categoryName);
        }
    }

    protected void saveCategory(String categoryName) {
        // 이미 존재하는지 확인 (중복 방지)
        categoryRepository.findByCategoryName(categoryName)
                .orElseGet(() -> {
                    Category category = Category.builder()
                            .categoryName(categoryName)
                            .build();
                    return categoryRepository.save(category);
                });
    }

    // Post관련
    @Transactional
    protected void saveAllPosts(Member member) {
        String[][] posts = testDataProvider.getPostDataSet();
        String[] categories = testDataProvider.getCategoryDataSet();

        // 카테고리가 없으면 먼저 생성
        saveAllCategories();

        // PostStatus 배열 (균등 분배용)
        PostStatus[] statusArray = {PostStatus.PUBLISHED, PostStatus.PRIVATE};

        for (int i = 0; i < posts.length; i++) {
            Category category = categoryRepository.findByCategoryName(categories[i % categories.length])
                    .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다"));

            // 4개 상태를 순환하며 균등하게 분배
            PostStatus status = statusArray[i % statusArray.length];
            savePost(posts[i][0], posts[i][1], member, category, status);
        }
    }

    protected void savePost(String title, String content, Member member, Category category, PostStatus status) {
        Post post = Post.builder()
                .title(title)
                .content(content)
                .member(member)
                .category(category)
                .status(status)
                .regDate(LocalDateTime.now().minusDays(16))
                .build();
        postRepository.save(post);
    }

    // Comment 관련

    // saveComment 메서드에 status 파라미터 추가
    protected Comment saveCommentWithStatus(String content, String email, String password, Post post, Comment parent, LocalDateTime regDate, CommentStatus status) {
        Comment comment = Comment.builder()
                .content(content)
                .email(email)
                .password(password)
                .post(post)
                .parent(parent)
                .status(status)  // 전달받은 status 사용
                .regDate(regDate)
                .build();
        return commentRepository.save(comment);
    }

    // 기존 메서드는 ACTIVE로 고정 (하위 호환성)
    protected Comment saveComment(String content, String email, String password, Post post, Comment parent, LocalDateTime regDate) {
        return saveCommentWithStatus(content, email, password, post, parent, regDate, CommentStatus.ACTIVE);
    }

    @Transactional
    protected void saveAllComments() {
        String[] comments = testDataProvider.getCommentDataSet();
        var posts = postRepository.findAll();

        if (posts.isEmpty()) {
            return;
        }

        // CommentStatus 배열 (균등 분배용)
        CommentStatus[] statusArray = {CommentStatus.ACTIVE, CommentStatus.DELETED};

        int commentIndex = 0;
        LocalDateTime baseTime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusDays(30); // 30일 전부터 시작

        for (Post post : posts) {
            int commentCount = 2 + (commentIndex % 2); // 2개 또는 3개
            Comment parentComment = null;

            for (int i = 0; i < commentCount && commentIndex < comments.length; i++) {
                // 댓글마다 시간 간격을 두어 realistic한 데이터 생성
                LocalDateTime commentTime = baseTime.plusHours(commentIndex * 2); // 2시간씩 간격

                // 3개 상태를 순환하며 균등하게 분배
                CommentStatus status = statusArray[commentIndex % statusArray.length];

                Comment comment = saveCommentWithStatus(
                        comments[commentIndex],
                        "test@naver.com",
                        "1234",
                        post,
                        i == 1 ? parentComment : null, // 두 번째 댓글은 대댓글
                        commentTime,
                        status
                );

                if (i == 0) {
                    parentComment = comment;
                }
                commentIndex++;
            }
        }
    }
}
