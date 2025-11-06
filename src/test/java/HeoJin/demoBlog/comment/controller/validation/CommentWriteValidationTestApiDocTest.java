package HeoJin.demoBlog.comment.controller.validation;

import HeoJin.demoBlog.comment.dto.request.CommentDeleteRequest;
import HeoJin.demoBlog.comment.dto.request.CommentModifyRequest;
import HeoJin.demoBlog.comment.dto.request.CommentWriteRequest;
import HeoJin.demoBlog.configuration.Integration.ApiDocTestSetup;
import HeoJin.demoBlog.configuration.mockUser.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentWriteValidationTestApiDocTest extends ApiDocTestSetup {

    final String DEFAULTVALIDATIONMESSAGE = "입력값 검증에 실패했습니다.";


    @Test
    @DisplayName("/api/posts/comments POST -> validation 예외 적용 테스트")
    void test1() throws Exception {
        // given
        CommentWriteRequest request = CommentWriteRequest.builder()
                .postId(null)
                .parentId(-7777L)
                .email("")
                .password("")
                .content("")
                .build();

        // when + then
        mockMvc.perform(post("/api/posts/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEFAULTVALIDATIONMESSAGE))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.validation").exists())
                .andExpect(jsonPath("$.validation.postId").value("포스트 ID를 선택해 주세요"))
                .andExpect(jsonPath("$.validation.parentId").value("parent ID는 양수여야 합니다."))
                .andExpect(jsonPath("$.validation.email").value("이메일을 입력해 주세요."))
                .andExpect(jsonPath("$.validation.password").value("비밀번호를 입력해 주세요"))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해 주세요"))
                .andDo(print());
    }


    @Test
    @DisplayName("/api/comments POST -> validation 예외 적용 테스트")
    void test2() throws Exception {
        // given
         CommentDeleteRequest request = CommentDeleteRequest.builder()
                .postId(null)
                .commentId(null)
                .parentId(11L)
                .email("")
                .password("")
                .content("")
                .build();
        // when + then
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEFAULTVALIDATIONMESSAGE))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.validation").exists())
                .andExpect(jsonPath("$.validation.postId").value("포스트 ID 값을 선택 해 주세요."))
                .andExpect(jsonPath("$.validation.commentId").value("comment ID 값을 선택 해 주세요."))
                .andExpect(jsonPath("$.validation.email").value("email을 입력해 주세요."))
                .andExpect(jsonPath("$.validation.password").value("비밀번호를 입력 해 주세요."))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력 해 주세요."))
                .andDo(print());
    }

    @WithMockCustomUser
    @Test
    @DisplayName("/api/comments PUT -> validation 예외 적용 테스트")
    void test3() throws Exception {
        // given
        CommentModifyRequest request = CommentModifyRequest.builder()
                .postId(null)
                .commentId(null)
                .parentId(11L)
                .email("")
                .password("")
                .content("")
                .build();

        // when + then
        mockMvc.perform(put("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEFAULTVALIDATIONMESSAGE))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.validation").exists())
                .andExpect(jsonPath("$.validation.postId").value("포스트 ID를 선택해 주세요"))
                .andExpect(jsonPath("$.validation.commentId").value("comment ID를 선택해 주세요"))
                .andExpect(jsonPath("$.validation.email").value("이메일을 입력해 주세요."))
                .andExpect(jsonPath("$.validation.password").value("비밀번호를 입력 해 주세요"))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해 주세요"))
                .andDo(print());
    }
}
