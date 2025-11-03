package HeoJin.demoBlog.global.exception;

import HeoJin.demoBlog.global.exception.common.CustomException;
import jakarta.servlet.http.HttpServletResponse;

public class CustomNotFound extends CustomException {

    private final static String MESSAGE = "존재하지 않는 entity 입니다. : ";

    public CustomNotFound(String entity) {
        super(MESSAGE + entity);
    }

    @Override
    public int getStatusCode() {
        return HttpServletResponse.SC_NOT_FOUND; // 404
    }
}
