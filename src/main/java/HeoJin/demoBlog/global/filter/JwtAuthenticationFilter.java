package HeoJin.demoBlog.global.filter;

import HeoJin.demoBlog.global.jwt.JwtTokenProvider;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.entity.RefreshToken;
import HeoJin.demoBlog.member.repository.MemberRepository;
import HeoJin.demoBlog.member.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// 인증 처리랑
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter는 특이하게 빈 등록 가능한듯

    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 쿠키에서 token 가져옴
        String token = resolveToken(request);

        if(token != null) {
            // 토큰이 유효한 경우
            if(tokenProvider.validateToken(token)){
                authenticateUser(token, request);
            }
            // 토큰이 만료된 경우
            else if(tokenProvider.isTokenExpired(token)){
                // RefreshToken으로 AccessToken 갱신 시도
                String newAccessToken = refreshAccessToken(token);

                if(newAccessToken != null) {
                    // 새로운 AccessToken을 쿠키에 설정
                    ResponseCookie newAccessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
                            .httpOnly(true)
                            .secure(true) // HTTPS 환경에서는 true로 설정
                            .path("/")
                            .maxAge(60 * 60 * 24) // 1일
                            .sameSite("None")
                            .build();

                    response.addHeader(HttpHeaders.SET_COOKIE, newAccessTokenCookie.toString());

                    // 새 토큰으로 인증 처리
                    authenticateUser(newAccessToken, request);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    // 토큰으로 사용자 인증 처리
    private void authenticateUser(String token, HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(token);
        String email = tokenProvider.getEmail(token);
        String role = tokenProvider.getRole(token);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        UsernamePasswordAuthenticationToken authentication = new
                UsernamePasswordAuthenticationToken(memberId, null, authorities);

        // 요청에 대한 부가데이터 들어감
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // AccessToken 갱신 로직
    private String refreshAccessToken(String expiredToken) {
        try {
            // 만료된 토큰에서 memberId 추출
            Long memberId = tokenProvider.getMemberIdFromExpiredToken(expiredToken);

            // DB에서 RefreshToken 조회
            Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByMemberId(memberId);

            if(refreshTokenOpt.isPresent()) {
                RefreshToken refreshToken = refreshTokenOpt.get();

                // RefreshToken이 만료되지 않았고 유효한지 확인
                if(!refreshToken.isExpired() && tokenProvider.validateToken(refreshToken.getToken())) {
                    // Member 정보 조회
                    Optional<Member> memberOpt = memberRepository.findById(memberId);

                    if(memberOpt.isPresent()) {
                        Member member = memberOpt.get();

                        // 새로운 AccessToken 발급
                        return tokenProvider.generateToken(
                                member.getId(),
                                member.getEmail(),
                                member.getRole().getRoleName()
                        );
                    }
                }
            }
        } catch (Exception e) {
            // 예외 발생 시 null 반환 (갱신 실패)
            return null;
        }
        return null;
    }

    private String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        // 쿠키가 없는 경우
        if (cookies == null) {
            return null;
        }

        // 쿠키 배열에서 "accessToken" 이름의 쿠키를 찾아서 값 반환
        return Arrays.stream(cookies)
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}