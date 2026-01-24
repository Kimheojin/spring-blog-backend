package HeoJin.demoBlog.Post.service;

import HeoJin.demoBlog.post.dto.response.PagePostResponse;
import HeoJin.demoBlog.post.dto.response.PostResponse;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.post.service.AdminPostReadService;
import HeoJin.demoBlog.tag.dto.data.PostIdWithTagDto;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.member.entity.Member;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminPostReadServiceTest {

    @InjectMocks
    private AdminPostReadService adminPostReadService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostTagRepository postTagRepository;

    @Test
    @DisplayName("관리자 단일 포스트 조회 성공")
    void test1() {
        // given
        Long postId = 1L;
        Member member = Member.builder().memberName("Admin").build();
        Category category = Category.builder().categoryName("AdminCat").build();
        Post post = Post.builder()
                .id(postId)
                .title("Admin Post")
                .content("Content")
                .member(member)
                .category(category)
                .status(PostStatus.PUBLISHED)
                .regDate(LocalDateTime.now())
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(postTagRepository.getTagListWithPostId(postId)).willReturn(Collections.emptyList());

        // when
        PostResponse response = adminPostReadService.getAdminSinglePost(postId);

        // then
        assertThat(response.getPostId()).isEqualTo(postId);
        assertThat(response.getTitle()).isEqualTo("Admin Post");
        verify(postRepository).findById(postId);
        verify(postTagRepository).getTagListWithPostId(postId);
    }

    @Test
    @DisplayName("관리자 포스트 목록 조회 성공")
    void test3() {
        // given
        String categoryName = "Tech";
        PostStatus status = PostStatus.PUBLISHED;
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Member member = Member.builder().memberName("Admin").build();
        Category category = Category.builder().categoryName(categoryName).build();
        Post post = Post.builder()
                .id(1L)
                .title("Admin List Post")
                .content("Content")
                .member(member)
                .category(category)
                .status(status)
                .regDate(LocalDateTime.now())
                .build();

        List<Post> posts = Collections.singletonList(post);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        given(postRepository.findPostsWithFilters(eq(categoryName), eq(status), any(Pageable.class)))
                .willReturn(postPage);
        


        PostIdWithTagDto tagDto = PostIdWithTagDto.builder()
                .postId(1L)
                .tagName("Tag1")
                .tagId(100L)
                .build();
        given(postTagRepository.getTagListWithPostIdList(anyList())).willReturn(List.of(tagDto));

        // when
        PagePostResponse response = adminPostReadService.readAdminPosts(categoryName, status, page, size);

        // then
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).getTitle()).isEqualTo("Admin List Post");
        assertThat(response.content().get(0).getTagList()).hasSize(1);
        verify(postRepository).findPostsWithFilters(eq(categoryName), eq(status), any(Pageable.class));
        verify(postTagRepository).getTagListWithPostIdList(anyList());
    }
}
