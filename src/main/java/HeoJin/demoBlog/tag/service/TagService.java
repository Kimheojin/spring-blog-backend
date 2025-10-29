package HeoJin.demoBlog.tag.service;


import HeoJin.demoBlog.global.exception.CustomNotFound;
import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.tag.dto.request.ListAddTagRequestDto;
import HeoJin.demoBlog.tag.dto.request.ListDeleteTagRequest;
import HeoJin.demoBlog.tag.dto.response.ListTagResponseDto;
import HeoJin.demoBlog.tag.dto.response.PageTagPostResponse;
import HeoJin.demoBlog.tag.dto.response.PostTagResponseDto;
import HeoJin.demoBlog.tag.dto.response.TagResponseDto;
import HeoJin.demoBlog.tag.entity.PostTag;
import HeoJin.demoBlog.tag.entity.Tag;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
import HeoJin.demoBlog.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final TagRepository tagRepository;


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
                        -> addTagPost(addTagDtoRequest.getTagName(), postId)
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
                        -> deleteTagPost(deleteTagDtoRequest.getTagName(), postId)
        );

    }



    @Transactional(readOnly = true)
    public ListTagResponseDto getTagList() {

        List<TagResponseDto> tagDtos = postTagRepository.getCountWithTagId();
        return new ListTagResponseDto(tagDtos);
    }

    private void addTagPost(String tagName, Long postId){
        Optional<Tag> byTagName = tagRepository.findByTagName(tagName);
        // 기존 tag 존재하는 경우 (postTag 연결 테이블만 추가)
        if (byTagName.isPresent()) {
            PostTag postTag = PostTag.builder()
                    .tagId(byTagName.get().getId())
                    .postId(postId)
                    .build();

            postTagRepository.save(postTag);

        }else {
            Tag newTag = Tag.builder()
                    .tagName(tagName)
                    .build();
            tagRepository.save(newTag);
            PostTag postTag = PostTag.builder()
                    .tagId(newTag.getId())
                    .postId(postId)
                    .build();
            postTagRepository.save(postTag);
        }
    }

    private void deleteTagPost(String tagName, Long postId) {
        Optional<Tag> byTagName = tagRepository.findByTagName(tagName);
        if(byTagName.isEmpty()){
            throw new CustomNotFound("해당 Tag entity 가 존재하지 않습니다,");

        } else {
            Long tagId = byTagName.get().getId();
            postTagRepository.deleteByPostIdAndTagId(postId, tagId);

            if (!postTagRepository.existsByTagId(tagId)){
                tagRepository.deleteById(tagId);
            }

        }
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
