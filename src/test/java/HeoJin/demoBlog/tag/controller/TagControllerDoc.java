package HeoJin.demoBlog.tag.controller;

import HeoJin.demoBlog.configuration.Integration.ApiDocTestBase;
import HeoJin.demoBlog.configuration.Integration.DataInitComponent;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.tag.entity.Tag;
import HeoJin.demoBlog.tag.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TagControllerDoc extends ApiDocTestBase {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private DataInitComponent dataInitComponent;

    @BeforeEach
    void init(){
        Member member = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        dataInitComponent.saveAllPosts(member);
        dataInitComponent.saveAllTag();
    }

    @Test
    @DisplayName("Get -> 정상 요청 + 전체 태그 리스트 반환")
    void test1() throws Exception {
        // given

        // when + then
        ResultActions testMock = mockMvc.perform(get("/api/tag/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());


        // docs
        testMock.andDo(document("get-/api/tag/list",
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("tagResponseDtoList").description("전체 태그 리스트"),
                        fieldWithPath("tagResponseDtoList[].tagName").description("해당 태그 이름"),
                        fieldWithPath("tagResponseDtoList[].tagId").description("해당 태그 id"),
                        fieldWithPath("tagResponseDtoList[].count").description("해당 태그 포함 post 수")

                )
        ));
    }

    // 태그 아이디를 통한 조회
    @Test
    @DisplayName("Get -> 정상 요청 + 태그 아이디를 통한 조회")
    void test2() throws Exception {
        // given
        Tag testTag = tagRepository.findByTagName("tag1").orElseThrow();
        String testTagName = testTag.getTagName();
        Long testTagId = testTag.getId();
        
        // when
        ResultActions testMock = mockMvc.perform(get("/api/tag/postlist")
                .param("tagId", String.valueOf(testTagId))
                .param("tagName", testTagName)
                .param("page", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andDo(print());

        // docs
        testMock.andDo(document("get-/api/tag/postlist",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("tagId").description("태그 ID"),
                        parameterWithName("tagName").description("태그 이름"),
                        parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                        parameterWithName("pageSize").description("페이지 크기")
                ),
                responseFields(
                        fieldWithPath("content").description("태그가 포함된 게시글 목록"),
                        fieldWithPath("content[].postId").description("게시글 ID"),
                        fieldWithPath("content[].title").description("게시글 제목"),
                        fieldWithPath("content[].regDate").description("게시글 작성일 (yyyy-MM-dd HH:mm)"),
                        fieldWithPath("pageNumber").description("현재 페이지 번호"),
                        fieldWithPath("pageSize").description("페이지 크기"),
                        fieldWithPath("totalElements").description("전체 게시글 수"),
                        fieldWithPath("totalPages").description("전체 페이지 수"),
                        fieldWithPath("first").description("첫 페이지 여부"),
                        fieldWithPath("last").description("마지막 페이지 여부")
                )
        ));
    }

}
