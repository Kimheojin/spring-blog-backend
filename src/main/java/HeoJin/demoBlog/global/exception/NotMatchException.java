package HeoJin.demoBlog.global.exception;

import HeoJin.demoBlog.global.exception.common.CustomException;
import jakarta.servlet.http.HttpServletResponse;

public class NotMatchException extends CustomException {

    private final static String MESSAGE = "비밀번호와 아이디가 일치하지 않습니다.";

    public NotMatchException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpServletResponse.SC_NOT_FOUND; // 404
    }
}
