package HeoJin.demoBlog.comment.controller.doc;

import HeoJin.demoBlog.comment.dto.request.CommentDeleteRequest;
import HeoJin.demoBlog.comment.dto.request.CommentModifyRequest;
import HeoJin.demoBlog.comment.dto.request.CommentWriteRequest;
import HeoJin.demoBlog.comment.entity.Comment;
import HeoJin.demoBlog.comment.repository.CommentRepository;
import HeoJin.demoBlog.configuration.Integration.ApiDocTestSetup;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;


import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class CommentWriteTestApiDocTest extends ApiDocTestSetup {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void init(){
        Member member = createTestMember();
        saveAllCategories();
        saveAllPosts(member);
        saveAllComments();
    }

    // 댓글 + 대댓글 작성
    @Test
    @DisplayName("post -> /api/posts/comments -> 댓글 + 대댓글 작성")
    public void test1() throws Exception {
        // given

        final String EMAIL = "hurjin1109@test.com";
        final String PASSWORD = "1234";
        final String CONTENT = "안녕하세요";


        Post existPost = postRepository.findAll().get(0);

        CommentWriteRequest request = CommentWriteRequest.builder()
                .postId(existPost.getId())
                .email(EMAIL)
                .password(PASSWORD)
                .content(CONTENT)
                .parentId(null)
                .build();


        //when + then
        ResultActions testMock = mockMvc.perform(post("/api/posts/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        //docs
        testMock.andDo(document("post-/api/posts/comments",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("postId").description("post Id"),
                        fieldWithPath("parentId").description("부모 댓글 Id (null 가능)"),
                        fieldWithPath("email").description("댓글 아이디"),
                        fieldWithPath("password").description("댓글 비밀 번호"),
                        fieldWithPath("content").description("댓글 내용")
                ),
                responseFields(
                        fieldWithPath("commentDtoList").description("댓글 목록"),
                        fieldWithPath("commentDtoList[].id").description("댓글 ID"),
                        fieldWithPath("commentDtoList[].content").description("댓글 내용"),
                        fieldWithPath("commentDtoList[].regDate").description("작성 날짜"),
                        fieldWithPath("commentDtoList[].email").description("작성자 이메일"),
                        fieldWithPath("commentDtoList[].postId").description("포스트 ID"),
                        fieldWithPath("commentDtoList[].status").description("댓글 상태 (ACTIVE, DELETED, ADMIN_DELETED)"),
                        fieldWithPath("commentDtoList[].parentId").description("부모 댓글 ID (최상위 댓글인 경우 null)"),
                        subsectionWithPath("commentDtoList[].replies").description("대댓글 목록 (구조는 상위 댓글과 동일)")
                )));

    }


    // 댓글 + 대댓글 임시 삭제

    @Test
    @DisplayName("post -> /api/comments -> 댓글 + 대댓글 임시 삭제")
    public void test2() throws Exception {
        // given
        List<Comment> comments = commentRepository.findAll();


        Comment existingComment = comments.get(1);

        CommentDeleteRequest request = CommentDeleteRequest.builder()
                .postId(existingComment.getPost().getId())
                .commentId(existingComment.getId())
                .parentId(existingComment.getParent() != null ? existingComment.getParent().getId() : null)
                .email(existingComment.getEmail())
                .password(existingComment.getPassword())
                .content(existingComment.getContent())
                .build();


        //when + then
        ResultActions testMock = mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        //docs
        testMock.andDo(document("post-/api/comments",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("postId").description("post Id"),
                        fieldWithPath("commentId").description("commentId"),
                        fieldWithPath("parentId").description("부모 댓글 Id (null 가능)"),
                        fieldWithPath("email").description("댓글 아이디"),
                        fieldWithPath("password").description("댓글 비밀 번호"),
                        fieldWithPath("content").description("댓글 내용")
                ),
                responseFields(
                        fieldWithPath("commentDtoList").description("댓글 목록"),
                        fieldWithPath("commentDtoList[].id").description("댓글 ID"),
                        fieldWithPath("commentDtoList[].regDate").description("작성 날짜"),
                        fieldWithPath("commentDtoList[].content").description("댓글 내용"),
                        fieldWithPath("commentDtoList[].email").description("작성자 이메일"),
                        fieldWithPath("commentDtoList[].status").description("댓글 상태 (ACTIVE, DELETED, ADMIN_DELETED)"),
                        fieldWithPath("commentDtoList[].postId").description("포스트 ID"),
                        fieldWithPath("commentDtoList[].parentId").description("부모 댓글 ID (최상위 댓글인 경우 null)"),
                        subsectionWithPath("commentDtoList[].replies").description("대댓글 목록 (구조는 상위 댓글과 동일)")
                )));

    }

    @Test
    @DisplayName("put -> /api/comments -> 댓글 수정")
    public void test3() throws Exception {
        // given
        Post existingPost = postRepository.findAll().get(0);
        Long postId = existingPost.getId();


        Comment existingComment = commentRepository.customFindCommentsByPostId(existingPost.getId()).get(0);
        Long commentId = existingComment.getId();


        CommentModifyRequest commentModifyRequest = CommentModifyRequest.builder()
                .postId(existingPost.getId())
                .commentId(commentId)
                .parentId(null)
                .password(existingComment.getPassword())
                .email(existingComment.getEmail())
                .content("수정된 댓글 내용")
                .build();

        String requestBody = objectMapper.writeValueAsString(commentModifyRequest);

        //when + then
        ResultActions testMock = mockMvc.perform(put("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());

        //docs
        testMock.andDo(document("put-/api/comments",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("postId").description("포스트 ID"),
                        fieldWithPath("commentId").description("수정할 댓글 ID"),
                        fieldWithPath("parentId").description("부모 댓글 ID (최상위 댓글인 경우 null)").optional(),
                        fieldWithPath("password").description("댓글 비밀번호"),
                        fieldWithPath("email").description("작성자 이메일"),
                        fieldWithPath("content").description("수정할 댓글 내용")
                ),
                responseFields(
                        fieldWithPath("commentDtoList").description("댓글 목록"),
                        fieldWithPath("commentDtoList[].id").description("댓글 ID"),
                        fieldWithPath("commentDtoList[].regDate").description("작성 날짜"),
                        fieldWithPath("commentDtoList[].content").description("댓글 내용"),
                        fieldWithPath("commentDtoList[].status").description("댓글 상태 (ACTIVE, DELETED, ADMIN_DELETED)"),
                        fieldWithPath("commentDtoList[].email").description("작성자 이메일"),
                        fieldWithPath("commentDtoList[].postId").description("포스트 ID"),
                        fieldWithPath("commentDtoList[].parentId").description("부모 댓글 ID (최상위 댓글인 경우 null)"),
                        subsectionWithPath("commentDtoList[].replies").description("대댓글 목록 (구조는 상위 댓글과 동일)")
                )));
    }



}