package HeoJin.demoBlog.member.controller.doc;

import HeoJin.demoBlog.configuration.Integration.ApiDocTestBase;
import HeoJin.demoBlog.configuration.Integration.DataInitComponent;
import HeoJin.demoBlog.configuration.mockUser.WithMockCustomUser;
import HeoJin.demoBlog.member.dto.request.LoginDto;
import HeoJin.demoBlog.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class AuthTestApiDocTest extends ApiDocTestBase {
    @Autowired
    private DataInitComponent dataInitComponent;


    @BeforeEach
    void init(){
        // role 까지만 생성
        Member member = dataInitComponent.createTestMember();
    }

    @Test
    @DisplayName("GET /api/auth/status - 인증된 사용자 상태 확인")
    @WithMockCustomUser()
    void test1() throws Exception {
        // WithMock 타면 filter 인증 관련 코드 안탐
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Long expectedMemberId = (Long) authentication.getPrincipal();


        // when + then
        ResultActions testMock = mockMvc.perform(get("/api/auth/status")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)) // 시큐리티 컨텍스트에 Authentication 객체 설정
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.authenticated").isBoolean())
                        .andExpect(jsonPath("$.memberId").value(expectedMemberId))
                        .andExpect(jsonPath("$.role").value("ADMIN"))
                        .andDo(print());



        // docs
        testMock.andDo(document("get-auth-status-authenticated",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("authenticated").description("인증 여부 (true)"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("memberId").description("인증된 사용자의 ID"),
                        fieldWithPath("role").description("인증된 사용자의 권한")
                )));
    }

    @Test
    @DisplayName("POST /api/auth/login - 사용자 로그인 테스트")
    void test2() throws Exception {
        // given

        LoginDto loginDto = LoginDto.builder()
                .email(dataInitComponent.TEST_EMAIL)
                .password(dataInitComponent.TEST_PASSWORD).build();

        // when + then
        ResultActions testMock = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().httpOnly("accessToken", true))
                .andExpect(cookie().path("accessToken", "/"))
                .andDo(print());

        // docs
        testMock.andDo(document("post-/api/auth/login",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("email").description("사용자 이메일"),
                        fieldWithPath("password").description("사용자 비밀번호")
                ),
                responseFields(
                        fieldWithPath("message").description("로그인 성공"),
                        fieldWithPath("statusCode").description("상태 코드")
                ),
                responseCookies(
                        cookieWithName("accessToken").description("HttpOnly, Path=/로 설정된 액세스 토큰")
                )
        ));

    }





}
