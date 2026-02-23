package HeoJin.demoBlog.category.repository;


import HeoJin.demoBlog.category.dto.data.CategoryWithCountDto;

import java.util.List;

public interface CategoryRepositoryCustom {


    List<CategoryWithCountDto> findAllCategoriesWithCount();

    void increasePostCount(Long categoryId, Long count);
    void decreasePostCount(Long categoryId, Long count);

}
