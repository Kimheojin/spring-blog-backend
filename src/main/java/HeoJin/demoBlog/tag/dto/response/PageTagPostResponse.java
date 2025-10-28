package HeoJin.demoBlog.tag.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageTagPostResponse(List<PostTagResponseDto> content, int pageNumber,
                                  int pageSize, long totalElements,
                                  int totalPages, boolean first, boolean last) {

    public static PageTagPostResponse fromPage(Page<PostTagResponseDto> page) {
        return new PageTagPostResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
