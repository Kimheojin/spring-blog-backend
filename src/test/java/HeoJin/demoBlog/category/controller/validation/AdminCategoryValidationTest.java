package HeoJin.demoBlog.category.controller.validation;


import HeoJin.demoBlog.category.dto.request.AddCategoryRequest;
import HeoJin.demoBlog.category.dto.request.DeleteCategoryRequest;
import HeoJin.demoBlog.category.dto.request.ModifyCategoryNameRequest;
import HeoJin.demoBlog.configuration.Integration.ApiDocTestBase;
import HeoJin.demoBlog.configuration.mockUser.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminCategoryValidationTest extends ApiDocTestBase {

    final String DEFAULTVALIDATIONMESSAGE = "입력값 검증에 실패했습니다.";

    // validation 걸리는 거라 미리 저장 안해놔도 괜찮을듯
    @Test
    @WithMockCustomUser
    @DisplayName("/api/admin/categories Delete -> request 유효하지 않은 categoryId (음수) + 빈 categoryname")
    public void test1() throws Exception {
        // given

        DeleteCategoryRequest request = DeleteCategoryRequest.builder()
                .categoryId(-999L)
                .categoryName("").build();


        // when + then

        mockMvc.perform(delete("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEFAULTVALIDATIONMESSAGE))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.validation").exists())
                .andExpect(jsonPath("$.validation.categoryId").value("유효하지 않은 카테고리 ID 입니다."))
                .andExpect(jsonPath("$.validation.categoryName").value("삭제하고자 하는 카테고리 이름이 유효하지 않습니다."))
                .andDo(print());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("/api/admin/categories post -> 카테고리 명은 15자를 넘을 수 없습니다.  -> 테스트")
    public void test2() throws Exception {
        // given

        AddCategoryRequest request = AddCategoryRequest.builder()
                .categoryName("카테고리명은15자를넘길수없습니다.카테고리명은15자를넘길수없습니다.")
                .build();

        // when + then
        mockMvc.perform(post("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEFAULTVALIDATIONMESSAGE))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.validation").exists())
                .andExpect(jsonPath("$.validation.categoryName").value("카테고리 명은 15자를 넘을 수 없습니다."))
                .andDo(print());

    }

    @Test
    @WithMockCustomUser
    @DisplayName("/api/admin/categories post -> 카테고리 명은 15자를 넘을 수 없습니다.  -> 테스트")
    public void test3() throws Exception {

        // given
        ModifyCategoryNameRequest request = ModifyCategoryNameRequest.builder()
                .categoryId(-888L)
                .categoryName("").build();
        // when + then
        mockMvc.perform(put("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DEFAULTVALIDATIONMESSAGE))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.validation").exists())
                .andExpect(jsonPath("$.validation.categoryId").value("유효하지 않은 카테고리 ID 입니다."))
                .andExpect(jsonPath("$.validation.categoryName").value("삭제하고자 하는 카테고리 이름이 유효하지 않습니다."))
                .andDo(print());


    }

}