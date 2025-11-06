package HeoJin.demoBlog.comment.controller.doc;

import HeoJin.demoBlog.comment.dto.request.CommentAdminDeleteRequest;
import HeoJin.demoBlog.comment.entity.Comment;
import HeoJin.demoBlog.comment.repository.CommentRepository;
import HeoJin.demoBlog.configuration.Integration.ApiDocTestSetup;
import HeoJin.demoBlog.configuration.mockUser.WithMockCustomUser;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminCommentTestApiDocTest extends ApiDocTestSetup {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void init() {
        Member member = createTestMember();
        saveAllCategories();
        saveAllPosts(member);
        saveAllComments();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("get /api/admin/posts/{postId}/comments -> 해단 post 관리자 전체 댓글 조회")
    void test1() throws Exception {
        // given
        Post existingPost = postRepository.findAll().get(0);
        Long postId = existingPost.getId();

        // when + then
        ResultActions testMock = mockMvc.perform(get("/api/admin/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andDo(print());

        // docs
        testMock.andDo(document("get-/api/admin/posts/{postId}/comments",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName("postId").description("해당 포스트 ID")
                ),
                responseFields(
                        fieldWithPath("commentDtoList").description("댓글 목록"),
                        fieldWithPath("commentDtoList[].id").description("댓글 ID"),
                        fieldWithPath("commentDtoList[].content").description("댓글 내용"),
                        fieldWithPath("commentDtoList[].email").description("작성자 이메일"),
                        fieldWithPath("commentDtoList[].postId").description("포스트 ID"),
                        fieldWithPath("commentDtoList[].status").description("댓글 상태 (ACTIVE, DELETED, ADMIN_DELETED)"),
                        fieldWithPath("commentDtoList[].regDate").description("작성 날짜"),
                        fieldWithPath("commentDtoList[].parentId").description("부모 댓글 ID (최상위 댓글인 경우 null)"),
                        subsectionWithPath("commentDtoList[].replies").description("대댓글 목록 (구조는 상위 댓글과 동일)")
                )));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("delete /api/admin/comments -> 관리자 댓글 삭제")
       void test2() throws Exception {
        // given
        Comment existingComment = commentRepository.findAll().get(1);

        CommentAdminDeleteRequest request = CommentAdminDeleteRequest.builder()
                .commentId(existingComment.getId())
                .postId(existingComment.getPost().getId())
                .parentId(existingComment.getParent() != null ? existingComment.getParent().getId() : null)
                .email(existingComment.getEmail())
                .content(existingComment.getContent())
                .build();

        // when + then
        ResultActions testMock = mockMvc.perform(delete("/api/admin/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        // docs
        testMock.andDo(document("delete-/api/admin/comments",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("postId").description("포스트 ID"),
                        fieldWithPath("commentId").description("삭제할 댓글 ID"),
                        fieldWithPath("parentId").description("부모 댓글 ID (null 가능)"),
                        fieldWithPath("email").description("댓글 작성자 이메일"),
                        fieldWithPath("content").description("댓글 내용")
                ),
                responseFields(
                        fieldWithPath("commentDtoList").description("삭제 후 댓글 목록"),
                        fieldWithPath("commentDtoList[].id").description("댓글 ID"),
                        fieldWithPath("commentDtoList[].content").description("댓글 내용"),
                        fieldWithPath("commentDtoList[].regDate").description("댓글 작성 시간"),
                        fieldWithPath("commentDtoList[].email").description("작성자 이메일"),
                        fieldWithPath("commentDtoList[].status").description("댓글 상태 (ACTIVE, DELETED, ADMIN_DELETED)"),
                        fieldWithPath("commentDtoList[].postId").description("포스트 ID"),
                        fieldWithPath("commentDtoList[].parentId").description("부모 댓글 ID (최상위 댓글인 경우 null)"),
                        subsectionWithPath("commentDtoList[].replies").description("대댓글 목록 (구조는 상위 댓글과 동일)")
                )));
    }

        @Test
        @WithMockCustomUser
        @DisplayName("get /api/admin/comments -> 관리자 전체 댓글 목록 조회")
        void test3() throws Exception {
            // given


            // when + then
            ResultActions testMock = mockMvc.perform(get("/api/admin/comments")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print());

            // docs
            testMock.andDo(document("get-/api/admin/comments",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("commentDtoList").description("댓글 목록"),
                            fieldWithPath("commentDtoList[].id").description("댓글 ID"),
                            fieldWithPath("commentDtoList[].content").description("댓글 내용"),
                            fieldWithPath("commentDtoList[].regDate").description("댓글 작성 시간"),
                            fieldWithPath("commentDtoList[].email").description("작성자 이메일"),
                            fieldWithPath("commentDtoList[].status").description("댓글 상태 (ACTIVE, DELETED, ADMIN_DELETED)"),
                            fieldWithPath("commentDtoList[].postId").description("포스트 ID"),
                            fieldWithPath("commentDtoList[].parentId").description("부모 댓글 ID (최상위 댓글인 경우 null)"),
                            subsectionWithPath("commentDtoList[].replies").description("대댓글 목록 (구조는 상위 댓글과 동일)")
                    )));
    }
}