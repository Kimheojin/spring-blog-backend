package HeoJin.demoBlog.post.service;

import HeoJin.demoBlog.post.dto.response.PagePostResponse;
import HeoJin.demoBlog.post.dto.response.PostContractionResponse;
import HeoJin.demoBlog.post.dto.response.PostResponse;
import HeoJin.demoBlog.post.dto.response.TagResponse;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public class PostMapper {

   public static PagePostResponse toPagePostResponse(List<PostResponse> content, Page<Post> postPage) {

       return PagePostResponse.builder()
               .content(content)
               .pageNumber(postPage.getNumber())
               .pageSize(postPage.getSize())
               .totalElements(postPage.getTotalElements())
               .totalPages(postPage.getTotalPages())
               .first(postPage.isFirst())
               .last(postPage.isLast())
               .build();
    }

    public static PostResponse toPostResponse (Post post, List<TagResponse> tagResponseList) {
        return PostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .memberName(post.getMember().getMemberName())
                .categoryName(post.getCategory().getCategoryName())
                .tagList(tagResponseList)
                .status(PostStatus.valueOf(post.getStatus().name()))
                .regDate(post.getRegDate())
                .build();
    }
    public static PostContractionResponse toPostContractionResponse(Post post) {
        return PostContractionResponse.builder()
                .title(post.getTitle())
                .regDate(post.getRegDate())
                .build();
    }
}

