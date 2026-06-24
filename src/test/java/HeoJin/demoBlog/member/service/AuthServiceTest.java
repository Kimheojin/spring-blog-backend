package HeoJin.demoBlog.member.service;

import HeoJin.demoBlog.global.jwt.JwtTokenProvider;
import HeoJin.demoBlog.member.dto.request.LoginDto;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.entity.RefreshToken;
import HeoJin.demoBlog.member.entity.Role;
import HeoJin.demoBlog.member.repository.MemberRepository;
import HeoJin.demoBlog.member.repository.RefreshTokenRepository;
import HeoJin.demoBlog.member.dto.data.LoginResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;

    @Test
    @DisplayName("로그인 성공 - 새로운 RefreshToken 생성")
    void test1() {
        // given
        LoginDto loginDto = new LoginDto("test@example.com", "password123!");
        
        Member member = Member.builder()
                .id(1L)
                .email("test@example.com")
                .role(Role.builder().roleName("Admin").build())
                .build();

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(userDetails.getUsername()).willReturn("test@example.com");
        given(memberRepository.findByEmail("test@example.com")).willReturn(Optional.of(member));
        
        given(jwtTokenProvider.generateToken(anyLong(), anyString(), anyString())).willReturn("access-token");
        given(jwtTokenProvider.generateRefreshToken(anyLong())).willReturn("refresh-token");

        // 이거 value 매핑 하는 등등 참고하기
        given(jwtTokenProvider.getRefreshTokenExpirationMillis()).willReturn(3600000L); // 1 시간

        given(refreshTokenRepository.findByMemberId(member.getId())).willReturn(Optional.empty());

        // when
        LoginResult loginResult = authService.login(loginDto);

        // then
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(memberRepository).findByEmail("test@example.com");
        verify(jwtTokenProvider).generateToken(eq(member.getId()), eq(member.getEmail()), anyString());
        verify(jwtTokenProvider).generateRefreshToken(member.getId());
        verify(refreshTokenRepository).save(any(RefreshToken.class));


        assertThat(loginResult.accessToken()).isEqualTo("access-token");
        assertThat(loginResult.refreshToken()).isEqualTo("refresh-token");
        assertThat(loginResult.newRefreshToken()).isTrue();
    }

    @Test
    @DisplayName("로그인 성공 - 기존 RefreshToken 업데이트")
    void test2() {
        // given
        LoginDto loginDto = new LoginDto("test@example.com", "password123!");

        Member member = Member.builder()
                .id(1L)
                .email("test@example.com")
                .role(Role.builder()
                        .roleName("ADMIN")
                        .build())
                .build();

        RefreshToken existingRefreshToken = RefreshToken.builder()
                .id(1L)
                .memberId(member.getId())
                .token("old-refresh-token")
                .build();

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(userDetails.getUsername()).willReturn("test@example.com");
        given(memberRepository.findByEmail("test@example.com")).willReturn(Optional.of(member));

        given(jwtTokenProvider.generateToken(anyLong(), anyString(), anyString())).willReturn("access-token");
        given(jwtTokenProvider.generateRefreshToken(anyLong())).willReturn("new-refresh-token");
        given(jwtTokenProvider.getRefreshTokenExpirationMillis()).willReturn(3600000L);

        given(refreshTokenRepository.findByMemberId(member.getId())).willReturn(Optional.of(existingRefreshToken));

        // when
        LoginResult loginResult = authService.login(loginDto);

        // then
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
        assertThat(loginResult.newRefreshToken()).isFalse();
    }

    @Test
    @DisplayName("로그아웃 성공")
    void test3() {
        // given
        given(jwtTokenProvider.validateToken("valid-access-token")).willReturn(true);
        given(jwtTokenProvider.getMemberId("valid-access-token")).willReturn(1L);

        // when
        boolean result = authService.logout("valid-access-token");

        // then
        verify(refreshTokenRepository).deleteByMemberId(1L);
        assertThat(result).isTrue();
    }
}
