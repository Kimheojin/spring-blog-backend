package HeoJin.demoBlog.tag.repository;

import HeoJin.demoBlog.post.dto.response.TagResponse;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.entity.QPost;
import HeoJin.demoBlog.tag.dto.data.PostIdWithTagDto;
import HeoJin.demoBlog.tag.dto.response.PostTagResponseDto;
import HeoJin.demoBlog.tag.dto.response.TagResponseDto;
import HeoJin.demoBlog.tag.entity.QPostTag;
import HeoJin.demoBlog.tag.entity.QTag;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<PostIdWithTagDto> getTagListWithPostIdList(List<Long> postIds) {

        List<PostIdWithTagDto> postIdWithTagDtos = jpaQueryFactory
                .select(Projections.constructor(PostIdWithTagDto.class,
                        postTag.postId,
                        postTag.tagId,
                        tag.tagName))
                .from(postTag)
                .join(tag).on(postTag.tagId.eq(tag.id))
                .where(postTag.postId.in(postIds))
                .fetch();
        return postIdWithTagDtos;
    }

    @Override
    public List<TagResponse> getTagListWithPostId(Long postId) {

        List<TagResponse> tagResponseList = jpaQueryFactory
                .select(Projections.constructor(TagResponse.class,
                        tag.tagName,
                        tag.id
                        ))
                .from(postTag)
                .join(tag).on(postTag.tagId.eq(tag.id))
                .where(postTag.postId.eq(postId))
                .fetch();


        return tagResponseList;
    }

    @Override
    public Map<Long, List<String>> findAllTagListWithPostPublishedId() {

        List<Tuple> tupleList = jpaQueryFactory
                .select(post.id, tag.tagName)
                .from(post)
                .where(post.status.eq(PostStatus.PUBLISHED))
                .leftJoin(postTag).on(post.id.eq(postTag.postId))
                .leftJoin(tag).on(postTag.tagId.eq(tag.id))
                .fetch();

        Map<Long, List<String>> result = new HashMap<>();

        tupleList.forEach(t -> {
            Long postId = t.get(post.id);
            String tagName = t.get(tag.tagName);
            if(tagName == null){
                // computIfAbsent -> 기존 postId 없으면, ArrayList 초기화 후 실행
                result.computeIfAbsent(postId, k -> new ArrayList<>());
            }else{
                result.computeIfAbsent(postId, k -> new ArrayList<>())
                        .add(tagName);
            }
        });

        return result;
    }

}
