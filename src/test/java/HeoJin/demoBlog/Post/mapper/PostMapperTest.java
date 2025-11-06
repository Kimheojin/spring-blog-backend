package HeoJin.demoBlog.Post.mapper;

import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.post.dto.response.PagePostResponse;
import HeoJin.demoBlog.post.dto.response.PostContractionResponse;
import HeoJin.demoBlog.post.dto.response.PostResponse;
import HeoJin.demoBlog.post.dto.response.TagResponse;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.service.PostMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostMapperTest {

    @Test
    @DisplayName("toPagePostResponse -> Page + content -> PagePostResponse 변환")
    void test1() {
        // given
        List<PostResponse> content = Arrays.asList(
                PostResponse.builder()
                        .postId(1L)
                        .title("첫 번째 글")
                        .build(),
                PostResponse.builder()
                        .postId(2L)
                        .title("두 번째 글")
                        .build()
        );

        // Mock Page 생성도 가능
        // 기억하면 좋을듯
        Page<Post> mockPage = Mockito.mock(Page.class);
        Mockito.when(mockPage.getNumber()).thenReturn(0);
        Mockito.when(mockPage.getSize()).thenReturn(10);
        Mockito.when(mockPage.getTotalElements()).thenReturn(25L);
        Mockito.when(mockPage.getTotalPages()).thenReturn(3);
        Mockito.when(mockPage.isFirst()).thenReturn(true);
        Mockito.when(mockPage.isLast()).thenReturn(false);

        // when
        PagePostResponse result = PostMapper.toPagePostResponse(content, mockPage);

        // then
        Assertions.assertEquals(content, result.content());
        Assertions.assertEquals(0, result.pageNumber());
        Assertions.assertEquals(10, result.pageSize());
        Assertions.assertEquals(25L, result.totalElements());
        Assertions.assertEquals(3, result.totalPages());
        Assertions.assertTrue(result.first());
        Assertions.assertFalse(result.last());
    }

    @Test
    @DisplayName("toPostResponse -> Post를 PostResponse로 변환")
    void test2() {
        // given
        Member mockMember = Member.builder()
                .memberName("작성자")
                .build();

        Category mockCategory = Category.builder()
                .categoryName("Java")
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("테스트 포스트")
                .content("테스트 내용입니다")
                .member(mockMember)
                .category(mockCategory)
                .status(PostStatus.PUBLISHED)
                .regDate(LocalDateTime.now())
                .build();

        List<TagResponse> testResponseList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TagResponse testTagResponse = TagResponse.builder()
                    .tagId(0L)
                    .tagName("test" + i).build();
        }

        // when
        PostResponse result = PostMapper.toPostResponse(post, testResponseList);

        // then
        Assertions.assertEquals(post.getId(), result.getPostId());
        Assertions.assertEquals(post.getTitle(), result.getTitle());
        Assertions.assertEquals(post.getContent(), result.getContent());
        Assertions.assertEquals(post.getMember().getMemberName(), result.getMemberName());
        Assertions.assertEquals(post.getCategory().getCategoryName(), result.getCategoryName());
        Assertions.assertEquals(post.getStatus(), result.getStatus());
        Assertions.assertEquals(post.getRegDate(), result.getRegDate());
    }

    @Test
    @DisplayName("toPostResponse -> DRAFT 상태 Post 변환")
    void test3() {
        // given
        Member mockMember = Member.builder()
                .memberName("작성자")
                .build();

        Category mockCategory = Category.builder()
                .categoryName("Spring")
                .build();

        Post post = Post.builder()
                .id(2L)
                .title("임시저장 포스트")
                .content("임시저장 내용")
                .member(mockMember)
                .category(mockCategory)
                .status(PostStatus.DRAFT)
                .regDate(LocalDateTime.now())
                .build();


        List<TagResponse> testResponseList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TagResponse testTagResponse = TagResponse.builder()
                    .tagId(0L)
                    .tagName("test" + i).build();
        }

        // when
        PostResponse result = PostMapper.toPostResponse(post, testResponseList);

        // then
        Assertions.assertEquals(PostStatus.DRAFT, result.getStatus());
        Assertions.assertEquals("임시저장 포스트", result.getTitle());
    }

    @Test
    @DisplayName("toPostContractionResponse -> Post를 PostContractionResponse로 변환")
    void test4() {
        // given
        LocalDateTime regDate = LocalDateTime.now();

        Post post = Post.builder()
                .id(1L)
                .title("간단한 포스트 제목")
                .content("내용은 변환되지 않음")
                .regDate(regDate)
                .build();

        // when
        PostContractionResponse result = PostMapper.toPostContractionResponse(post);

        // then
        Assertions.assertEquals(post.getTitle(), result.getTitle());
        Assertions.assertEquals(post.getRegDate(), result.getRegDate());
    }
}
