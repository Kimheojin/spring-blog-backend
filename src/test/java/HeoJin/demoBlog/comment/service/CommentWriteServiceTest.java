package HeoJin.demoBlog.comment.service;


import HeoJin.demoBlog.comment.dto.request.CommentAdminDeleteRequest;
import HeoJin.demoBlog.comment.dto.request.CommentDeleteRequest;
import HeoJin.demoBlog.comment.dto.request.CommentModifyRequest;
import HeoJin.demoBlog.comment.dto.request.CommentWriteRequest;
import HeoJin.demoBlog.comment.entity.Comment;
import HeoJin.demoBlog.comment.entity.CommentStatus;
import HeoJin.demoBlog.comment.repository.CommentRepository;
import HeoJin.demoBlog.global.exception.refactor.NotFoundException;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CommentWriteServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;



    @Spy // 부분 mock
    @InjectMocks
    private CommentWriteService commentWriteService;

    @Test
    @DisplayName("commentWrite -> (부모 없?는 경우) 정상 동작 테스트")
    void test1() {
        // given
        Post mockPost = Post.builder()
                .id(1L).build();

        CommentWriteRequest commentWriteRequest = CommentWriteRequest.builder()
                .content("테스트 comment 내용 입니다.")
                .postId(1L)  // mockPost.getId()와 일치
                .email("hurjin1109@naver.com")
                .parentId(null).build();

        Mockito.when(postRepository.findPublishedWithPostId(1L))
                .thenReturn(Optional.of(mockPost));

        // when
        commentWriteService.commentWrite(commentWriteRequest);

        // then
        // 호출 되었는지
        Mockito.verify(postRepository).findPublishedWithPostId(1L);
        Mockito.verify(commentRepository).save(Mockito.any(Comment.class));

        // parentId가 null이므로 호출 X
        Mockito.verify(commentRepository, Mockito.never()).findById(Mockito.any());
    }

    @Test
    @DisplayName("commentWrite -> (부모 있는 경우) 정상 동작 테스트")
    void test3() {
        // given
        Post mockPost = Post.builder()
                .id(1L).build();

        Comment parentComment = Comment.builder()
                .id(2L)
                .post(mockPost) // Post 설정 추가
                .build();

        CommentWriteRequest commentWriteRequest = CommentWriteRequest.builder()
                .content("대댓글 내용")
                .postId(1L)
                .parentId(2L)  // 부모 댓글 ID
                .email("test@test.com")
                .build();

        Mockito.when(postRepository.findPublishedWithPostId(1L))
                .thenReturn(Optional.of(mockPost));
        Mockito.when(commentRepository.findById(2L))
                .thenReturn(Optional.of(parentComment));

        // when
        commentWriteService.commentWrite(commentWriteRequest);

        // then
        Mockito.verify(postRepository).findPublishedWithPostId(1L);
        Mockito.verify(commentRepository).findById(2L);
        Mockito.verify(commentRepository).save(Mockito.any(Comment.class));
    }

    @Test
    @DisplayName("commentWrite -> 포스트가 없는 경우 예외 발생")
    void test4() {
        // given
        CommentWriteRequest commentWriteRequest = CommentWriteRequest.builder()
                .postId(-999L)  // 존재하지 않는 포스트 ID
                .content("테스트")
                .build();

        Mockito.when(postRepository.findPublishedWithPostId(-999L))
                .thenReturn(Optional.empty());

        // when & then
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> commentWriteService.commentWrite(commentWriteRequest));
        
        Assertions.assertTrue(exception.getMessage().contains("post"));

        Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    @DisplayName("commentDelete -> 정상 동작 테스트")
    void test6() {
        // given
        CommentDeleteRequest request = CommentDeleteRequest.builder()
                .postId(1L)
                .commentId(2L)
                .email("test@naver.com")
                .password("1234")
                .build();

        Comment realComment = Comment.builder()
                .id(2L)
                .email("test@naver.com")
                .content("원본 내용")
                .status(CommentStatus.ACTIVE)
                .build();

        // when

        Mockito.doReturn(realComment)
                .when(commentWriteService)
                .validateCommentAccess(1L, 2L, "test@naver.com", "1234");

        commentWriteService.commentDelete(request);

        // then

        Assertions.assertEquals(CommentStatus.DELETED, realComment.getStatus());
    }

    @Test
    @DisplayName("commentModify -> 정상 작동 테스트")
    void test7() {
        CommentModifyRequest request = CommentModifyRequest.builder()
                .postId(1L)
                .commentId(2L)
                .email("test@naver.com")
                .password("1234")
                .content("수정된 내용")
                .build();

        Comment realComment = Comment.builder()
                .id(2L)
                .email("test@naver.com")
                .content("원본 내용")
                .status(CommentStatus.ACTIVE)
                .build();

        Mockito.doReturn(realComment)
                .when(commentWriteService)
                .validateCommentAccess(1L, 2L, "test@naver.com", "1234");

        commentWriteService.commentModify(request);

        Assertions.assertEquals("수정된 내용", realComment.getContent());
    }

    @Test
    @DisplayName("commentAdminDelete -> 정상 작동 테스트")
    void test8() {

        // given
        CommentAdminDeleteRequest request = CommentAdminDeleteRequest.builder()
                .postId(1L)
                .commentId(2L)
                .email("test@naver.com")
                .content("테스트 내용 입니다.")
                .build();

        Post mockPost = Post.builder()
                .id(1L).build();

        Comment realComment = Comment.builder()
                .id(2L)
                .email("test@naver.com")
                .status(CommentStatus.ACTIVE)
                .build();

        // when

        Mockito.when(postRepository.findById(request.getPostId()))
                .thenReturn(Optional.of(mockPost));

        Mockito.when(commentRepository.findById(request.getCommentId()))
                .thenReturn(Optional.of(realComment));

        commentWriteService.commentAdminDelete(request);

        // then

        Mockito.verify(postRepository).findById(1L);
        Mockito.verify(commentRepository).findById(2L);
        Assertions.assertEquals(CommentStatus.ADMIN_DELETED, realComment.getStatus());
    }
}
