package HeoJin.demoBlog.category.mapper;

import HeoJin.demoBlog.category.dto.data.CategoryWithCountDto;
import HeoJin.demoBlog.category.dto.response.CategoryResponse;
import HeoJin.demoBlog.category.dto.response.CategoryWithCountResponse;
import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.service.CategoryMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CategoryMapperTest {
    
    @Test
    @DisplayName("toCategoryResponse -> Category를 CategoryResponse로 정상 변환")
    void test1() {
        // given
        Category category = Category.builder()
                .id(1L)
                .categoryName("테스트 카테고리")
                .priority(5L)
                .build();
        // when 
        CategoryResponse testCategoryResponse = CategoryMapper.toCategoryResponse(category);

        // then
        Assertions.assertEquals(category.getId(), testCategoryResponse.getCategoryId());
        Assertions.assertEquals(category.getCategoryName(), testCategoryResponse.getCategoryName());

    }

    @Test
    @DisplayName("toCategoryResponse -> null값 Category CategoryResponse로  변환")
    void test2() {
        // given
        Category category = Category.builder()
                .id(null)
                .priority(null)
                .categoryName(null).build();

        // when

        CategoryResponse testCategoryResponse = CategoryMapper.toCategoryResponse(category);

        // then
        Assertions.assertNull(testCategoryResponse.getCategoryName());
        Assertions.assertNull(testCategoryResponse.getCategoryId());
    }

    @Test
    @DisplayName("toCategoryResponse -> 빈 문자열 변환")
    void test3() {
        // given
        Category category = Category.builder()
                .id(92L)
                .categoryName("")
                .priority(1L)
                .build();

        // when

        CategoryResponse testCategoryResponse = CategoryMapper.toCategoryResponse(category);

        // then
        Assertions.assertEquals(category.getId(),testCategoryResponse.getCategoryId());
        Assertions.assertEquals(category.getCategoryName(),testCategoryResponse.getCategoryName());
        Assertions.assertTrue(testCategoryResponse.getCategoryName().isEmpty());
    }

    @Test
    @DisplayName("CategoryWithCountResponse -> 정상 작동 테스트")
    void test4() {
        // given
        CategoryWithCountDto categoryWithCountDto = CategoryWithCountDto.builder()
                .categoryId(1L)
                .postCount(2L)
                .categoryName("테스트 카테고리")
                .priority(3L).build();
        // when
        CategoryWithCountResponse result = CategoryMapper.toCategoryWithCountResponse(categoryWithCountDto);

        // then
        Assertions.assertEquals(categoryWithCountDto.getCategoryId(), result.getCategoryId());
        Assertions.assertEquals(categoryWithCountDto.getPostCount(), result.getPostCount());
        Assertions.assertEquals(categoryWithCountDto.getCategoryName(), result.getCategoryName());
        Assertions.assertNotEquals(categoryWithCountDto.getClass(), result.getClass());

    }
}
