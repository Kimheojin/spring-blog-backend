package HeoJin.demoBlog.category.service;


import HeoJin.demoBlog.category.dto.request.AddCategoryRequest;
import HeoJin.demoBlog.category.dto.request.DeleteCategoryRequest;
import HeoJin.demoBlog.category.dto.request.ModifyCategoryNameRequest;
import HeoJin.demoBlog.category.dto.response.CategoryResponse;
import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;

import HeoJin.demoBlog.global.exception.refactor.BusinessErrorCode;
import HeoJin.demoBlog.global.exception.refactor.BusinessException;
import HeoJin.demoBlog.global.exception.refactor.NotFoundException;

import HeoJin.demoBlog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    // 카테고리 단일 삭제
    @Transactional
    public List<CategoryResponse> deleteCategoryAndGetAll(DeleteCategoryRequest deleteCategoryRequest) {

        Long categoryId = deleteCategoryRequest.getCategoryId();


        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("일치하는 카테고리가 존재하지 않습니다."));
        if(postRepository.existsByCategoryId(categoryId)){
            throw new BusinessException(BusinessErrorCode.DUPLICATE_RESOURCE,
                    "해당 카테고리에 post가 존재합니다.");
        }
        
        // DataIntegrityViolationException -> 무결성 제약 위반시, Spring entity 단에서 발생
        // 체크 예외 X

        categoryRepository.delete(category);

        return categoryRepository.findAllByOrderByPriorityAsc()
                .stream().map(CategoryMapper::toCategoryResponse)
                .toList();
    }

    @Transactional
    public List<CategoryResponse> addCategoryAndGetAll(AddCategoryRequest addCategoryRequest) {
        if(categoryRepository.findByCategoryName(addCategoryRequest.getCategoryName()).isPresent()){
            throw new BusinessException(BusinessErrorCode.DUPLICATE_RESOURCE,
                    "이미 해당 이름의 카테고리가 존재합니다.");
        }

        // 정상 로직은 들여쓰기 없이 깔끔하게
        categoryRepository.save(Category.builder()
                .categoryName(addCategoryRequest.getCategoryName())
                .priority(addCategoryRequest.getPriority())
                .build());

        return categoryRepository.findAllByOrderByPriorityAsc().stream()
                .map(CategoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    // 카테고리 이름 수정
    @Transactional
    public List<CategoryResponse> updateCategoryAndGetAll(ModifyCategoryNameRequest modifyCategoryNameRequest) {
        Category category = categoryRepository.findById(modifyCategoryNameRequest.getCategoryId())
                .orElseThrow(() -> new NotFoundException("해당 카테고리가 존재하지 않습니다."));

        // 변경 감지
        category.updateCategoryName(modifyCategoryNameRequest.getCategoryName());
        category.updatePriority(modifyCategoryNameRequest.getPriority());

        return categoryRepository.findAllByOrderByPriorityAsc().stream()
                .map(CategoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }
}
