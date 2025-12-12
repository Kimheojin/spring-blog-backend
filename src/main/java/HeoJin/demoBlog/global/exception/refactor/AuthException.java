package HeoJin.demoBlog.global.exception.refactor;

import HeoJin.demoBlog.global.exception.common.CustomException;

public class AuthException extends CustomException {

    private final BusinessErrorCode errorCode;

    public AuthException(BusinessErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthException(BusinessErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    @Override
    public int getStatusCode() {
        return errorCode.getStatus();
    }

    public String getErrorCode() {
        return errorCode.getCode();
    }
}
