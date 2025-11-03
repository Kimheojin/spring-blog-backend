package HeoJin.demoBlog.post.service;


import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import HeoJin.demoBlog.global.exception.CustomNotFound;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.repository.MemberRepository;
import HeoJin.demoBlog.post.dto.request.PostDeleteRequest;
import HeoJin.demoBlog.post.dto.request.PostModifyRequest;
import HeoJin.demoBlog.post.dto.request.PostRequest;
import HeoJin.demoBlog.post.dto.request.TagRequest;
import HeoJin.demoBlog.post.dto.response.PostContractionResponse;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.tag.service.TagManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostWriteService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final TagManager tagManager;


    @Transactional
    public PostContractionResponse writePost(Long memberId, PostRequest postRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomNotFound("회원"));


        // 카테고리 이미 존재 하는 지 안하는지 확인
        Category category = categoryRepository.findByCategoryName(postRequest.getCategoryName())
                .orElseThrow(() -> new CustomNotFound("카테고리"));

        Post newpost = Post.builder()
                .title(postRequest.getTitle())
                .member(member)
                .regDate(LocalDateTime.now(ZoneId.of("Asia/Seoul"))) // 현재 시간으로
                .content(postRequest.getContent())
                .status(postRequest.getPostStatus())
                .category(category)
                .build();

        postRepository.save(newpost);

        // 태그 관련

        postRequest.getTagList()
                .forEach(tagRequest -> tagManager.addTagPost(tagRequest.getTagName(), newpost.getId()));

        // 제목 + 등록 날짜
        return PostMapper.toPostContractionResponse(newpost);
    }


    // 게시글 수정
    @Transactional
    public PostContractionResponse updatePost(PostModifyRequest postModifyRequest) {
        // 변경감지로
        Post post = postRepository.findById(postModifyRequest.getPostId())
                .orElseThrow(() -> new CustomNotFound("해당 Post가 존재하지 않습니다."));

        post.updatePost(postModifyRequest.getTitle(),
                postModifyRequest.getContent(),
                postModifyRequest.getPostStatus());
        List<TagRequest> tagList = postModifyRequest.getTagList();
        tagManager.modifyTagList(tagList, post.getId());


        return PostMapper.toPostContractionResponse(post);
    }


    @Transactional
    public void deletePost(PostDeleteRequest postDeleteRequest) {
        Post post = postRepository.findById(postDeleteRequest.getPostId())
                .orElseThrow(() -> new CustomNotFound("해당 Post가 존재하지 않습니다."));
        tagManager.deleteTagByPostId(post.getId());
        postRepository.delete(post);


    }
}
