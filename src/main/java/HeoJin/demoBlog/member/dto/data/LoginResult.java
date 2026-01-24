package HeoJin.demoBlog.member.dto.data;

// 로그인 성공 시 controller 로 반환
// 나중에 class 변환 고려
public record LoginResult(
        String accessToken,
        String refreshToken,
        boolean newRefreshToken
) {
}
