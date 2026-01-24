package HeoJin.demoBlog.comment.controller;


import HeoJin.demoBlog.comment.dto.Response.CommentDto;
import HeoJin.demoBlog.comment.dto.Response.CommentListDto;
import HeoJin.demoBlog.comment.dto.request.CommentAdminDeleteRequest;
import HeoJin.demoBlog.comment.service.CommentReadService;
import HeoJin.demoBlog.comment.service.CommentWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminCommentController {


    private final CommentWriteService commentWriteService;
    private final CommentReadService commentReadService;

    // 관리자용
    // 전체 댓글을 리스트 형태로
    @GetMapping("/comments")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommentListDto> getAllComments(){
        List<CommentDto> commentDtoList = commentReadService.getAdminComment();

        return ResponseEntity.ok(new CommentListDto(commentDtoList));
    }

    //상태 상관 X 전체 댓글 조회
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentListDto> getAllCommentsWithPostID(
            @PathVariable Long postId
    ){
        List<CommentDto> commentDtos = commentReadService.getAdminCommentByPostId(postId);
        return ResponseEntity.ok(new CommentListDto(commentDtos));
    }


    // 댓글 + 대댓글 admin 삭제
    @DeleteMapping("/comments")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommentListDto> adminDeleteComment(
            @RequestBody @Valid CommentAdminDeleteRequest commentAdminDeleteRequest
    ){
        commentWriteService.commentAdminDelete(commentAdminDeleteRequest);

        List<CommentDto> commentDtos = commentReadService.getCommentByPostId(commentAdminDeleteRequest.getPostId());

        return ResponseEntity.ok(new CommentListDto(commentDtos));
    }


}
