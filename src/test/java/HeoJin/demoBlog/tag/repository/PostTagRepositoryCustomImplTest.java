package HeoJin.demoBlog.tag.repository;

import HeoJin.demoBlog.configuration.InitRepository.TestInitRepository;
import HeoJin.demoBlog.global.config.QuerydslConfig;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.tag.dto.response.PostTagResponseDto;
import HeoJin.demoBlog.tag.dto.response.TagResponseDto;
import HeoJin.demoBlog.tag.entity.Tag;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class, TestInitRepository.class})
class PostTagRepositoryCustomImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private PostTagRepository postTagRepository;

    @Autowired
    private TestInitRepository testInitRepository;

    private Tag tag1, tag2;
    private Post post1, post2;

    @BeforeEach
    void setUp() {
        // 간단하게
        tag1 = Tag.builder().tagName("tag1").build();
        tag2 = Tag.builder().tagName("tag2").build();
        em.persist(tag1);
        em.persist(tag2);

        post1 = Post.builder().title("post1").content("content1").regDate(LocalDateTime.now())
                .status(PostStatus.PUBLISHED).build();
        post2 = Post.builder().title("post1").content("content1").regDate(LocalDateTime.now())
                .status(PostStatus.PUBLISHED).build();
        em.persist(post1);
        em.persist(post2);
        testInitRepository.createPostTagLink(post1.getId(), tag1.getId());
        testInitRepository.createPostTagLink(post2.getId(), tag2.getId());

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("정상동작 +  태그별 포스트 개수")
    void test1() {
        // given
        
        // When
        List<TagResponseDto> result = postTagRepository.getCountWithTagId();

        // Then
        assertThat(result).hasSize(2);

        TagResponseDto tag1Result = result.stream().filter(r ->
                r.getTagName().equals("tag1")).findFirst().orElse(null);
        assertThat(tag1Result).isNotNull();
        assertThat(tag1Result.getCount()).isEqualTo(1);

        TagResponseDto tag2Result = result.stream().filter(r ->
                r.getTagName().equals("tag2")).findFirst().orElse(null);
        assertThat(tag2Result).isNotNull();
        assertThat(tag2Result.getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("페이징 + tagId 를 통한 조회")
    void test2() {
        // Given
        Tag persistedTag1 = em.createQuery("select t from Tag t where t.tagName = 'tag1'", Tag.class).getSingleResult();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<PostTagResponseDto> result = postTagRepository.findPublishedPostWithTag(persistedTag1.getId(), pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().stream().map(PostTagResponseDto::getTitle)).containsExactlyInAnyOrder("post1");
    }


}