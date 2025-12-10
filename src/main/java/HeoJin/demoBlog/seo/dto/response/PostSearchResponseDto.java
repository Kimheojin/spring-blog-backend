package HeoJin.demoBlog.seo.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PostSearchResponseDto {
    private String resultTitle;
    private Long postId;
}
