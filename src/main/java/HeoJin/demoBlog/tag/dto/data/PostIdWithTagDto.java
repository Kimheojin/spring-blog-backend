package HeoJin.demoBlog.tag.dto.data;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PostIdWithTagDto {
    private Long postId;
    private Long tagId;
    private String tagName;
}
