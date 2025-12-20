package HeoJin.demoBlog.post.service;


import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.global.exception.refactor.NotFoundException;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import HeoJin.demoBlog.post.dto.response.PagePostResponse;
import HeoJin.demoBlog.post.dto.response.TagResponse;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.post.dto.response.PostResponse;
import HeoJin.demoBlog.tag.dto.data.PostIdWithTagDto;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostReadService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final PostTagRepository postTagRepository;

    @Transactional(readOnly = true)
    public PagePostResponse readPagedPosts(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findPublishedPostsWithFetch(pageable);
        List<Long> postIds = postPage.getContent().stream()
                .map(Post::getId)
                .toList();
        if(postIds.isEmpty()){
            return PostMapper.toPagePostResponse(List.of(), postPage);
        }

        List<PostIdWithTagDto> tagListWithPostIds = postTagRepository.getTagListWithPostIdList(postIds);

        Map<Long, List<PostIdWithTagDto>> tagsByPostId = tagListWithPostIds.stream()
                .collect(Collectors.groupingBy(PostIdWithTagDto::getPostId));



        List<PostResponse> postResponses = postPage.getContent()
                .stream()
                .map(post -> {
                    List<PostIdWithTagDto> tags = tagsByPostId.getOrDefault(post.getId(), Collections.emptyList());
                    List<TagResponse> tagResponses = tags.stream()
                            .map(tag -> new TagResponse(tag.getTagName(), tag.getTagId()))
                            .collect(Collectors.toList());
                    return PostMapper.toPostResponse(post, tagResponses);
                })
                .collect(Collectors.toList());

        return PostMapper.toPagePostResponse(postResponses, postPage);

    }

    @Transactional(readOnly = true)
    public PagePostResponse readPagingCategoryPosts(String categoryName,
                                                    int page, int size){
        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new NotFoundException("해당 이름의 카테고리를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size); // 프라이머리로
        Page<Post> postPage = postRepository
                .findPublishedCategoryWithFetch(category.getCategoryName(), pageable);
        List<Long> postIds = postPage.getContent().stream()
                .map(Post::getId)
                .toList();
        if(postIds.isEmpty()){
            return PostMapper.toPagePostResponse(List.of(), postPage);
        }

        List<PostIdWithTagDto> tagListWithPostIds = postTagRepository.getTagListWithPostIdList(postIds);
        Map<Long, List<PostIdWithTagDto>> tagsByPostId = tagListWithPostIds.stream()
                .collect(Collectors.groupingBy(PostIdWithTagDto::getPostId));

        List<PostResponse> postResponses = postPage.getContent()
                .stream()
                .map(post -> {
                    List<PostIdWithTagDto> tags = tagsByPostId.getOrDefault(post.getId(), Collections.emptyList());
                    List<TagResponse> tagResponses = tags.stream()
                            .map(tag -> new TagResponse(tag.getTagName(), tag.getTagId()))
                            .collect(Collectors.toList());
                    return PostMapper.toPostResponse(post, tagResponses);
                })
                .collect(Collectors.toList());

        return PostMapper.toPagePostResponse(postResponses, postPage);
    }

    @Transactional(readOnly = true)
    public PostResponse getSinglePost(Long postId) {
        Post post = postRepository.findPublishedWithPostId(postId)
                .orElseThrow(() -> new NotFoundException("해당 포스트를 찾을 수 없습니다."));

        List<TagResponse> tagListWithPostId = postTagRepository.getTagListWithPostId(post.getId());

        return PostMapper.toPostResponse(post, tagListWithPostId);
    }


}
