package HeoJin.demoBlog.post.dto.request;


import HeoJin.demoBlog.post.entity.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {

    @NotBlank(message = "제목을 입력해 주세요")
    @Size(max = 100, message = "제목은 100자를 넘지 못합니다.")
    private String title;
    @NotBlank(message = "내용을 입력해 주세요")
    private String content;
    @NotBlank(message = "카테고리 명을 입력해 주세요")
    private String categoryName;
    @NotNull(message = "게시글 상태를 선택해주세요.")
    private PostStatus postStatus; // 잘못된 값 오면 400에러

    private List<TagRequest> tagList;


}
