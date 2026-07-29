package HeoJin.demoBlog.global.scheduler.post;

import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostSchedulerTest {

    private static final ZoneId TEST_ZONE = ZoneId.of("Asia/Seoul");
    private static final Instant TEST_INSTANT = Instant.parse("2026-07-29T20:00:00Z");

    private PostScheduler postScheduler;

    @Mock
    private PostRepository postRepository;
    @Mock
    private CategoryRepository categoryRepository;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(TEST_INSTANT, TEST_ZONE);
        postScheduler = new PostScheduler(postRepository, categoryRepository, clock);
        now = LocalDateTime.now(clock);
    }

    @Test
    @DisplayName("예약 발행 - 기준 시각 이전 게시글만 발행 상태로 변경")
    void test1() {
        // given
        Category category = Category.builder().id(1L).build();
        Post post = createPost(category, now.minusMinutes(1));

        given(postRepository.findByStatusAndRegDateBefore(PostStatus.SCHEDULED, now))
                .willReturn(List.of(post));

        // when
        postScheduler.publishScheduledPosts();

        // then
        assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(post.getRegDate()).isEqualTo(now);
        verify(categoryRepository).syncPostCounts(1L);
    }

    @Test
    @DisplayName("예약 발행 - 발행 대상이 없으면 카테고리 동기화 호출 없음")
    void test2() {
        // given
        given(postRepository.findByStatusAndRegDateBefore(PostStatus.SCHEDULED, now))
                .willReturn(List.of());

        // when
        postScheduler.publishScheduledPosts();

        // then
        verify(categoryRepository, never()).syncPostCounts(anyLong());
    }

    @Test
    @DisplayName("예약 발행 - 같은 카테고리의 여러 게시글은 카테고리 동기화 한 번만 호출")
    void test3() {
        // given
        Category category = Category.builder().id(1L).build();
        Post post1 = createPost(category, now.minusMinutes(10));
        Post post2 = createPost(category, now.minusMinutes(5));

        given(postRepository.findByStatusAndRegDateBefore(PostStatus.SCHEDULED, now))
                .willReturn(List.of(post1, post2));

        // when
        postScheduler.publishScheduledPosts();

        // then
        assertThat(post1.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(post2.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        verify(categoryRepository).syncPostCounts(1L);
    }

    private Post createPost(Category category, LocalDateTime regDate) {
        return Post.builder()
                .title("test title")
                .content("test content")
                .category(category)
                .regDate(regDate)
                .status(PostStatus.SCHEDULED)
                .build();
    }
}
