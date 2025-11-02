package HeoJin.demoBlog.global.exception.image;

import HeoJin.demoBlog.global.exception.common.CustomException;
import jakarta.servlet.http.HttpServletResponse;

public class CloudinaryCustomRuntimeException extends CustomException {

    /*
     * https://cloudinary.com/documentation/admin_api#error_handling
     * -> 클라우디너리 error 종류
     * -> Free 플랜인 경우 자세히 못보는듯
     */

    private final int statusCode; // extractStatusCode에서 초기화

    public CloudinaryCustomRuntimeException(Exception e) {
        super(e.getMessage(), e);
        this.statusCode = extractStatusCode(e);
    }

    public CloudinaryCustomRuntimeException(String message, Exception e) {
        super(message, e);
        this.statusCode = extractStatusCode(e);
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Cloudinary 예외에서 HTTP 상태코드를 추출
     * 직접 뽑아내고 싶은데 안되는듯
     */
    private int extractStatusCode(Exception e) {
        if (e == null || e.getMessage() == null) {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR; // 500
        }

        String message = e.getMessage().toLowerCase();

        // 400 Bad Request 관련
        if (message.contains("bad request") ||
                message.contains("invalid") ||
                message.contains("file size") ||
                message.contains("unsupported format") ||
                message.contains("malformed")) {
            return HttpServletResponse.SC_BAD_REQUEST; // 400
        }

        // 401 Authorization required 관련
        if (message.contains("unauthorized") ||
                message.contains("authentication") ||
                message.contains("invalid api key") ||
                message.contains("api key")) {
            return HttpServletResponse.SC_UNAUTHORIZED; // 401
        }

        // 403 Not allowed 관련
        if (message.contains("forbidden") ||
                message.contains("not allowed") ||
                message.contains("permission denied")) {
            return HttpServletResponse.SC_FORBIDDEN; // 403
        }

        // 404 Not found 관련
        if (message.contains("not found") ||
                message.contains("resource not found")) {
            return HttpServletResponse.SC_NOT_FOUND; // 404
        }

        // 409 Already exists 관련
        if (message.contains("already exists") ||
                message.contains("conflict")) {
            return HttpServletResponse.SC_CONFLICT; // 409
        }

        // 420 Rate limited 관련 (Cloudinary 특별 코드)
        if (message.contains("rate limit") ||
                message.contains("too many requests")) {
            return 420; // Rate limited
        }

        // 기본값: 500 Internal Server Error
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR; // 500
    }
}