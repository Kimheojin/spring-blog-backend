package HeoJin.demoBlog.tag.dto.response;

import HeoJin.demoBlog.post.dto.response.PostResponse;

import java.util.List;

public record PageTagPostResponse(List<PostResponse> content, int pageNumber,
                                  int pageSize, long totalElements,
                                  int totalPages, boolean first, boolean last) {
}
