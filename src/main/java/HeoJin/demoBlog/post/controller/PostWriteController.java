package HeoJin.demoBlog.post.controller;


import HeoJin.demoBlog.post.dto.request.PostDeleteRequest;
import HeoJin.demoBlog.post.dto.request.PostModifyRequest;
import HeoJin.demoBlog.post.dto.request.PostRequest;
import HeoJin.demoBlog.post.dto.request.ScheduledPostRequest;
import HeoJin.demoBlog.post.dto.response.MessageResponse;
import HeoJin.demoBlog.post.dto.response.PostContractionResponse;
import HeoJin.demoBlog.post.service.PostWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class PostWriteController {

    private final PostWriteService postWriteService;


    // 게시글 작성
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/posts")
    public ResponseEntity<PostContractionResponse> writePost(@AuthenticationPrincipal Long memberId,
                                                             @RequestBody @Valid  PostRequest postDto) {
        return ResponseEntity.ok(postWriteService.writePost(memberId, postDto));
    }

    // 게시글 수정
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/posts")
    public ResponseEntity<PostContractionResponse> modifyPost(
            @RequestBody @Valid PostModifyRequest postModifyRequest
    ){
        return ResponseEntity.ok(postWriteService.updatePost(postModifyRequest));
    }

    // 게시글 삭제
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/posts")
    public ResponseEntity<MessageResponse> deletePost(
            @RequestBody @Valid PostDeleteRequest postDeleteRequest){
        postWriteService.deletePost(postDeleteRequest);
        return ResponseEntity.ok(MessageResponse.of("게시글이 성골적으로 삭제 되었습니다."));
    }

    // 예약 발행 게시글작성
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/scheduled-post")
    public ResponseEntity<PostContractionResponse> schedulePost(@AuthenticationPrincipal Long memberId,
                                                             @RequestBody @Valid ScheduledPostRequest postDto) {
        return ResponseEntity.ok(postWriteService.schedulePost(memberId, postDto));
    }

}
