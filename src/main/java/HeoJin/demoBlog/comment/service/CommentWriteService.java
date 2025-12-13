package HeoJin.demoBlog.comment.service;

import HeoJin.demoBlog.comment.dto.request.CommentAdminDeleteRequest;
import HeoJin.demoBlog.comment.dto.request.CommentDeleteRequest;
import HeoJin.demoBlog.comment.dto.request.CommentModifyRequest;
import HeoJin.demoBlog.comment.dto.request.CommentWriteRequest;
import HeoJin.demoBlog.comment.entity.Comment;
import HeoJin.demoBlog.comment.repository.CommentRepository;
import HeoJin.demoBlog.global.exception.refactor.BusinessErrorCode;
import HeoJin.demoBlog.global.exception.refactor.BusinessException;
import HeoJin.demoBlog.global.exception.refactor.NotFoundException;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentWriteService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;


    @Transactional
    public void commentWrite(CommentWriteRequest commentWriteRequest) {
        Post post = postRepository.findPublishedWithPostId(commentWriteRequest.getPostId())
                .orElseThrow(() -> new NotFoundException("해당 post 가 존재하지 않습니다."));

        Comment parenComment = null;
        if(commentWriteRequest.getParentId() != null){
            parenComment = commentRepository.findById(commentWriteRequest.getParentId())
                    .orElseThrow(() -> new NotFoundException("부모 댓글이 존재하지 않습니다."));

            if (!parenComment.getPost().getId().equals(post.getId())) {
                throw new BusinessException(BusinessErrorCode.INVALID_REQUEST,
                        "부모 댓글과 같은 게시글이어야 합니다.");
            }
        }

        commentRepository.save(CommentMapper.toComment(commentWriteRequest,
                post, parenComment));
    }

    @Transactional
    public void commentDelete(CommentDeleteRequest request) {
        Comment comment = validateCommentAccess(request.getPostId(),
                request.getCommentId(),
                request.getEmail(),
                request.getPassword());

        comment.delete();
    }

    @Transactional
    public void commentModify(CommentModifyRequest request) {
        Comment comment = validateCommentAccess(request.getPostId(),
                request.getCommentId(),
                request.getEmail(),
                request.getPassword());

        comment.updateComment(request.getContent());
    }

    // 관리자용
    @Transactional
    public void commentAdminDelete(CommentAdminDeleteRequest request) {
        postRepository.findById(request.getPostId())
                .orElseThrow(() -> new NotFoundException("포스트"));


        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new NotFoundException("커맨트"));

        if(comment.getEmail().equals(request.getEmail())){
            comment.adminDelete();
        }
    }

    // 공통 검증 로직
    protected Comment validateCommentAccess(Long postId, Long commentId, String email, String password) {

        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("해당 포스트가 존재하지 않습니다.");
        }


        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("해당 코멘트가 존재하지 않습니다."));


        if (!isMatchAboutEmailAndPassword(comment, email, password)) {
            throw new BusinessException(BusinessErrorCode.INVALID_REQUEST,
                                        "인증 정보가 일치하지 않습니다.");
        }

        return comment;
    }

    protected boolean isMatchAboutEmailAndPassword(Comment comment, String email, String password ){
        return comment.getEmail().equals(email)
                && comment.getPassword().equals(password);
    }
}
