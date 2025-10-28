package HeoJin.demoBlog.tag.repository;

import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.entity.QPost;
import HeoJin.demoBlog.tag.dto.response.PostTagResponseDto;
import HeoJin.demoBlog.tag.dto.response.TagResponseDto;
import HeoJin.demoBlog.tag.entity.QPostTag;
import HeoJin.demoBlog.tag.entity.QTag;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostTagRepositoryCustomImpl implements PostTagRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static final QPostTag postTag = QPostTag.postTag;
    private static final QTag tag = QTag.tag;
    private static final QPost post = QPost.post;

    @Override
    public List<TagResponseDto> getCountWithTagId() {
        return jpaQueryFactory
                .select(Projections.constructor(TagResponseDto.class,
                        tag.tagName,
                        tag.id,
                        postTag.count()))
                .from(postTag)
                .join(tag).on(postTag.tagId.eq(tag.id))
                .groupBy(tag.tagName, tag.id)
                .fetch();
    }

    @Override
    public Page<PostTagResponseDto> findPublishedPostWithTag(Long tagId, Pageable pageable) {
        List<PostTagResponseDto> posts = jpaQueryFactory
                .select(Projections.constructor(PostTagResponseDto.class,
                        post.id,
                        post.title,
                        post.regDate))
                .from(post)
                .join(postTag).on(postTag.postId.eq(post.id))
                .where(postTag.tagId.eq(tagId)
                        .and(post.status.eq(PostStatus.PUBLISHED)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(post.count())
                .from(post)
                .join(postTag).on(postTag.postId.eq(post.id))
                .where(postTag.tagId.eq(tagId)
                        .and(post.status.eq(PostStatus.PUBLISHED)))
                .fetchOne();


        return new PageImpl<>(posts, pageable, total);
    }
}
