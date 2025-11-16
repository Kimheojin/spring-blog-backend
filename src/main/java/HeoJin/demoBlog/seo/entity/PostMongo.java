package HeoJin.demoBlog.seo.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

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
    private String plainContent;
    private LocalDateTime syncedDate;
    private List<String> tagList;
    // 해당 데이터 해시값
    private String contentHash;


    public PostMongo update(PostMongo postMysql) {
        this.title = postMysql.getTitle();
        this.plainContent = postMysql.getPlainContent();
        this.tagList = postMysql.getTagList();
        this.contentHash = postMysql.getContentHash();
        this.syncedDate = LocalDateTime.now();

        return this;
    }
}
