package HeoJin.demoBlog.tag.service;


import HeoJin.demoBlog.global.exception.CustomNotFound;
import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.tag.dto.request.ListAddTagRequestDto;
import HeoJin.demoBlog.tag.dto.request.ListDeleteTagRequest;
import HeoJin.demoBlog.tag.dto.response.ListTagResponseDto;
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

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final TagRepository tagRepository;
    private final TagManager tagManager;


    @Transactional
    public void addTagPost(ListAddTagRequestDto listAddTagRequestDto) {
        // 해당 post 값
        Long postId = listAddTagRequestDto.postId();
        // 검증
        if (!postRepository.existsById(postId)) {
            throw new CustomNotFound("해당 post가 존재하지 않습니다.");
        }

        listAddTagRequestDto.DtoList().forEach(
                addTagDtoRequest
                        -> tagManager.addTagPost(addTagDtoRequest.getTagName(), postId)
        );

    }

    // 태그 삭제 메소드
    @Transactional
    public void deleteTag(ListDeleteTagRequest listDeleteTagRequest) {

        long postId = listDeleteTagRequest.postId();
        // 검증
        if(!postRepository.existsById(postId)) {
            throw new CustomNotFound("해당 post 가 존재하지 않습니다,");
        }

        listDeleteTagRequest.DtoList().forEach(
                deleteTagDtoRequest
                        -> tagManager.deleteTagPost(deleteTagDtoRequest.getTagName(), postId)
        );

    }



    @Transactional
    public ListTagResponseDto getTagList() {

        List<TagResponseDto> tagDtos = postTagRepository.getCountWithTagId();
        return new ListTagResponseDto(tagDtos);
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
