package HeoJin.demoBlog.tag.repository;

import HeoJin.demoBlog.configuration.InitRepository.TestInitRepository;
import HeoJin.demoBlog.global.config.QuerydslConfig;
import HeoJin.demoBlog.post.dto.response.TagResponse;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.tag.dto.data.PostIdWithTagDto;
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
import java.util.Map;

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

        post1 = Post.builder()
                .title("post1")
                .content("content1")
                .regDate(LocalDateTime.now())
                .status(PostStatus.PUBLISHED).build();

        post2 = Post.builder()
                .title("post2")
                .content("content1")
                .regDate(LocalDateTime.now())
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
                r.getTagName().equals("tag1"))
                .findFirst()
                .orElse(null);

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
        Tag persistedTag1 = em.createQuery("select t from Tag t where t.tagName = 'tag1'", Tag.class)
                .getSingleResult();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<PostTagResponseDto> result = postTagRepository.findPublishedPostWithTag(persistedTag1.getId(), pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().stream().map(PostTagResponseDto::getTitle)).containsExactlyInAnyOrder("post1");
    }

    @Test
    @DisplayName("postId 리스트로 태그 목록 조회")
    void test3() {
        // Given
        Long id1 = post1.getId();
        Long id2 = post2.getId();
        
        // When
        List<PostIdWithTagDto> result = postTagRepository.getTagListWithPostIdList(List.of(id1, id2));

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("tagName").contains("tag1", "tag2");
    }

    @Test
    @DisplayName("단일 postId로 태그 목록 조회")
    void test4() {
        // Given
        Long id1 = post1.getId();

        // When
        List<TagResponse> result = postTagRepository.getTagListWithPostId(id1);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTagName()).isEqualTo("tag1");
    }

    @Test
    @DisplayName("발행된 포스트의 모든 태그 맵 조회")
    void test5() {
        // Given
        
        // When
        Map<Long, List<String>> result = postTagRepository.findAllTagListWithPostPublishedId();

        // Then
        assertThat(result).containsKeys(post1.getId(), post2.getId());
        assertThat(result.get(post1.getId())).contains("tag1");
        assertThat(result.get(post2.getId())).contains("tag2");
    }
    
    @Test
    @DisplayName("발행되지 않은 포스트 제외 확인")
    void test6() {
        // Given
        Post draftPost = Post.builder().title("draft").content("draft").regDate(LocalDateTime.now())
                .status(PostStatus.DRAFT).build();
        em.persist(draftPost);

        Tag tag3 = Tag.builder().tagName("tag3").build();
        em.persist(tag3);

        testInitRepository.createPostTagLink(draftPost.getId(), tag3.getId());
        em.flush();
        em.clear();

        // When
        Map<Long, List<String>> result = postTagRepository.findAllTagListWithPostPublishedId();

        // Then
        assertThat(result).doesNotContainKey(draftPost.getId());
    }

}
