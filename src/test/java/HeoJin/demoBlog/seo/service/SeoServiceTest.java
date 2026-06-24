package HeoJin.demoBlog.seo.service;

import HeoJin.demoBlog.seo.dto.response.ListPostSearchResponseDto;
import HeoJin.demoBlog.seo.repository.PostMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SeoServiceTest {

    @InjectMocks
    private SeoService seoService;

    @Mock
    private PostMongoRepository postMongoRepository;

    @Test
    @DisplayName("통합 검색 테스트")
    void test1() {
        // given
        String term = "test";

        ListPostSearchResponseDto expectedResponse = new ListPostSearchResponseDto(Collections.emptyList(), 1L);

        given(postMongoRepository.getUnifiedSearch(term)).willReturn(expectedResponse);

        // when
        ListPostSearchResponseDto response = seoService.getUnifiedSearch(term);

        // then
        assertThat(response).isEqualTo(expectedResponse);
        verify(postMongoRepository).getUnifiedSearch(term);
    }
}
