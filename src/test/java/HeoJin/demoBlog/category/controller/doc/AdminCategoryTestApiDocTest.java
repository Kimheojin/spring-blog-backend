package HeoJin.demoBlog.category.controller.doc;


import HeoJin.demoBlog.category.dto.request.AddCategoryRequest;
import HeoJin.demoBlog.category.dto.request.DeleteCategoryRequest;
import HeoJin.demoBlog.category.dto.request.ModifyCategoryNameRequest;
import HeoJin.demoBlog.category.entity.Category;
import HeoJin.demoBlog.category.repository.CategoryRepository;
import HeoJin.demoBlog.configuration.Integration.ApiDocTestBase;
import HeoJin.demoBlog.configuration.Integration.DataInitComponent;
import HeoJin.demoBlog.configuration.mockUser.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AdminCategoryTestApiDocTest extends ApiDocTestBase {

    @Autowired
    public CategoryRepository categoryRepository;
    @Autowired
    public DataInitComponent dataInitComponent;

    @BeforeEach
    public void init(){
        dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
    }

    // Post + /api/admin/categories -> 카테고리 추가
    @Test
    @WithMockCustomUser
    @DisplayName("post /api/admin/categories -> 카테고리 추가 정상 요청")
    void test1() throws Exception {
        // given
        final String categoryName = "테스트1";
        AddCategoryRequest request = AddCategoryRequest.builder()
                .categoryName(categoryName)
                .priority(1L)
                .build();

        // when + then
        ResultActions testMock = mockMvc.perform(post("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        // docs
        testMock.andDo(document("post-/api/admin/categories",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("categoryName").description("추가 카테고리 이름"),
                        fieldWithPath("priority").description("카테고리 우선순위")
                ),
                responseFields(
                        fieldWithPath("categoryResponses").description("카테고리 목록"),
                        fieldWithPath("categoryResponses[].categoryId").description("카테고리 아이디"),
                        fieldWithPath("categoryResponses[].categoryName").description("저장된 카테고리 이름"),
                        fieldWithPath("categoryResponses[].priority").description("카테고리 우선순위")
                )));
    }

    // Delete + api/admin/categories -> 카테고리 삭제
    @Test
    @WithMockCustomUser
    @DisplayName("delete /api/admin/categories -> 카테고리 삭제 정상 요청")
    void test2() throws Exception {
        // given
        
        // 기존 데이터로 하면 연관 데이터 존재할 수 도 있어서 새로 만들어야 할듯
        final String categoryName = "테스트1";
        Category testCategory = Category.builder()
                .categoryName(categoryName)
                .priority(1L)
                .build();

        categoryRepository.save(testCategory);

        DeleteCategoryRequest request = DeleteCategoryRequest.builder()
                .categoryId(categoryRepository.findByCategoryName(categoryName).get().getId())
                .categoryName(categoryName)
                .build();
        // when + then
        ResultActions testMock = mockMvc.perform(delete("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        // docs
        testMock.andDo(document("delete-/api/admin/categories",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(

                        fieldWithPath("categoryId").description("삭제 카테고리 Id"),
                        fieldWithPath("categoryName").description("삭제 카테고리 이름")
                ),
                responseFields(
                        fieldWithPath("categoryResponses").description("카테고리 목록"),
                        fieldWithPath("categoryResponses[].categoryId").description("카테고리 아이디"),
                        fieldWithPath("categoryResponses[].categoryName").description("저장된 카테고리 이름"),
                        fieldWithPath("categoryResponses[].priority").description("카테고리 우선순위")
                )));

        categoryRepository.deleteAll();
    }

    // Put + /api/admin/categories -> 카테고리 이름 변경
    @Test
    @WithMockCustomUser
    @DisplayName("put /api/admin/categories -> 카테고리 이름 변경")
    void test3() throws Exception {
        // given
        Category category = categoryRepository.findByCategoryName("Java1")
                .orElseThrow(() -> new AssertionError("테스트 초기화 데이터 오류"));

        ModifyCategoryNameRequest request = ModifyCategoryNameRequest.builder()
                .categoryId(category.getId())
                .categoryName("변경된카테고리명")
                .priority(2L)
                .build();

        // when + then
        ResultActions testMock = mockMvc.perform(put("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        // docs
        testMock.andDo(document("put-/api/admin/categories",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("categoryId").description("변경할 카테고리 아이디"),
                        fieldWithPath("categoryName").description("변경 할 테스트 카테고리 명"),
                        fieldWithPath("priority").description("카테고리 우선순위")
                ),
                responseFields(
                        fieldWithPath("categoryResponses").description("카테고리 목록"),
                        fieldWithPath("categoryResponses[].categoryId").description("카테고리 아이디"),
                        fieldWithPath("categoryResponses[].categoryName").description("저장된 카테고리 이름"),
                        fieldWithPath("categoryResponses[].priority").description("카테고리 우선순위")
                )));


    }


    @Test
    @WithMockCustomUser
    @DisplayName("delete /api/admin/categories -> 존재하지 않는 카테고리 삭제 요청")
    void test5() throws Exception {
        // given

        // 기존 데이터로 하면 연관 데이터 존재할 수 도 있어서 새로 만들어야 할듯
        final String categoryName = "테스트1";
        Category testCategory = Category.builder()
                .categoryName(categoryName)
                .build();

        categoryRepository.save(testCategory);

        DeleteCategoryRequest request = DeleteCategoryRequest.builder()
                .categoryId(categoryRepository.findByCategoryName(categoryName).get().getId())
                .categoryName(categoryName)
                .build();
        // when + then
        ResultActions testMock = mockMvc.perform(delete("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());


    }



}