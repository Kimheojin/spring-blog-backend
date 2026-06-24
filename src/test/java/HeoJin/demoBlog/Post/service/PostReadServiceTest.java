package HeoJin.demoBlog.Post.service;

import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.post.dto.response.PagePostResponse;
import HeoJin.demoBlog.post.dto.response.PostResponse;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.post.service.PostReadService;
import HeoJin.demoBlog.tag.dto.data.PostIdWithTagDto;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
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
class PostReadServiceTest {

    @InjectMocks
    private PostReadService postReadService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private PostTagRepository postTagRepository;

    @Test
    @DisplayName("페이징된 게시글 목록 조회 성공")
    void test1() {
        // given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Member member = Member.builder().memberName("testUser").build();
        Category category = Category.builder().categoryName("General").build();
        Post post = Post.builder()
                .id(1L)
                .title("Test Post")
                .content("Content")
                .member(member)
                .category(category)
                .status(PostStatus.PUBLISHED)
                .regDate(LocalDateTime.now())
                .build();

        List<Post> posts = Collections.singletonList(post);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        given(postRepository.findPublishedPostsWithFetch(any(Pageable.class))).willReturn(postPage);


        PostIdWithTagDto tagDto = PostIdWithTagDto.builder()
                .postId(1L)
                .tagId(100L)
                .tagName("Java")
                .build();
        given(postTagRepository.getTagListWithPostIdList(anyList())).willReturn(List.of(tagDto));

        // when
        PagePostResponse response = postReadService.readPagedPosts(page, size);

        // then
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).getTitle()).isEqualTo("Test Post");
        assertThat(response.content().get(0).getTagList()).hasSize(1);
        assertThat(response.content().get(0).getTagList().get(0).getTagName()).isEqualTo("Java");
        
        verify(postRepository).findPublishedPostsWithFetch(any(Pageable.class));
        verify(postTagRepository).getTagListWithPostIdList(anyList());
    }

    @Test
    @DisplayName("카테고리별 게시글 목록 조회 성공")
    void test2() {
        // given
        String categoryName = "General";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Category category = Category.builder().categoryName(categoryName).build();

        given(categoryRepository.findByCategoryName(categoryName)).willReturn(Optional.of(category));

        Member member = Member.builder()
                .memberName("testUser")
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("Test Post")
                .content("Content")
                .member(member)
                .category(category)
                .status(PostStatus.PUBLISHED)
                .regDate(LocalDateTime.now())
                .build();

        List<Post> posts = Collections.singletonList(post);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        given(postRepository.findPublishedCategoryWithFetch(eq(categoryName), any(Pageable.class))).willReturn(postPage);
        given(postTagRepository.getTagListWithPostIdList(anyList())).willReturn(Collections.emptyList());

        // when
        PagePostResponse response = postReadService.readPagingCategoryPosts(categoryName, page, size);

        // then
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).getCategoryName()).isEqualTo(categoryName);
        verify(categoryRepository).findByCategoryName(categoryName);
        verify(postRepository).findPublishedCategoryWithFetch(eq(categoryName), any(Pageable.class));
    }

    @Test
    @DisplayName("단일 게시글 조회 성공")
    void test3() {
        // given
        Long postId = 1L;
        Member member = Member.builder().memberName("testUser").build();
        Category category = Category.builder().categoryName("General").build();
        Post post = Post.builder()
                .id(postId)
                .title("Test Post")
                .content("Content")
                .member(member)
                .category(category)
                .status(PostStatus.PUBLISHED)
                .regDate(LocalDateTime.now())
                .build();

        given(postRepository.findPublishedWithPostId(postId)).willReturn(Optional.of(post));
        given(postTagRepository.getTagListWithPostId(postId)).willReturn(Collections.emptyList());

        // when
        PostResponse response = postReadService.getSinglePost(postId);

        // then
        assertThat(response.getPostId()).isEqualTo(postId);
        assertThat(response.getTitle()).isEqualTo("Test Post");
        verify(postRepository).findPublishedWithPostId(postId);
        verify(postTagRepository).getTagListWithPostId(postId);
    }
}
