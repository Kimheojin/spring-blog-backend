package HeoJin.demoBlog.seo.service;


import HeoJin.demoBlog.global.exception.refactor.NotFoundException;
import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @DisplayName("포스트 없으면 CustomNotFound 발생")
    void test1() {
        // given
        when(postRepository.findPostsForMongo())
                .thenReturn(Collections.emptyList());
        when(postTagRepository.findAllTagListWithPostPublishedId())
                .thenReturn(Collections.emptyMap());
        // when

        // then
        assertThrows(NotFoundException.class, () -> syncService.triggerSync());
    }
}
