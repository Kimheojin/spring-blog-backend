package HeoJin.demoBlog.category.service;

import HeoJin.demoBlog.category.dto.request.AddCategoryRequest;
import HeoJin.demoBlog.category.dto.request.DeleteCategoryRequest;
import HeoJin.demoBlog.category.dto.request.ModifyCategoryNameRequest;
import HeoJin.demoBlog.category.dto.response.CategoryResponse;
import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import HeoJin.demoBlog.global.exception.refactor.BusinessException;
import HeoJin.demoBlog.post.repository.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminCategoryServiceTest {

    @InjectMocks
    private AdminCategoryService adminCategoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private PostRepository postRepository;

    @Test
    @DisplayName("addCategoryAndGetAll 정상 동작 테스트")
    void test1() {
        // given
        String categoryName = "New Category";
        Long priority = 1L;
        AddCategoryRequest request = AddCategoryRequest.builder()
                .categoryName(categoryName)
                .priority(priority)
                .build();

        Category savedCategory = Category.builder()
                .id(1L)
                .categoryName(categoryName)
                .priority(priority)
                .build();

        given(categoryRepository.findByCategoryName(categoryName)).willReturn(Optional.empty());
        given(categoryRepository.findAllByOrderByPriorityAsc()).willReturn(List.of(savedCategory));

        // when
        List<CategoryResponse> result = adminCategoryService.addCategoryAndGetAll(request);

        // then
        verify(categoryRepository).save(any(Category.class));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryName()).isEqualTo(categoryName);
    }

    @Test
    @DisplayName("updateCategoryAndGetAll 정상 동작 테스트")
    void test2() {
        // given
        Long categoryId = 1L;
        String oldName = "Old Name";
        String newName = "New Name";
        Long priority = 2L;

        ModifyCategoryNameRequest request = ModifyCategoryNameRequest.builder()
                .categoryId(categoryId)
                .categoryName(newName)
                .priority(priority)
                .build();

        Category existingCategory = Category.builder()
                .id(categoryId)
                .categoryName(oldName)
                .priority(1L)
                .build();
        
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(existingCategory));
        given(categoryRepository.findByCategoryName(newName)).willReturn(Optional.empty());
        
        Category updatedCategoryStub = Category.builder()
                .id(categoryId)
                .categoryName(newName)
                .priority(priority)
                .build();
        given(categoryRepository.findAllByOrderByPriorityAsc()).willReturn(List.of(updatedCategoryStub));

        // when
        List<CategoryResponse> result = adminCategoryService.updateCategoryAndGetAll(request);

        // then
        assertThat(existingCategory.getCategoryName()).isEqualTo(newName);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("deleteCategoryAndGetAll 정상 동작 테스트")
    void test3() {
        // given
        Long categoryId = 1L;
        DeleteCategoryRequest request = DeleteCategoryRequest.builder()
                .categoryId(categoryId)
                .build();

        Category existingCategory = Category.builder()
                .id(categoryId)
                .categoryName("To Be Deleted")
                .build();

        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(existingCategory));
        given(postRepository.existsByCategoryId(categoryId)).willReturn(false);
        given(categoryRepository.findAllByOrderByPriorityAsc()).willReturn(List.of());

        // when
        List<CategoryResponse> result = adminCategoryService.deleteCategoryAndGetAll(request);

        // then
        verify(categoryRepository).delete(existingCategory);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("카테고리 추가 실패 - 중복된 이름 존재")
    void test4() {
        // given
        String categoryName = "Duplicate Category";
        AddCategoryRequest request = AddCategoryRequest.builder()
                .categoryName(categoryName)
                .build();

        Category existingCategory = Category.builder()
                .id(1L)
                .categoryName(categoryName)
                .build();

        given(categoryRepository.findByCategoryName(categoryName)).willReturn(Optional.of(existingCategory));

        // when + then
        Assertions.assertThatThrownBy(() -> adminCategoryService.addCategoryAndGetAll(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 해당 이름의 카테고리가 존재합니다.");
    }

    @Test
    @DisplayName("카테고리 수정 실패 - 중복된 이름 존재")
    void test5() {
        // given
        Long categoryId = 1L;
        String newName = "Existing Name";
        ModifyCategoryNameRequest request = ModifyCategoryNameRequest.builder()
                .categoryId(categoryId)
                .categoryName(newName)
                .build();

        Category targetCategory = Category.builder()
                .id(categoryId)
                .categoryName("Old Name")
                .build();

        Category otherCategory = Category.builder()
                .id(2L)
                .categoryName(newName)
                .build();

        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(targetCategory));
        given(categoryRepository.findByCategoryName(newName)).willReturn(Optional.of(otherCategory));

        // when + then
        Assertions.assertThatThrownBy(() -> adminCategoryService.updateCategoryAndGetAll(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 해당 이름의 카테고리가 존재합니다.");
    }

    @Test
    @DisplayName("카테고리 삭제 실패 - 게시글 존재")
    void test7() {
        // given
        Long categoryId = 1L;
        DeleteCategoryRequest request = DeleteCategoryRequest.builder()
                .categoryId(categoryId)
                .build();

        Category existingCategory = Category.builder()
                .id(categoryId)
                .categoryName("Category With Posts")
                .build();

        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(existingCategory));
        given(postRepository.existsByCategoryId(categoryId)).willReturn(true);

        // when + then
        Assertions.assertThatThrownBy(() -> adminCategoryService.deleteCategoryAndGetAll(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("해당 카테고리에 post가 존재합니다.");
    }
}
