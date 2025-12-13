package HeoJin.demoBlog.seo.service;


import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.seo.dto.response.TriggerResponseDto;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SyncServiceTest {
    // 테스트 대상 객체
    @InjectMocks
    private SyncService syncService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostTagRepository postTagRepository;

    @Test
    @DisplayName("포스트 없으면 빈 결과 반환")
    void test1() {
        // given
        when(postRepository.findPostsForMongo())
                .thenReturn(Collections.emptyList());
        when(postTagRepository.findAllTagListWithPostPublishedId())
                .thenReturn(Collections.emptyMap());
        // when
        TriggerResponseDto result = syncService.triggerSync();

        // then
        Assertions.assertEquals(0, result.getInsertCount());
        Assertions.assertEquals(0, result.getUpdateCount());
        Assertions.assertEquals(0, result.getDeleteCount());
    }
}
