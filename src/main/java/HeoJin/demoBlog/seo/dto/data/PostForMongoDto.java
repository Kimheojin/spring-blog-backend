package HeoJin.demoBlog.seo.dto.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostForMongoDto {
    private Long postId;
    private String title;
    private String content;
    private List<String> tagList;

}
