package HeoJin.demoBlog.tag.dto.response;

import HeoJin.demoBlog.post.dto.response.TagResponse;

import java.util.List;

public record ListTagResponse(
        List<TagResponse> tagResponseList
) {
}
