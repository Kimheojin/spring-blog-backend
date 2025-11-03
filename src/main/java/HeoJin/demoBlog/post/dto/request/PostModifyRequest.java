package HeoJin.demoBlog.post.dto.request;


import HeoJin.demoBlog.post.entity.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostModifyRequest {
    @NotNull(message = "유효하지 않은 포스트 ID 입니다.")
    private Long postId;
    @NotBlank(message = "변경하고자 하는 제목을 입력해주세요")
    private String title;
    @NotBlank(message = "변경하고자 하는 내용을 입력해주세요")
    private String content;
    @NotBlank(message = "변경하고자 하는 카테고리명을 선택해주세요")
    private String categoryName;
    @NotNull(message = "게시글 상태를 선택 해 주세요")
    private PostStatus postStatus;

    private List<TagRequest> tagList;
}
