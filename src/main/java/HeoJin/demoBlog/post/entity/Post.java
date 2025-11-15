package HeoJin.demoBlog.post.entity;


import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    // mysql 적용 완
    @Column(unique = true)
    private String title;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime regDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( // 외래키
            name = "category_id"
//            ,foreignKey = @ForeignKey(
//                    name = "fk_post_category",
//                    foreignKeyDefinition = "FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE"
//            )
    ) // 카테고리 삭제될 때 모든 post 삭제
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PostStatus status = PostStatus.PRIVATE; // 비공개 디폴트

    public void updatePost(String title, String content, PostStatus postStatus){
        this.title = title;
        this.content = content;
        this.status = postStatus;
    }

    public void changeStatus(PostStatus status) {
        this.status = status;
    }

    public void changeRegDate(LocalDateTime time){
        this.regDate = time;
    }


}
