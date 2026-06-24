package HeoJin.demoBlog.tag.service;

import HeoJin.demoBlog.global.exception.refactor.NotFoundException;
import HeoJin.demoBlog.tag.dto.response.ListTagDtoResponseDto;
import HeoJin.demoBlog.tag.dto.response.PageTagPostResponse;
import HeoJin.demoBlog.tag.dto.response.PostTagResponseDto;
import HeoJin.demoBlog.tag.dto.response.TagResponseDto;
import HeoJin.demoBlog.tag.entity.Tag;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
import HeoJin.demoBlog.tag.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @InjectMocks
    private TagService tagService;
    @Mock
    private PostTagRepository postTagRepository;
    @Mock
    private TagRepository tagRepository;

    @Test
    @DisplayName("태그 목록 조회 정상 동작")
    void test1() {
        // given
        TagResponseDto dto = TagResponseDto.builder()
                .count(10L)
                .tagId(1L)
                .tagName("java")
                .build();
        given(postTagRepository.getCountWithTagId()).willReturn(List.of(dto));

        // when
        ListTagDtoResponseDto response = tagService.getTagList();

        // then
        assertThat(response.tagResponseDtoList()).hasSize(1);
    }

    @Test
    @DisplayName("태그별 포스트 목록 조회 - 성공")
    void test2() {
        // given
        String tagName = "Java";
        Long tagId = 1L;
        int page = 0;
        int size = 10;
        
        Tag tag = Tag.builder()
                .id(tagId)
                .tagName(tagName)
                .build();

        given(tagRepository.findById(tagId))
                .willReturn(Optional.of(tag));

        List<PostTagResponseDto> content = Collections.emptyList();
        Page<PostTagResponseDto> postPage = new PageImpl<>(content, PageRequest.of(page, size), 0);

        given(postTagRepository.findPublishedPostWithTag(eq(tagId), any(Pageable.class)))
                .willReturn(postPage);

        // when
        PageTagPostResponse response = tagService
                .reaTagPostList(tagName, tagId, page, size);

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("태그별 포스트 목록 조회 - 태그 불일치/없음 예외")
    void test3() {
        // given
        Long tagId = 1L;
        given(tagRepository.findById(tagId))
                .willReturn(Optional.empty());

        // when + then
        assertThatThrownBy(()
                -> tagService.reaTagPostList("Java", tagId, 0, 10))
                .isInstanceOf(NotFoundException.class);
    }
}
