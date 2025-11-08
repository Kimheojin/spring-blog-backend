package HeoJin.demoBlog.comment.controller.doc;

import HeoJin.demoBlog.configuration.Integration.ApiDocTestBase;
import HeoJin.demoBlog.configuration.Integration.DataInitComponent;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.post.entity.Post;
import HeoJin.demoBlog.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentReadTestApiDocTest extends ApiDocTestBase {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private DataInitComponent dataInitComponent;

    @BeforeEach
    void init(){
        Member member = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        dataInitComponent.saveAllPosts(member);
        dataInitComponent.saveAllComments();
    }

    @Test
    @DisplayName("get -> /api/posts/{postId}/comments -> 해당 postId 댓글 확인")
    public void test1() throws Exception {
        // given

        Post existingPost = postRepository.findAll().get(0);

        Long postId = existingPost.getId();

        //when + then
        ResultActions testMock = mockMvc.perform(get("/api/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andDo(print());

        //docs
        testMock.andDo(document("get-/api/posts/{postId}/comments",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("postId").description("해당 포스트 ID")
                ),
                responseFields(
                        fieldWithPath("commentDtoList").description("댓글 목록"),
                        fieldWithPath("commentDtoList[].id").description("댓글 ID"),
                        fieldWithPath("commentDtoList[].content").description("댓글 내용"),
                        fieldWithPath("commentDtoList[].regDate").description("댓글 작성 시간"),
                        fieldWithPath("commentDtoList[].email").description("작성자 이메일"),
                        fieldWithPath("commentDtoList[].postId").description("포스트 ID"),
                        fieldWithPath("commentDtoList[].status").description("댓글 상태 (ACTIVE, DELETED, ADMIN_DELETED)"),
                        fieldWithPath("commentDtoList[].parentId").description("부모 댓글 ID (최상위 댓글인 경우 null)"),
                        subsectionWithPath("commentDtoList[].replies").description("대댓글 목록 (구조는 상위 댓글과 동일)")
                )));

    }

}