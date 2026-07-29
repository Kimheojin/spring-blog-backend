package HeoJin.demoBlog.global.scheduler.post;

import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostCountSchedulerTest {

    @InjectMocks
    private PostCountScheduler postCountScheduler;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 게시글 수 동기화 - 모든 카테고리 동기화")
    void test1() { 
        // given
        Category category1 = Category.builder().id(1L).build();
        Category category2 = Category.builder().id(2L).build();

        given(categoryRepository.findAll()).willReturn(List.of(category1, category2));

        // when
        postCountScheduler.syncAllCategoryPostCounts();

        // then
        verify(categoryRepository).syncPostCounts(1L);
        verify(categoryRepository).syncPostCounts(2L);
    }
}
