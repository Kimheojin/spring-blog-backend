package HeoJin.demoBlog.seo.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PostMongo {
    // mongo document id
    @Id
    private String id;
    private Long postId;
    private String title;
    private String content;
    private LocalDateTime syncedDate;


    public PostMongo update(PostMongo postMysql) {
        this.content = postMysql.getContent();
        this.title = postMysql.getTitle();
        this.syncedDate = LocalDateTime.now();

        return this;
    }
}
