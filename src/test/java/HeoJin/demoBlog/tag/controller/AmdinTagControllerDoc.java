package HeoJin.demoBlog.tag.controller;


import HeoJin.demoBlog.configuration.Integration.ApiDocTestSetup;
import HeoJin.demoBlog.configuration.mockUser.WithMockCustomUser;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.post.dto.response.TagResponse;
import HeoJin.demoBlog.post.repository.PostRepository;
import HeoJin.demoBlog.tag.dto.request.AddTagDtoRequest;
import HeoJin.demoBlog.tag.dto.request.DeleteTagDtoRequest;
import HeoJin.demoBlog.tag.dto.request.ListAddTagRequestDto;
import HeoJin.demoBlog.tag.dto.request.ListDeleteTagRequest;
import HeoJin.demoBlog.tag.repository.PostTagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AmdinTagControllerDoc extends ApiDocTestSetup {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostTagRepository postTagRepository;


    @BeforeEach
    void init() {
        Member member = createTestMember();
        saveAllCategories();
        saveAllPosts(member);
        saveAllTag();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("Post -> 정상 요청 + tag 추가 엔드포인트")
    void test1() throws Exception {
        // given
        Long postId = postRepository.findAll().get(0).getId();
        List<AddTagDtoRequest> tagTestRequestList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {

            AddTagDtoRequest addTagDtoRequest = AddTagDtoRequest.builder()
                    .tagName("test" + i)
                    .build();
            tagTestRequestList.add(addTagDtoRequest);
        }
        ListAddTagRequestDto testTagRequest = new ListAddTagRequestDto(tagTestRequestList, postId);
        // when + then
        ResultActions testMock = mockMvc.perform(post("/api/admin/tag/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTagRequest)))
                .andExpect(status().isOk())
                .andDo(print());

        // docs
        testMock.andDo(document("post-/api/admin/tag/list",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("DtoList").description("추가하고자 하는 태그 리스트"),
                        fieldWithPath("DtoList[].tagName").description("해당 게시물 태그 이름"),
                        fieldWithPath("postId").description("해당 postId")
                ),
                responseFields(

                        fieldWithPath("tagResponseList").description("저장된 태그 리스트"),
                        fieldWithPath("tagResponseList[].tagName").description("해당 태그 이름"),
                        fieldWithPath("tagResponseList[].tagId").description("해당 태그 id")

                )
        ));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("Delete -> 정상 요청 + tag 삭제 엔드포인트")
    void test2() throws Exception {
        // given
        Long postId = postRepository.findAll().get(0).getId();
        List<TagResponse> tagListWithPostId = postTagRepository.getTagListWithPostId(postId);
        String tagName = tagListWithPostId.get(0).getTagName();
        List<DeleteTagDtoRequest> dtoRequests = List.of(DeleteTagDtoRequest.builder()
                .tagName(tagName).build());
        ListDeleteTagRequest testTagRequest = new ListDeleteTagRequest(dtoRequests, postId);

        // when + then
        ResultActions testMock = mockMvc.perform(delete("/api/admin/tag/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTagRequest)))
                .andExpect(status().isOk())
                .andDo(print());

        // docs
        testMock.andDo(document("delete-/api/admin/tag/list",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("DtoList").description("삭제하고자 하는 태그 리스트"),
                        fieldWithPath("DtoList[].tagName").description("해당 게시물 태그 이름"),
                        fieldWithPath("postId").description("해당 postId")
                ),
                responseFields(

                        fieldWithPath("tagResponseList").description("삭제 후 저장된 태그 리스트"),
                        fieldWithPath("tagResponseList[].tagName").description("해당 태그 이름"),
                        fieldWithPath("tagResponseList[].tagId").description("해당 태그 id")

                )
        ));
    }



}
