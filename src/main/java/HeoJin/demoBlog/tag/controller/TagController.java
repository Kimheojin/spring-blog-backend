package HeoJin.demoBlog.tag.controller;


import HeoJin.demoBlog.tag.dto.request.ListAddTagRequestDto;
import HeoJin.demoBlog.tag.dto.request.ListDeleteTagRequest;
import HeoJin.demoBlog.tag.dto.response.ListTagResponseDto;
import HeoJin.demoBlog.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    // 태그 추가 엔드포인트
    @PostMapping("/tag/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> updateTag(
            ListAddTagRequestDto listAddTagRequestDto
    ){
        tagService.addTagPost(listAddTagRequestDto);
        // 반환 값 생각하기
        return ResponseEntity.ok("hello");
    }

    // 태그 삭제 엔드 포인트
    @DeleteMapping("/tag/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteTag(
            ListDeleteTagRequest listDeleteTagRequest
    ){
        tagService.deleteTag(listDeleteTagRequest);
        return ResponseEntity.ok("hello");
    }

    // 태그 목록 반환
    @GetMapping("/tag/list")
    public ResponseEntity<ListTagResponseDto> getTagList(
    ){

        ListTagResponseDto tagList = tagService.getTagList();
        return ResponseEntity.ok(tagList);
    }

    //


}
