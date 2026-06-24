package HeoJin.demoBlog.tag.service;

import HeoJin.demoBlog.global.exception.refactor.NotFoundException;
import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.tag.dto.request.AddTagDtoRequest;
import HeoJin.demoBlog.tag.dto.request.DeleteTagDtoRequest;
import HeoJin.demoBlog.tag.dto.request.ListAddTagRequestDto;
import HeoJin.demoBlog.tag.dto.request.ListDeleteTagRequest;
import HeoJin.demoBlog.tag.dto.response.ListTagResponse;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminTagServiceTest {

    @InjectMocks
    private AdminTagService adminTagService;
    @Mock
    private TagManager tagManager;
    @Mock
    private PostTagRepository postTagRepository;
    @Mock
    private PostRepository postRepository;

    @Test
    @DisplayName("태그 추가 - 성공")
    void test1() {
        // given
        Long postId = 1L;
        AddTagDtoRequest addRequest = AddTagDtoRequest.builder()
                .tagName("Java")
                .build();
        ListAddTagRequestDto requestDto = new ListAddTagRequestDto(List.of(addRequest), postId);

        given(postRepository.existsById(postId)).willReturn(true);
        given(postTagRepository.getTagListWithPostId(postId)).willReturn(Collections.emptyList());

        // when
        ListTagResponse response = adminTagService.addTagPost(requestDto);

        // then
        verify(tagManager).addTagPost("Java", postId);
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("태그 추가 - 포스트 없음 예외")
    void test2() {
        // given
        ListAddTagRequestDto requestDto = new ListAddTagRequestDto(Collections.emptyList(), 1L);
        given(postRepository.existsById(1L)).willReturn(false);

        // when + then
        assertThatThrownBy(() -> adminTagService.addTagPost(requestDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("태그 삭제 - 성공")
    void test3() {
        // given
        Long postId = 1L;
        DeleteTagDtoRequest deleteRequest = DeleteTagDtoRequest.builder()
                .tagName("Java")
                .build();
        ListDeleteTagRequest requestDto = new ListDeleteTagRequest(List.of(deleteRequest), postId);

        given(postRepository.existsById(postId)).willReturn(true);
        given(postTagRepository.getTagListWithPostId(postId)).willReturn(Collections.emptyList());

        // when
        ListTagResponse response = adminTagService.deleteTag(requestDto);

        // then
        verify(tagManager).deleteTagPost("Java", postId);
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("태그 삭제 - 포스트 없음 예외")
    void test5() {
        // given
        ListDeleteTagRequest requestDto = new ListDeleteTagRequest(Collections.emptyList(), 1L);
        given(postRepository.existsById(1L)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> adminTagService.deleteTag(requestDto))
                .isInstanceOf(NotFoundException.class);
    }
}
