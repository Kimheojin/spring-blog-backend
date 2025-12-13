package HeoJin.demoBlog.post.service;


import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import HeoJin.demoBlog.global.exception.refactor.BusinessErrorCode;
import HeoJin.demoBlog.global.exception.refactor.BusinessException;
import HeoJin.demoBlog.global.exception.refactor.NotFoundException;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.repository.MemberRepository;
import HeoJin.demoBlog.post.dto.request.*;
import HeoJin.demoBlog.post.dto.response.PostContractionResponse;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
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
        // 제목 중복의 경우 Gelobal 처리
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("해당 회원을 찾을 수 없습니다."));


        // 카테고리 이미 존재 하는 지 안하는지 확인
        Category category = categoryRepository.findByCategoryName(postRequest.getCategoryName())
                .orElseThrow(() -> new NotFoundException("해당 카테고리를 찾을 수 없습니다."));

        if (postRequest.getPostStatus().equals("SCHEDULED")) {
            throw new BusinessException(BusinessErrorCode.INVALID_REQUEST, "예약 발행은 별도 API를 사용해야 합니다.");
        }

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
                .orElseThrow(() -> new NotFoundException("해당 Post가 존재하지 않습니다."));

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
                .orElseThrow(() -> new NotFoundException("해당 Post가 존재하지 않습니다."));
        tagManager.deleteTagByPostId(post.getId());
        postRepository.delete(post);


    }
    @Transactional
    public PostContractionResponse schedulePost(Long memberId, ScheduledPostRequest scheduledPostRequest){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("해당 회원을 찾을 수 없습니다."));

        Category category = categoryRepository.findByCategoryName(scheduledPostRequest.getCategoryName())
                .orElseThrow(() -> new NotFoundException("해당 카테고리를 찾을 수 없습니다."));


        Post scheduledPost = Post.builder()
                .title(scheduledPostRequest.getTitle())
                .member(member)
                .regDate(scheduledPostRequest.getRegDate()) // 현재 시간으로
                .content(scheduledPostRequest.getContent())
                .status(PostStatus.SCHEDULED)
                .category(category)
                .build();

        postRepository.save(scheduledPost);

        // 태그 관련
        scheduledPostRequest.getTagList()
                .forEach(tagRequest -> tagManager.addTagPost(tagRequest.getTagName(), scheduledPost.getId()));


        return PostMapper.toPostContractionResponse(scheduledPost);

    }
}
