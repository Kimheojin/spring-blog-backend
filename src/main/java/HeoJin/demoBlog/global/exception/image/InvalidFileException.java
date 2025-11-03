package HeoJin.demoBlog.global.exception.image;

import HeoJin.demoBlog.global.exception.common.CustomException;
import jakarta.servlet.http.HttpServletResponse;

public class InvalidFileException extends CustomException {

    private static final String MESSAGE = "유효하지 않은 파일입니다.";

    public InvalidFileException() {
        super(MESSAGE);
    }

    public InvalidFileException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpServletResponse.SC_BAD_REQUEST; // 400
    }
}
