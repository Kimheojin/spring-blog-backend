package HeoJin.demoBlog.seo.service;

import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.seo.dto.data.PostForMongoDto;
import HeoJin.demoBlog.seo.dto.response.TriggerResponseDto;
import HeoJin.demoBlog.seo.entity.PostMongo;
import HeoJin.demoBlog.seo.repository.PostMongoRepository;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SyncServiceTest {

    @InjectMocks
    private SyncService syncService;
    @Mock
    private PostMongoRepository postMongoRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostTagRepository postTagRepository;

    @Test
    @DisplayName("동기화 - 데이터 없음")
    void test3() {
        // given
        given(postRepository.findPostsForMongo()).willReturn(Collections.emptyList());
        given(postTagRepository.findAllTagListWithPostPublishedId()).willReturn(Collections.emptyMap());

        // when
        TriggerResponseDto response = syncService.triggerSync();

        // then
        assertThat(response.getInsertCount()).isEqualTo(0);
        assertThat(response.getUpdateCount()).isEqualTo(0);
        assertThat(response.getDeleteCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("동기화 - 삽입, 수정, 삭제, 유지")
    void test1() {
        // given
        PostForMongoDto newPost = PostForMongoDto.builder()
                .postId(1L).title("New").content("Content1").build();

        PostForMongoDto updatedPost = PostForMongoDto.builder()
                .postId(2L).title("Updated").content("Content2").build();

        PostForMongoDto unchangedPost = PostForMongoDto.builder()
                .postId(3L).title("Unchanged").content("Content3").build();

        List<PostForMongoDto> mysqlPosts = List.of(newPost, updatedPost, unchangedPost);
        given(postRepository.findPostsForMongo()).willReturn(mysqlPosts);
        
        Map<Long, List<String>> tagMap = new HashMap<>();
        tagMap.put(1L, List.of("Tag1"));
        tagMap.put(2L, List.of("Tag2"));
        tagMap.put(3L, List.of("Tag3"));
        given(postTagRepository.findAllTagListWithPostPublishedId()).willReturn(tagMap);

        // Mongo Data
        PostMongo mongoPost2 = PostMongo.builder()
                .postId(2L)
                .title("Old Title")
                .plainContent(SyncManager.toPlainText("Content2"))
                .contentHash(SyncManager.makeHashCodeToContent("Content2"))
                .tagList(List.of("Tag2"))
                .syncedDate(LocalDateTime.now())
                .build();
        
        PostMongo mongoPost3 = PostMongo.builder()
                .postId(3L)
                .title("Unchanged")
                .plainContent(SyncManager.toPlainText("Content3"))
                .contentHash(SyncManager.makeHashCodeToContent("Content3"))
                .tagList(List.of("Tag3"))
                .syncedDate(LocalDateTime.now())
                .build();

        PostMongo mongoPost4 = PostMongo.builder()
                .postId(4L).title("Deleted").build();

        List<PostMongo> mongoPosts = List.of(mongoPost2, mongoPost3, mongoPost4);
        given(postMongoRepository.getAll()).willReturn(mongoPosts);

        // when
        TriggerResponseDto response = syncService.triggerSync();

        // then
        assertThat(response.getInsertCount()).isEqualTo(1);
        assertThat(response.getUpdateCount()).isEqualTo(1);
        assertThat(response.getDeleteCount()).isEqualTo(1);
        
        verify(postMongoRepository).insertAll(anyList());
        verify(postMongoRepository).updateAll(anyList());
        verify(postMongoRepository).deleteAll(anyList());
    }
}
