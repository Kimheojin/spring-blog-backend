package HeoJin.demoBlog.tag.service;

import HeoJin.demoBlog.global.exception.refactor.NotFoundException;
import HeoJin.demoBlog.post.dto.request.TagRequest;
import HeoJin.demoBlog.post.dto.response.TagResponse;
import HeoJin.demoBlog.tag.entity.PostTag;
import HeoJin.demoBlog.tag.entity.Tag;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
import HeoJin.demoBlog.tag.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.anyList;

@ExtendWith(MockitoExtension.class)
class TagManagerTest {

    @InjectMocks
    private TagManager tagManager;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private PostTagRepository postTagRepository;

    @Test
    @DisplayName("태그 추가 - 기존 태그 존재 시 동작 검증")
    void test1() {
        // given
        String tagName = "Java";
        Long postId = 1L;
        Tag tag = Tag.builder()
                .id(100L)
                .tagName(tagName)
                .build();

        given(tagRepository.findByTagName(tagName))
                .willReturn(Optional.of(tag));

        // when
        tagManager.addTagPost(tagName, postId);

        // then
        // verify 행동 검증 (중복 저장 하는 지)
        verify(tagRepository, never()).save(any(Tag.class));
        verify(postTagRepository).save(any(PostTag.class));

    }

    @Test
    @DisplayName("태그 추가 - 새로운 태그 생성")
    void test2() {
        // given
        String tagName = "Java";
        Long postId = 1L;
        Tag newTag = Tag
                .builder()
                .id(100L)
                .tagName(tagName).build();

        given(tagRepository.findByTagName(tagName))
                .willReturn(Optional.empty());
        given(tagRepository.save(any(Tag.class)))
                .willReturn(newTag);

        // when
        tagManager.addTagPost(tagName, postId);

        // then
        verify(tagRepository).save(any(Tag.class));
        verify(postTagRepository).save(any(PostTag.class));
    }

    @Test
    @DisplayName("태그 삭제 - 태그 없음 예외")
    void test3() {
        // given
        given(tagRepository.findByTagName("Java"))
                .willReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() ->
                tagManager.deleteTagPost("Java", 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("태그 삭제 - 연결 해제 및 오르판 태그 삭제 확인")
    void test4() {
        // given
        String tagName = "Java";
        Long postId = 1L;
        Tag tag = Tag
                .builder()
                .id(100L)
                .tagName(tagName)
                .build();

        given(tagRepository.findByTagName(tagName))
                .willReturn(Optional.of(tag));

        given(postTagRepository.existsByTagId(tag.getId()))
                .willReturn(false);

        // when
        tagManager.deleteTagPost(tagName, postId);

        // then
        verify(postTagRepository).deleteByPostIdAndTagId(postId, tag.getId());
        verify(tagRepository).deleteById(tag.getId());
    }

    @Test
    @DisplayName("태그 삭제 - 연결 해제만 (다른 포스트 사용 중)")
    void test5() {
        // given
        String tagName = "Java";
        Long postId = 1L;
        Tag tag = Tag.builder()
                .id(100L)
                .tagName(tagName)
                .build();

        given(tagRepository.findByTagName(tagName))
                .willReturn(Optional.of(tag));
        given(postTagRepository.existsByTagId(tag.getId()))
                .willReturn(true);

        // when
        tagManager.deleteTagPost(tagName, postId);

        // then
        verify(postTagRepository).deleteByPostIdAndTagId(postId, tag.getId());
        verify(tagRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("포스트별 태그 전체 삭제")
    void test6() {
        // given
        Long postId = 1L;
        PostTag postTag = PostTag.builder()
                .tagId(100L)
                .postId(postId)
                .build();

        given(postTagRepository.findAllByPostId(postId))
                .willReturn(List.of(postTag));
        given(postTagRepository.existsByTagId(100L))
                .willReturn(false); // Orphan

        // when
        tagManager.deleteTagByPostId(postId);

        // then
        verify(postTagRepository).deleteAllByPostId(postId);
        verify(tagRepository).deleteAllByIdIn(anyList());
    }

    @Test
    @DisplayName("태그 목록 수정")
    void test7() {
        // given
        Long postId = 1L;

        TagResponse oldTag1 = new TagResponse("Java", 100L);
        TagResponse oldTag2 = new TagResponse("Spring", 101L);

        given(postTagRepository.getTagListWithPostId(postId))
                .willReturn(List.of(oldTag1, oldTag2));

        TagRequest reqSpring = mock(TagRequest.class);
        given(reqSpring.getTagName())
                .willReturn("Spring");

        TagRequest reqDocker = mock(TagRequest.class);
        given(reqDocker.getTagName())
                .willReturn("Docker");
        
        List<TagRequest> newTags = List.of(reqSpring, reqDocker);


        given(tagRepository.findByTagName("Docker"))
                .willReturn(Optional.empty());

        Tag javaTag = Tag.builder()
                .id(100L)
                .tagName("Java")
                .build();

        given(tagRepository.findByTagName("Java"))
                .willReturn(Optional.of(javaTag));
        
        // when
        tagManager.modifyTagList(newTags, postId);

        // then
        verify(tagRepository).save(any(Tag.class));
        verify(postTagRepository).deleteByPostIdAndTagId(eq(postId), eq(100L));

    }
}
