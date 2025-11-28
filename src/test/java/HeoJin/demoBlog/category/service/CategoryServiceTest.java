package HeoJin.demoBlog.category.service;


import HeoJin.demoBlog.category.dto.data.CategoryWithCountDto;
import HeoJin.demoBlog.category.dto.response.CategoryResponse;
import HeoJin.demoBlog.category.dto.response.CategoryWithCountResponse;
import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test // 모든 카테고리 조회
    @DisplayName("getAllCategoryNames -> 정상 동작 테스트")
    void test1() {
        // given
        List<Category> mockCategories = Arrays.asList(
                Category.builder()
                        .id(1L)
                        .categoryName("Java")
                        .priority(1L)
                        .build(),
                Category.builder()
                        .id(2L)
                        .categoryName("Spring")
                        .priority(2L)
                        .build(),
                Category.builder()
                        .id(3L)
                        .categoryName("React")
                        .priority(3L)
                        .build()
        );

        Mockito.when(categoryRepository.findAllByOrderByPriorityAsc())  // 변경
                .thenReturn(mockCategories);

        // when
        List<CategoryResponse> result = categoryService.getAllCategoryNames();

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1L ,result.get(0).getCategoryId());
        Assertions.assertEquals(2L ,result.get(1).getCategoryId());
        Assertions.assertEquals(3L ,result.get(2).getCategoryId());
        Assertions.assertEquals("Java" ,result.get(0).getCategoryName());
        Assertions.assertEquals("Spring" ,result.get(1).getCategoryName());

        // 실제로 메소드가 실행 되었는지 확인
        Mockito.verify(categoryRepository).findAllByOrderByPriorityAsc();  // 변경
    }

    @Test
    @DisplayName("getAllCategoryNames -> 빈 카테고리 목록 정상 반환" )
    void test2() {
        // given
        Mockito.when(categoryRepository.findAllByOrderByPriorityAsc()).thenReturn(Collections.emptyList());  // 변경

        // when
        List<CategoryResponse> result = categoryService.getAllCategoryNames();

        // then
        Assertions.assertTrue(result.isEmpty());
        Assertions.assertEquals(0, result.size());

        Mockito.verify(categoryRepository).findAllByOrderByPriorityAsc();  // 변경
    }

    @Test
    @DisplayName("getCategoriesWithPublishedStats -> 정상 동작 확인")
    void test3() {
        // given
        List<CategoryWithCountDto> mockResult = Arrays.asList(
                new CategoryWithCountDto(1L, "Java", 5L, 1L),
                new CategoryWithCountDto(2L, "React", 3L, 2L),
                new CategoryWithCountDto(3L, "Spring", 10L, 3L)
        );


        Mockito.when(categoryRepository.findAllCategoriesWithCount())
                .thenReturn(mockResult);
        // when

        List<CategoryWithCountResponse> result = categoryService.getCategoriesWithPublishedStats();

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(mockResult.get(0).getCategoryName(), result.get(0).getCategoryName());
        Assertions.assertEquals(mockResult.get(0).getPostCount(), result.get(0).getPostCount());
        Assertions.assertEquals(mockResult.get(1).getCategoryName(), result.get(1).getCategoryName());
        Assertions.assertEquals(mockResult.get(1).getPostCount(), result.get(1).getPostCount());

        Mockito.verify(categoryRepository).findAllCategoriesWithCount();

    }

    @Test
    @DisplayName("getCategoriesWithPublishedStats -> 빈 목록 반환")
    void test4() {
        // given
        Mockito.when(categoryRepository.findAllCategoriesWithCount())
                .thenReturn(Collections.emptyList());

        // when
        List<CategoryWithCountResponse> result = categoryService.getCategoriesWithPublishedStats();

        // then
        Assertions.assertTrue(result.isEmpty());
        Assertions.assertEquals(0, result.size());

        Mockito.verify(categoryRepository).findAllCategoriesWithCount();
    }

    @Test
    @DisplayName("getCategoriesWithPublishedStats -> 게시글 수가 0인 카테고리도 포함")
    void test5() {
        // given
        List<CategoryWithCountDto> mockResult = Arrays.asList(
                new CategoryWithCountDto(1L, "Java", 5L, 1L),
                new CategoryWithCountDto(2L, "Empty Category", 0L, 2L)  // 게시글 0개
        );

        Mockito.when(categoryRepository.findAllCategoriesWithCount())
                .thenReturn(mockResult);

        // when
        List<CategoryWithCountResponse> result = categoryService.getCategoriesWithPublishedStats();

        // then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(0L, result.get(1).getPostCount());

        Mockito.verify(categoryRepository).findAllCategoriesWithCount();
    }


}
