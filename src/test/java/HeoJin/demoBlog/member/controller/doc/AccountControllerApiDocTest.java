package HeoJin.demoBlog.member.controller.doc;

import HeoJin.demoBlog.configuration.Integration.ApiDocTestBase;
import HeoJin.demoBlog.configuration.Integration.DataInitComponent;
import HeoJin.demoBlog.configuration.mockUser.WithMockCustomUser;
import HeoJin.demoBlog.member.dto.request.PasswordUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountControllerApiDocTest extends ApiDocTestBase {

    @Autowired
    private DataInitComponent dataInitComponent;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        dataInitComponent.createTestMember();
    }

    @Test
    @DisplayName("PATCH /api/auth/password - 비밀번호 변경")
    @WithMockCustomUser
    void test1() throws Exception {
        // given
        PasswordUpdateDto passwordUpdateDto = PasswordUpdateDto.builder()
                .oldPassword(dataInitComponent.TEST_PASSWORD)
                .newPassword("newPassword1234")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(patch("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordUpdateDto)));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string("비밀번호 변경이 완료되었습니다."))
                .andDo(print())
                .andDo(document("account-password-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("oldPassword").type(JsonFieldType.STRING).description("기존 비밀번호"),
                                fieldWithPath("newPassword").type(JsonFieldType.STRING).description("새로운 비밀번호")
                        )
                ));
    }
}
