package HeoJin.demoBlog.member.service;


import HeoJin.demoBlog.global.exception.refactor.NotFoundException;
import HeoJin.demoBlog.global.jwt.JwtTokenProvider;
import HeoJin.demoBlog.member.dto.request.LoginDto;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.member.entity.RefreshToken;
import HeoJin.demoBlog.member.repository.MemberRepository;
import HeoJin.demoBlog.member.repository.RefreshTokenRepository;
import HeoJin.demoBlog.member.dto.data.LoginResult;
import lombok.RequiredArgsConstructor;
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
    public LoginResult login(LoginDto loginDto) {
        // 인증 수행 (이메일과 패스워드 검증)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        // UserDetails에서 Member 정보 추출
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(
                member.getId(),
                member.getEmail(),
                member.getRole().getRoleName()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId());

        boolean created = upsertRefreshToken(member.getId(), refreshToken);

        return new LoginResult(accessToken, refreshToken, created);
    }

    @Transactional
    public boolean logout(String accessToken) {
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            Long memberId = jwtTokenProvider.getMemberId(accessToken);
            refreshTokenRepository.deleteByMemberId(memberId);
            SecurityContextHolder.clearContext();
            return true;
        }
        SecurityContextHolder.clearContext();
        return false;
    }

    private boolean upsertRefreshToken(Long memberId, String refreshToken) {
        LocalDateTime expiryDate = LocalDateTime.now()
                .plusSeconds(jwtTokenProvider.getRefreshTokenExpirationMillis() / 1000);

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByMemberId(memberId);

        if (existingToken.isPresent()) {
            existingToken.get().updateToken(refreshToken, expiryDate);
            return false;
        }

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(refreshToken)
                .memberId(memberId)
                .expiryDate(expiryDate)
                .build();
        refreshTokenRepository.save(newRefreshToken);
        return true;
    }
}
