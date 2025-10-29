package HeoJin.demoBlog.seo.controller;


import HeoJin.demoBlog.seo.dto.response.ListPostSearchResponseDto;
import HeoJin.demoBlog.seo.service.SeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
@RequiredArgsConstructor
public class SeoController {

    private final SeoService seoService;

    // 통합 검색
    @GetMapping("/seo/unified-search")
    public ResponseEntity<ListPostSearchResponseDto> getUnifiedSearch (
            @RequestParam String term
    ){
        // 근데 이거 mongo 는 페이징 처리 지원 안하지 않나..?
        ListPostSearchResponseDto result = seoService.getUnifiedSearch(term);
        return ResponseEntity.ok(result);
    }
}
