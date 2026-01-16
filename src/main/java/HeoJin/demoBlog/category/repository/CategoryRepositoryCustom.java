package HeoJin.demoBlog.category.repository;


import HeoJin.demoBlog.category.dto.data.CategoryWithCountDto;

import java.util.List;

public interface CategoryRepositoryCustom {


    List<CategoryWithCountDto> findAllCategoriesWithCount();

}
