package HeoJin.demoBlog.tag.controller;


import HeoJin.demoBlog.tag.dto.request.ListAddTagRequestDto;
import HeoJin.demoBlog.tag.dto.request.ListDeleteTagRequest;
import HeoJin.demoBlog.tag.dto.response.ListTagResponseDto;
import HeoJin.demoBlog.tag.dto.response.PageTagPostResponse;
import HeoJin.demoBlog.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class TagController {

    // 태그 기능이랑 카테고리 기능이랑 차별점을 두는 건 맞음
    // 검색 엔진에도 태그 내용 추가하는 게 좋을듯
    // 일반 post 작성에도 tag 기능 추가행함
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

    // 태그 아이디를 통한 조회
    @GetMapping("/tag/postlist")
    public ResponseEntity<PageTagPostResponse> getTagPostList(
            @RequestParam Long tagId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int pageSize
    ){
        PageTagPostResponse pageTagPostResponse = tagService.reaTagPostList(tagId, page, pageSize);

        return ResponseEntity.ok(pageTagPostResponse);
    }


}
