package HeoJin.demoBlog.member.service;


import HeoJin.demoBlog.global.jwt.JwtTokenProvider;
import HeoJin.demoBlog.member.dto.request.LoginDto;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.entity.RefreshToken;
import HeoJin.demoBlog.member.repository.MemberRepository;
import HeoJin.demoBlog.member.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void login(LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        // 로그인 로직은 SecurityContext 저장 X


        // 인증 수행
        // 이메일로 찾아와서 password 검증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        // UserDetails에서 Member 정보 추출
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(
                member.getId(),
                member.getEmail(),
                member.getRole().getRoleName()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId());

        // AccessToken을 쿠키에 저장 (SameSite=None 설정)
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true) // HTTPS 관련 설정
                .path("/")
                .maxAge(60 * 60 * 24) // 1일 (설정에 맞게 조정)
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", accessTokenCookie.toString());


        // RefreshToken을 DB에 저장
        LocalDateTime expiryDate = LocalDateTime.now()
                .plusSeconds(jwtTokenProvider.getRefreshTokenExpirationMillis() / 1000);

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByMemberId(member.getId());

        if (existingToken.isPresent()) {
            // 기존 토큰이 있으면 업데이트
            existingToken.get().updateToken(refreshToken, expiryDate);
        } else {
            // 없으면 새로 생성
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .token(refreshToken)
                    .memberId(member.getId())
                    .expiryDate(expiryDate)
                    .build();
            refreshTokenRepository.save(newRefreshToken);
        }
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 accessToken 가져오기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    String token = cookie.getValue();

                    // 토큰에서 memberId 추출
                    if (jwtTokenProvider.validateToken(token)) {
                        Long memberId = jwtTokenProvider.getMemberId(token);

                        // DB에서 RefreshToken 삭제
                        refreshTokenRepository.deleteByMemberId(memberId);
                    }
                }
            }
        }

        // SecurityContext 클리어
        SecurityContextHolder.clearContext();

        // accessToken 쿠키 삭제 (SameSite=None 설정)
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(true) // login과 설정 일치
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
    }
}
