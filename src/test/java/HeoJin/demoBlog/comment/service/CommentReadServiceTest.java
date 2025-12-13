package HeoJin.demoBlog.comment.service;

import HeoJin.demoBlog.comment.dto.Response.CommentDto;
import HeoJin.demoBlog.comment.entity.Comment;
import HeoJin.demoBlog.comment.entity.CommentStatus;
import HeoJin.demoBlog.comment.repository.CommentRepository;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CommentReadServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private CommentReadService commentReadService;

    @Test
    @DisplayName("getCommentByPostId -> 정상 동작 테스트")
    void test1() {
        // given
        Post post = createTestPost();
        List<Comment> mockComment = createTestComments(post);

        Mockito.when(postRepository.existsById(post.getId())).thenReturn(true);
        Mockito.when(commentRepository.customFindCommentsByPostId(post.getId()))
                .thenReturn(mockComment);

        // when
        List<CommentDto> result = commentReadService.getCommentByPostId(post.getId());

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(2, result.get(0).getReplies().size());

        Mockito.verify(commentRepository, Mockito.times(1))
                .customFindCommentsByPostId(post.getId());
    }

    @Test
    @DisplayName("getCommentByPostId -> Repository에서 빈 리스트 반환 시")
    void test2() {
        // given
        Mockito.when(postRepository.existsById(-999L)).thenReturn(true);
        Mockito.when(commentRepository.customFindCommentsByPostId(-999L))
                .thenReturn(Collections.emptyList());

        // when
        List<CommentDto> result = commentReadService.getCommentByPostId(-999L);

        // then
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(commentRepository, Mockito.times(1))
                .customFindCommentsByPostId(-999L);
    }

    @Test
    @DisplayName("getAdminCommentByPostId -> 정상 동작 테스트")
    void test3() {
        // given
        Post post = createTestPost();
        List<Comment> mockComment = createTestComments(post);

        Mockito.when(postRepository.existsById(post.getId())).thenReturn(true);
        Mockito.when(commentRepository.customFindAllCommentByPostIdForAdmin(post.getId()))
                .thenReturn(mockComment);

        // when
        List<CommentDto> result = commentReadService.getAdminCommentByPostId(post.getId());

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(2, result.get(0).getReplies().size());

        Mockito.verify(commentRepository, Mockito.times(1))
                .customFindAllCommentByPostIdForAdmin(post.getId());
    }

    @Test
    @DisplayName("getAdminCommentByPostId -> Repository에서 빈 리스트 반환 시")
    void test4() {
        // given
        Mockito.when(postRepository.existsById(-999L)).thenReturn(true);
        Mockito.when(commentRepository.customFindAllCommentByPostIdForAdmin(-999L))
                .thenReturn(Collections.emptyList());

        // when
        List<CommentDto> result = commentReadService.getAdminCommentByPostId(-999L);

        // then
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(commentRepository, Mockito.times(1))
                .customFindAllCommentByPostIdForAdmin(-999L);
    }


    private Post createTestPost() {
        return Post.builder()
                .id(1L)
                .title("대상 포스트")
                .build();
    }

    private List<Comment> createTestComments(Post post) {
        Comment parentComment1 = Comment.builder()
                .id(1L).post(post).content("부모 Comment1")
                .status(CommentStatus.ACTIVE).parent(null).build();

        Comment parentComment2 = Comment.builder()
                .id(2L).post(post).content("부모 Comment2")
                .status(CommentStatus.ACTIVE).parent(null).build();

        return Arrays.asList(
                parentComment1, parentComment2,
                Comment.builder().id(3L).post(post).content("자식 comment")
                        .status(CommentStatus.ACTIVE).parent(parentComment1).build(),
                Comment.builder().id(4L).post(post).content("일반 comment1")
                        .status(CommentStatus.ACTIVE).parent(parentComment1).build(),
                Comment.builder().id(5L).post(post).content("일반 comment2")
                        .status(CommentStatus.ACTIVE).parent(parentComment2).build(),
                Comment.builder().id(6L).post(post).content("일반 comment3")
                        .status(CommentStatus.DELETED).parent(null).build()
        );
    }
}