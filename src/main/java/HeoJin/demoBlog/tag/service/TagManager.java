package HeoJin.demoBlog.tag.service;

import HeoJin.demoBlog.global.exception.CustomNotFound;
import HeoJin.demoBlog.post.dto.request.TagRequest;
import HeoJin.demoBlog.post.dto.response.TagResponse;
import HeoJin.demoBlog.tag.entity.PostTag;
import HeoJin.demoBlog.tag.entity.Tag;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
import HeoJin.demoBlog.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Component
public class TagManager {

    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    public void addTagPost(String tagName, Long postId){
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

    public void deleteTagPost(String tagName, Long postId) {
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

    @Transactional
    public void deleteTagByPostId(Long postId){
        List<PostTag> postTags = postTagRepository.findAllByPostId(postId);
        if (postTags.isEmpty()) {
            return;
        }
        List<Long> tagIds = postTags.stream()
                .map(PostTag::getTagId)
                .toList();

        postTagRepository.deleteAllByPostId(postId);

        List<Long> dangleTagIds = tagIds.stream()
                .filter(tagId -> !postTagRepository.existsByTagId(tagId))
                .collect(Collectors.toList());

        if (!dangleTagIds.isEmpty()) {
            tagRepository.deleteAllByIdIn(dangleTagIds);
        }
    }

    public void modifyTagList(List<TagRequest> tagList, Long postId) {
        // 기존 태그
        Set<String> oldTagNames = postTagRepository.getTagListWithPostId(postId)
                .stream()
                .map(TagResponse::getTagName)
                .collect(Collectors.toSet());
        // 새로운 태그
        Set<String> newTagNames = tagList.stream()
                .map(TagRequest::getTagName)
                .collect(Collectors.toSet());


        newTagNames.stream()
                .filter(tagName -> !oldTagNames.contains(tagName))
                .forEach(tagName -> addTagPost(tagName, postId));

        oldTagNames.stream()
                .filter(tagName -> !newTagNames.contains(tagName))
                .forEach(tagName -> deleteTagPost(tagName, postId));

    }
}
