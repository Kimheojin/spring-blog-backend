package HeoJin.demoBlog.post.repository;


import HeoJin.demoBlog.category.entity.QCategory;
import HeoJin.demoBlog.member.entity.QMember;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.entity.QPost;
import HeoJin.demoBlog.seo.dto.data.PostForMongoDto;
import HeoJin.demoBlog.tag.entity.QPostTag;
import HeoJin.demoBlog.tag.entity.QTag;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory QFactory;

    // Q객체
    private static final QPost post = QPost.post;
    private static final QMember member = QMember.member;
    private static final QCategory category = QCategory.category;
    private static final QPostTag postTag = QPostTag.postTag;
    private static final QTag tag = QTag.tag;

    @Override
    public Page<Post> findPublishedPostsWithFetch(Pageable pageable) {

        List<Post> posts = QFactory
                .selectFrom(post)
                .join(post.member, member).fetchJoin()
                .join(post.category, category).fetchJoin()
                .where(post.status.eq(PostStatus.PUBLISHED))
                .orderBy(post.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        // status 로 만들어서 인덱스 태우는 정도??
        Long totalCount = QFactory
                .select(post.count())
                .where(post.status.eq(PostStatus.PUBLISHED))
                .from(post)
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;

        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<Post> findPublishedCategoryWithFetch(String categoryName, Pageable pageable) {

        List<Post> posts = QFactory
                .selectFrom(post)
                .join(post.category, category).fetchJoin()
                .join(post.member, member).fetchJoin()
                .where(category.categoryName.eq(categoryName))
                .where(post.status.eq(PostStatus.PUBLISHED))
                .orderBy(post.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 인덱스 태우기
        Long totalCount = QFactory
                .select(post.count())
                .from(post)
                .join(post.category, category)
                .where(category.categoryName.eq(categoryName))
                .where(post.status.eq(PostStatus.PUBLISHED))
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;


        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Optional<Post> findPublishedWithPostId(Long postId) {

        Post result = QFactory
                .selectFrom(post)
                .join(post.member, member).fetchJoin()
                .join(post.category, category).fetchJoin()
                .where(post.id.eq(postId))
                .where(post.status.eq(PostStatus.PUBLISHED))
                .fetchOne();

        return Optional.ofNullable(result);
    }


    @Override
    public Page<Post> findPostsWithFilters(String categoryName, PostStatus postStatus, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        // 카테고리 조건 추가
        if (categoryName != null) {
            builder.and(category.categoryName.eq(categoryName));
        }

        // 상태 조건 추가
        if (postStatus != null) {
            builder.and(post.status.eq(postStatus));
        }

        // 데이터 조회
        List<Post> posts = QFactory
                .selectFrom(post)
                .join(post.category, category).fetchJoin()
                .join(post.member, member).fetchJoin()
                .where(builder)
                .orderBy(post.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        // count 쿼리 부분
        Long totalCount = QFactory
                .select(post.count())
                .from(post)
                .join(post.category, category)
                .where(builder)
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;

        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public List<PostForMongoDto> findPostsForMongo() {
        return QFactory
                .select(Projections.constructor(PostForMongoDto.class,
                        post.id,
                        post.title,
                        post.content,
                        Expressions.list(tag.tagName)))
                .from(post)
                .leftJoin(postTag).on(post.id.eq(postTag.postId))
                .leftJoin(tag).on(postTag.tagId.eq(tag.id))
                .where(post.status.eq(PostStatus.PUBLISHED))
                .groupBy(
                        post.id,
                        post.title,
                        post.content
                )
                .fetch();
    }


}
