package HeoJin.demoBlog.tag.controller;


import HeoJin.demoBlog.tag.dto.response.ListTagDtoResponseDto;
import HeoJin.demoBlog.tag.dto.response.PageTagPostResponse;
import HeoJin.demoBlog.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;


    // 태그 목록 반환
    @GetMapping("/tag/list")
    public ResponseEntity<ListTagDtoResponseDto> getTagList(
    ){

        ListTagDtoResponseDto tagList = tagService.getTagList();
        return ResponseEntity.ok(tagList);
    }

    // 태그 아이디를 통한 조회
    @GetMapping("/tag/postlist")
    public ResponseEntity<PageTagPostResponse> getTagPostList(
            @RequestParam Long tagId,
            @RequestParam String tagName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int pageSize
    ){
        PageTagPostResponse pageTagPostResponse = tagService.reaTagPostList(tagName,tagId, page, pageSize);

        return ResponseEntity.ok(pageTagPostResponse);
    }


}
