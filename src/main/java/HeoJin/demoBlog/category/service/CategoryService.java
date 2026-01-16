package HeoJin.demoBlog.category.service;


import HeoJin.demoBlog.category.dto.response.CategoryResponse;
import HeoJin.demoBlog.category.dto.data.CategoryWithCountDto;
import HeoJin.demoBlog.category.dto.response.CategoryWithCountResponse;
import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    
    // 전체 카테고리 목록 반환
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategoryNames() {

        List<Category> categories = categoryRepository.findAllByOrderByPriorityAsc();

        return categories.stream()
                .map(CategoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    // post 수가 포함된 목록 반환
    @Transactional(readOnly = true)
    public List<CategoryWithCountResponse> getCategoriesWithPublishedStats() {
        List<CategoryWithCountDto> results = categoryRepository.findAllCategoriesWithCount();

        return results.stream()
                .map(CategoryMapper::toCategoryWithCountResponse)
                .collect(Collectors.toList());
    }



    // (반정규화 버전) post_count 컬럼을 직접 사용하여 목록 반환
    @Transactional(readOnly = true)
    public List<CategoryWithCountResponse> getCategoriesWithStatsDenormalized() {
        return categoryRepository.findAllByOrderByPriorityAsc().stream()
                .map(CategoryMapper::toCategoryWithCountResponse)
                .collect(Collectors.toList());
    }

}
