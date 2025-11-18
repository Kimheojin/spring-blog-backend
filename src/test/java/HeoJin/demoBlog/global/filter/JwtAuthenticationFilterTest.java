package HeoJin.demoBlog.global.filter;


import HeoJin.demoBlog.global.jwt.JwtTokenProvider;
import HeoJin.demoBlog.member.repository.MemberRepository;
import HeoJin.demoBlog.member.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private MemberRepository memberRepository;
    // 직접 생성하므로, injectMock 없어도 될거 같음
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setup(){
        jwtAuthenticationFilter = new JwtAuthenticationFilter(tokenProvider, refreshTokenRepository, memberRepository);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Jwt 필터 동작 테스트")
    void teest1() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        String testJwtValue = "testJwtValue";
        request.setCookies(new Cookie("accessToken", testJwtValue));

        when(tokenProvider.validateToken(testJwtValue)).thenReturn(true);
        when(tokenProvider.getMemberId(testJwtValue)).thenReturn(1L);
        when(tokenProvider.getEmail(testJwtValue)).thenReturn("test@test.com");
        when(tokenProvider.getRole(testJwtValue)).thenReturn("ROLE");

        // when

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        // 정확히 한번 호출하는 지 확인
        verify(filterChain).doFilter(request, response);
        // 객체 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(1L);
        assertThat(authentication.getName()).isEqualTo("1");


    }
}
