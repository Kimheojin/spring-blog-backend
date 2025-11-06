package HeoJin.demoBlog.Post.controller.doc;

import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import HeoJin.demoBlog.configuration.Integration.ApiDocTestSetup;
import HeoJin.demoBlog.configuration.mockUser.WithMockCustomUser;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.post.dto.request.PostDeleteRequest;
import HeoJin.demoBlog.post.dto.request.PostModifyRequest;
import HeoJin.demoBlog.post.dto.request.PostRequest;
import HeoJin.demoBlog.post.dto.request.TagRequest;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.entity.PostStatus;
import HeoJin.demoBlog.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostWriteTestApiDocTest extends ApiDocTestSetup {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void init() {
        Member member = createTestMember();
        saveAllCategories();
        saveAllPosts(member);
        saveAllTag();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("post /api/admin/posts -> 게시글 작성")
    void test1() throws Exception {
        // given
        Category category = categoryRepository.findByCategoryName("Java1")
                .orElseThrow(() -> new AssertionError("테스트 초기화 데이터 오류"));

        TagRequest tagRequest = TagRequest.builder()
                .tagName("hello")
                .build();

        PostRequest request = PostRequest.builder()
                .title("새로운 게시글 제목")
                .content("새로운 게시글 내용입니다.")
                .categoryName(category.getCategoryName())
                .postStatus(PostStatus.PUBLISHED)
                .tagList(List.of(tagRequest))
                .build();

        // when + then
        ResultActions testMock = mockMvc.perform(post("/api/admin/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        // docs
        testMock.andDo(document("post-/api/admin/posts",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("title").description("게시글 제목"),
                        fieldWithPath("content").description("게시글 내용"),
                        fieldWithPath("categoryName").description("카테고리 ID"),
                        fieldWithPath("postStatus").description("게시글 상태 (PUBLISHED, PRIVATE)"),
                        fieldWithPath("tagList").description("해당 게시물 태그 리스트"),
                        fieldWithPath("tagList[].tagName").description("해당 게시물 태그 이름")
                ),
                responseFields(
                        fieldWithPath("title").description("제목"),
                        fieldWithPath("regDate").description("저장 날짜")
                )));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("put /api/admin/posts -> 게시글 수정")
    void test2() throws Exception {
        // given
        Post existingPost = postRepository.findAll().get(0);
        Category category = categoryRepository.findByCategoryName("Java2")
                .orElseThrow(() -> new AssertionError("테스트 초기화 데이터 오류"));

        TagRequest tagRequest1 = TagRequest.builder()
                .tagName("tesst1")
                .build();

        TagRequest tagRequest2 = TagRequest.builder()
                .tagName("tesst1")
                .build();

        PostModifyRequest request = PostModifyRequest.builder()
                .postId(existingPost.getId())
                .title("수정된 게시글 제목")
                .content("수정된 게시글 내용입니다.")
                .categoryName(category.getCategoryName())
                .postStatus(PostStatus.PRIVATE)
                .tagList(List.of(tagRequest1, tagRequest2))
                .build();

        // when + then
        ResultActions testMock = mockMvc.perform(put("/api/admin/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        // docs
        testMock.andDo(document("put-/api/admin/posts",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("postId").description("수정할 게시글 ID"),
                        fieldWithPath("title").description("수정할 게시글 제목"),
                        fieldWithPath("content").description("수정할 게시글 내용"),
                        fieldWithPath("categoryName").description("수정할 카테고리 ID"),
                        fieldWithPath("tagList").description("수정할 태그 리스트"),
                        fieldWithPath("tagList[].tagName").description("수정할 태그 이름"),
                        fieldWithPath("postStatus").description("수정할 게시글 상태 (PUBLISHED, PRIVATE)")
                ),
                responseFields(
                        fieldWithPath("title").description("수정된 제목"),
                        fieldWithPath("regDate").description("저장 날짜")
                )));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("delete /api/admin/posts -> 게시글 삭제")
    void test3() throws Exception {
        // given
        Post existingPost = postRepository.findAll().get(0);

        PostDeleteRequest request = PostDeleteRequest.builder()
                .postId(existingPost.getId())
                .build();

        // when + then
        ResultActions testMock = mockMvc.perform(delete("/api/admin/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        // docs
        testMock.andDo(document("delete-/api/admin/posts",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("postId").description("삭제할 게시글 ID")
                ),
                responseFields(
                        fieldWithPath("message").description("삭제 완료 메시지")
                )));
    }
}