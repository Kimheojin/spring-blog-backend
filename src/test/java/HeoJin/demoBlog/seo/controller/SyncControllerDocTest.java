package HeoJin.demoBlog.seo.controller;

import HeoJin.demoBlog.configuration.Integration.ApiDocTestBase;
import HeoJin.demoBlog.configuration.Integration.DataInitComponent;
import HeoJin.demoBlog.configuration.mockUser.WithMockCustomUser;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.seo.entity.PostMongo;
import HeoJin.demoBlog.seo.repository.PostMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class SyncControllerDocTest extends ApiDocTestBase {

    @Autowired
    private DataInitComponent dataInitComponent;
    @Autowired
    private PostMongoRepository postMongoRepository;

    @BeforeEach
    void init() {
        Member member = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        dataInitComponent.saveAllPosts(member);
        dataInitComponent.saveAllTag();

        // 초기화
        List<PostMongo> existing = postMongoRepository.getAll();
        if (!existing.isEmpty()) {
            postMongoRepository.deleteAll(existing);
        }
    }

    @Test
    @WithMockCustomUser
    @DisplayName("POST /api/admin/seo/mongo-sync -> 동기화 요청 + 결과 문서화")
    void test1() throws Exception {
        // given

        // when
        ResultActions result = mockMvc.perform(post("/api/admin/seo/mongo-sync")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.insertCount").isNumber())
                .andExpect(jsonPath("$.updateCount").isNumber())
                .andExpect(jsonPath("$.deleteCount").isNumber())
                .andDo(print());

        // then + docs
        result.andDo(document("post-/api/admin/seo/mongo-sync",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("updateCount").description("동기화 과정에서 업데이트된 문서 수"),
                        fieldWithPath("insertCount").description("새로 삽입된 Mongo 문서 수"),
                        fieldWithPath("deleteCount").description("삭제된 Mongo 문서 수")
                )));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("GET /api/admin/seo/mongo/seo-data -> Mongo 상태 조회 문서화")
    void test2() throws Exception {
        // given
        mockMvc.perform(post("/api/admin/seo/mongo-sync")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // when
        ResultActions result = mockMvc.perform(get("/api/admin/seo/mongo/seo-data")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seoDataCount").isNumber())
                .andDo(print());

        // then + docs
        result.andDo(document("get-/api/admin/seo/mongo/seo-data",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("seoDataCount").description("현재 MongoDB에 존재하는 SEO 데이터 문서 수")
                )));
    }

}
