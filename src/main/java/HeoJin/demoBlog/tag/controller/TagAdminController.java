package HeoJin.demoBlog.tag.controller;


import HeoJin.demoBlog.tag.dto.request.ListAddTagRequestDto;
import HeoJin.demoBlog.tag.dto.request.ListDeleteTagRequest;
import HeoJin.demoBlog.tag.dto.response.ListTagResponse;
import HeoJin.demoBlog.tag.service.AdminTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class TagAdminController {
    private final AdminTagService adminTagService;

    // 태그 추가 엔드포인트
    @PostMapping("/tag/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ListTagResponse> updateTag(
            @Valid @RequestBody ListAddTagRequestDto listAddTagRequestDto
    ){
        ListTagResponse listTagResponse = adminTagService.addTagPost(listAddTagRequestDto);
        return ResponseEntity.ok(listTagResponse);
    }

    // 태그 삭제 엔드 포인트
    @DeleteMapping("/tag/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ListTagResponse> deleteTag(
            @Valid @RequestBody ListDeleteTagRequest listDeleteTagRequest
    ){
        ListTagResponse listTagResponse = adminTagService.deleteTag(listDeleteTagRequest);
        return ResponseEntity.ok(listTagResponse);
    }


}
