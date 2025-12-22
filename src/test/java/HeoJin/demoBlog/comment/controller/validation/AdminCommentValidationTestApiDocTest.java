package HeoJin.demoBlog.comment.controller.validation;

import HeoJin.demoBlog.comment.dto.request.CommentAdminDeleteRequest;
import HeoJin.demoBlog.configuration.Integration.ApiDocTestBase;
import HeoJin.demoBlog.configuration.mockUser.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminCommentValidationTestApiDocTest extends ApiDocTestBase {

    final String DEFAULTVALIDATIONMESSAGE = "입력값 검증에 실패했습니다.";

    @Test
    @DisplayName("/api/admin/comments delete -> validation 한번에 테스트")
    @WithMockCustomUser
    void test1() throws Exception {
        // given
        CommentAdminDeleteRequest request = CommentAdminDeleteRequest.builder()
                .postId(null)
                .commentId(null)
                .parentId(-999L)
                .email("")
                .content("")
                .build();

        // when + then

        mockMvc.perform(delete("/api/admin/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEFAULTVALIDATIONMESSAGE))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.validation").exists())
                .andExpect(jsonPath("$.validation.postId").value("포스트 ID를 선택해 주세요"))
                .andExpect(jsonPath("$.validation.commentId").value("comment ID를 선택해 주세요"))
                .andExpect(jsonPath("$.validation.parentId").value("parentId는 0 이상이어야 합니다."))
                .andExpect(jsonPath("$.validation.email").value("이메일을 입력해 주세요."))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해 주세요"))
                .andDo(print());




    }
}
