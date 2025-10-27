package HeoJin.demoBlog.tag.repository;

import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.QPost;
import HeoJin.demoBlog.tag.entity.QPostTag;
import HeoJin.demoBlog.tag.entity.QTag;
import HeoJin.demoBlog.tag.dto.response.TagResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public Page<Post> findPublishedPostWithTag(Long tagId, Pageable pageable) {
        List<Post> posts = jpaQueryFactory

        return null;
    }
}
