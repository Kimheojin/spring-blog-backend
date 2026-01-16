package HeoJin.demoBlog.category.repository;


import HeoJin.demoBlog.category.dto.data.CategoryWithCountDto;
import HeoJin.demoBlog.category.entity.QCategory;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.entity.QPost;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;


    // 상관 서브쿼리 - > join
    @Override
    public List<CategoryWithCountDto> findAllCategoriesWithCount() {
        QCategory category = QCategory.category;
        QPost post = QPost.post;

        return jpaQueryFactory
                .select(Projections.constructor(CategoryWithCountDto.class,
                        category.id,
                        category.categoryName,
                        post.count(),
                        category.priority))
                .from(category)
                .leftJoin(post).on(post.category.eq(category)
                        .and(post.status.eq(PostStatus.PUBLISHED)))
                .groupBy(category.id, category.categoryName, category.priority)
                .orderBy(category.priority.asc(), category.categoryName.asc())
                .fetch();
    }




}
