package HeoJin.demoBlog.seo.dto.response;

import java.util.List;

public record ListPostSearchResponseDto(
        List<PostSearchResponseDto> postSearchResponseDtoList,
        Long totalCount
) {
}
