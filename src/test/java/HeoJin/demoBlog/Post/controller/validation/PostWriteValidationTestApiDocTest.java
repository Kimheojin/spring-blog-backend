package HeoJin.demoBlog.Post.controller.validation;

import HeoJin.demoBlog.configuration.Integration.ApiDocTestSetup;
import HeoJin.demoBlog.configuration.mockUser.WithMockCustomUser;
import HeoJin.demoBlog.post.dto.request.PostDeleteRequest;
import HeoJin.demoBlog.post.dto.request.PostModifyRequest;
import HeoJin.demoBlog.post.dto.request.PostRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostWriteValidationTestApiDocTest extends ApiDocTestSetup {

    final String DEFAULTVALIDATIONMESSAGE = "입력값 검증에 실패했습니다.";

    @WithMockCustomUser
    @Test
    @DisplayName("/api/admin/posts Post -> validation 예외 적용 테스트")
    void test1() throws Exception {
        // given
        PostRequest request = PostRequest.builder()
                .title("")
                .content("")
                .categoryName("")
                .postStatus(null).build();

        // when + then
        mockMvc.perform(post("/api/admin/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEFAULTVALIDATIONMESSAGE))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.validation").exists())
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해 주세요"))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해 주세요"))
                .andExpect(jsonPath("$.validation.categoryName").value("카테고리 명을 입력해 주세요"))
                .andExpect(jsonPath("$.validation.postStatus").value("게시글 상태를 선택해주세요."))
                .andDo(print());

    }
    @WithMockCustomUser
    @Test
    @DisplayName("/api/admin/posts PUT -> validation 예외 적용 테스트")
    void test2() throws Exception {
        // given
        PostModifyRequest request = PostModifyRequest.builder()
                .postId(null)
                .title("")
                .content("")
                .categoryName("")
                .postStatus(null)
                .build();

        // when + then
        mockMvc.perform(put("/api/admin/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEFAULTVALIDATIONMESSAGE))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.validation").exists())
                .andExpect(jsonPath("$.validation.postId").value("유효하지 않은 포스트 ID 입니다."))
                .andExpect(jsonPath("$.validation.title").value("변경하고자 하는 제목을 입력해주세요"))
                .andExpect(jsonPath("$.validation.content").value("변경하고자 하는 내용을 입력해주세요"))
                .andExpect(jsonPath("$.validation.categoryName").value("변경하고자 하는 카테고리명을 선택해주세요"))
                .andExpect(jsonPath("$.validation.postStatus").value("게시글 상태를 선택 해 주세요"))
                .andDo(print());
    }

    @WithMockCustomUser
    @Test
    @DisplayName("/api/admin/posts DELETE -> validation 예외 적용 테스트")
    void test3() throws Exception {
        // given
        PostDeleteRequest request = PostDeleteRequest.builder()
                .postId(null)
                .build();

        // when + then
        mockMvc.perform(delete("/api/admin/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEFAULTVALIDATIONMESSAGE))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.validation").exists())
                .andExpect(jsonPath("$.validation.postId").value("포스트 ID를 선택 해 주세요"))
                .andDo(print());
    }
}
