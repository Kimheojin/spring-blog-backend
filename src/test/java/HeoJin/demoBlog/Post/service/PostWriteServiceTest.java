package HeoJin.demoBlog.Post.service;

import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.repository.MemberRepository;
import HeoJin.demoBlog.post.dto.request.PostDeleteRequest;
import HeoJin.demoBlog.post.dto.request.PostModifyRequest;
import HeoJin.demoBlog.post.dto.request.PostRequest;
import HeoJin.demoBlog.post.dto.request.ScheduledPostRequest;
import HeoJin.demoBlog.post.dto.response.PostContractionResponse;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.post.service.PostWriteService;
import HeoJin.demoBlog.tag.service.TagManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostWriteServiceTest {

    @InjectMocks
    private PostWriteService postWriteService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TagManager tagManager;

    @Test
    @DisplayName("게시글 작성 성공")
    void test1() {
        // given
        Long memberId = 1L;
        String categoryName = "Tech";
        
        Member member = Member.builder().id(memberId).build();
        Category category = Category.builder().categoryName(categoryName).build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(categoryRepository.findByCategoryName(any())).willReturn(Optional.of(category));

        PostRequest postRequest = PostRequest.builder()
                .title("Test Title")
                .content("Content")
                .categoryName(categoryName)
                .postStatus(PostStatus.PUBLISHED)
                .tagList(Collections.emptyList())
                .build();

        // when
        PostContractionResponse response = postWriteService.writePost(memberId, postRequest);

        // then
        verify(postRepository).save(any(Post.class));
        verify(memberRepository).findById(memberId);
        verify(categoryRepository).findByCategoryName(any());
        assertThat(response.getTitle()).isEqualTo("Test Title");
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void test3() {
        // given
        Long postId = 1L;
        Long oldCategoryId = 10L;
        Long newCategoryId = 20L;
        String newTitle = "Updated Title";
        String newContent = "Updated Content";
        String newCategoryName = "Updated Category";

        PostModifyRequest request = PostModifyRequest.builder()
                .postId(postId)
                .title(newTitle)
                .content(newContent)
                .categoryName(newCategoryName)
                .postStatus(PostStatus.PUBLISHED)
                .tagList(Collections.emptyList())
                .build();

        Category oldCategory = Category.builder()
                .id(oldCategoryId)
                .categoryName("Old Category")
                .build();

        Category newCategory = Category.builder()
                .id(newCategoryId)
                .categoryName(newCategoryName)
                .build();

        Post post = Post.builder()
                .id(postId)
                .title("Old Title")
                .content("Old Content")
                .category(oldCategory)
                .status(PostStatus.PUBLISHED)
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(categoryRepository.findByCategoryName(newCategoryName)).willReturn(Optional.of(newCategory));

        // when
        PostContractionResponse response = postWriteService.updatePost(request);

        // then
        assertThat(response.getTitle()).isEqualTo(newTitle);
        assertThat(post.getTitle()).isEqualTo(newTitle);
        assertThat(post.getContent()).isEqualTo(newContent);
        assertThat(post.getCategory().getId()).isEqualTo(newCategoryId);

        verify(postRepository).findById(postId);
        verify(categoryRepository).findByCategoryName(newCategoryName);
        verify(categoryRepository).syncPostCounts(oldCategoryId);
        verify(categoryRepository).syncPostCounts(newCategoryId);
        verify(tagManager).modifyTagList(any(), eq(postId));
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void test2() {
        // given
        Long postId = 1L;
        Long categoryId = 10L;
        PostDeleteRequest request = new PostDeleteRequest(postId);

        Category category = Category.builder()
                .id(categoryId)
                .build();

        Post post = Post.builder()
                .id(postId)
                .category(category)
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        postWriteService.deletePost(request);

        // then
        verify(postRepository).findById(postId);
        verify(tagManager).deleteTagByPostId(postId);
        verify(postRepository).delete(post);
        verify(categoryRepository).syncPostCounts(categoryId);
    }
    
    @Test
    @DisplayName("예약 게시글 작성 성공")
    void test5() {
        // given
        Long memberId = 1L;
        LocalDateTime scheduleTime = LocalDateTime.now().plusDays(1);
        ScheduledPostRequest request = new ScheduledPostRequest(
            "Scheduled Title",
            "Content",
            "Tech",
            scheduleTime,
            Collections.emptyList()
        );
        
        Member member = Member.builder().id(memberId).build();
        Category category = Category.builder().categoryName("Tech").build();
        
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(categoryRepository.findByCategoryName("Tech")).willReturn(Optional.of(category));

        // when
        PostContractionResponse response = postWriteService.schedulePost(memberId, request);
        
        // then
        verify(postRepository).save(any(Post.class));
        assertThat(response.getTitle()).isEqualTo("Scheduled Title");
    }
}
