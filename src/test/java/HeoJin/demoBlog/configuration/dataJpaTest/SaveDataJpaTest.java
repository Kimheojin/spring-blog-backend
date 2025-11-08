package HeoJin.demoBlog.configuration.dataJpaTest;

import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.comment.entity.Comment;
import HeoJin.demoBlog.comment.entity.CommentStatus;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import jakarta.persistence.EntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public abstract class SaveDataJpaTest {
    protected EntityManager entityManager;
    protected BCryptPasswordEncoder passwordEncoder;

    protected void initializeTestData(EntityManager entityManager, BCryptPasswordEncoder passwordEncoder) {
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    // Post 생성 메서드
    protected Post createPost(Member member, Category category, PostStatus status, String suffix) {
        return Post.builder()
                .member(member)
                .category(category)
                .status(status)
                .content("test 내용입니다 " + suffix)
                .title("test 제목입니다 " + suffix)
                .regDate(LocalDateTime.now())
                .build();
    }

    protected Comment createComment(String email, Post post, String content, CommentStatus commentStatus) {
        return Comment.builder()
                .email(email)  // 또는 email/password 방식이면 그에 맞게
                .post(post)
                .content(content)
                .status(commentStatus)
                .regDate(LocalDateTime.now())
                .build();
    }
    protected Comment createCommentWithParent(String email, Post post, String content, CommentStatus status, Comment parent) {
        return Comment.builder()
                .email(email)
                .password("1234")
                .post(post)
                .content(content)
                .status(status)
                .parent(parent)
                .regDate(LocalDateTime.now())
                .build();
    }
}