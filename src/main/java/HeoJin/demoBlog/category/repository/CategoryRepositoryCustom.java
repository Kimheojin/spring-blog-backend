package HeoJin.demoBlog.category.repository;


import HeoJin.demoBlog.category.dto.data.CategoryWithCountDto;

import java.util.List;

public interface CategoryRepositoryCustom {


    List<CategoryWithCountDto> findAllCategoriesWithCount();

    // postCount 관련 메서드
    void increasePostCount(Long categoryId, Long count);
    void decreasePostCount(Long categoryId, Long count);

    // postcount 동기화
    void syncPostCounts(Long categoryId);
}
