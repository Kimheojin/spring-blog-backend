package HeoJin.demoBlog.seo.dto.data;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostForMongoDto {
    private Long postId;
    private String title;
    private String content;

}
