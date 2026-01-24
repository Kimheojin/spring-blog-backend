package HeoJin.demoBlog.comment.service;

import HeoJin.demoBlog.comment.dto.Response.CommentDto;
import HeoJin.demoBlog.comment.entity.Comment;
import HeoJin.demoBlog.comment.repository.CommentRepository;
import HeoJin.demoBlog.global.exception.refactor.NotFoundException;
import HeoJin.demoBlog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class CommentReadService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    // postId에 따른 전체 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentByPostId(Long postId) {
        // 유효성 검사
        validatePostExists(postId);

        // 사용자 삭제, active 부분 조회
        List<Comment> comments = commentRepository.customFindCommentsByPostId(postId);

        return comments.stream()
                .filter(comment -> comment.getParent() == null)
                // 사용자 삭제 부분 변환
                .map(comment -> buildCommentTree(comment, comments))
                .collect(toList());
    }

    // 관리자용
    @Transactional(readOnly = true)
    public List<CommentDto> getAdminCommentByPostId(Long postId) {
        validatePostExists(postId);

        List<Comment> comments = commentRepository.customFindAllCommentByPostIdForAdmin(postId);

        return comments.stream()
                .filter(comment -> comment.getParent() == null)
                .map(comment -> buildAdminCommentTree(comment, comments))
                .collect(toList());

    }
    // 관리자용
    // comment list 조회
    @Transactional(readOnly = true)
    public List<CommentDto> getAdminComment() {
        List<Comment> allComments = commentRepository.findAll();

        return allComments.stream()
                .filter(comment -> comment.getParent() == null)
                .map(comment -> buildAdminCommentTree(comment, allComments))
                .collect(toList());
    }

    private CommentDto buildCommentTree(Comment comment, List<Comment> comments){

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        List<CommentDto> replies = comments.stream()
                .filter(c -> c.getParent() != null
                        && c.getParent().getId().equals(comment.getId()))
                .map(CommentMapper::toCommentDto)
                .collect(toList());

        commentDto.setReplies(replies);
        return commentDto;
    }

    private CommentDto buildAdminCommentTree(Comment comment, List<Comment> comments){

        CommentDto commentDto = CommentMapper.toCommentAdminDto(comment);

        List<CommentDto> replies = comments.stream()
                .filter(c -> c.getParent() != null && c.getParent().getId().equals(comment.getId()))
                .map(CommentMapper::toCommentAdminDto)
                .collect(toList());

        commentDto.setReplies(replies);
        return commentDto;
    }

    private void validatePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("해당 post가 존재하지 않습니다.");
        }
    }



}
