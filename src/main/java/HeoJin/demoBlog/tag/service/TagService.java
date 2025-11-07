package HeoJin.demoBlog.tag.service;


import HeoJin.demoBlog.global.exception.CustomNotFound;
import HeoJin.demoBlog.tag.dto.response.ListTagDtoResponseDto;
import HeoJin.demoBlog.tag.dto.response.PageTagPostResponse;
import HeoJin.demoBlog.tag.dto.response.PostTagResponseDto;
import HeoJin.demoBlog.tag.dto.response.TagResponseDto;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
import HeoJin.demoBlog.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final PostTagRepository postTagRepository;
    private final TagRepository tagRepository;


    @Transactional(readOnly = true)
    public ListTagDtoResponseDto getTagList() {

        List<TagResponseDto> tagDtos = postTagRepository.getCountWithTagId();
        return new ListTagDtoResponseDto(tagDtos);
    }



    @Transactional(readOnly = true)
    public PageTagPostResponse reaTagPostList(String tagName,Long tagId, int page, int pageSize) {
        // 검증
        tagRepository.findById(tagId)
                .filter(tag -> tagName.equals(tag.getTagName()))
                .orElseThrow(() -> new CustomNotFound("해당 tag 가 존재하지 않습니다."));
        // pageable 객체 생성
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<PostTagResponseDto> postPage = postTagRepository.findPublishedPostWithTag(tagId, pageable);

        return PageTagPostResponse.fromPage(postPage);
    }
}
