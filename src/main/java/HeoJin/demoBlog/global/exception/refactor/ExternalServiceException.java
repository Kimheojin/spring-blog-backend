package HeoJin.demoBlog.global.exception.refactor;

import HeoJin.demoBlog.global.exception.common.CustomException;

public class ExternalServiceException extends CustomException {

    private final int statusCode;

    public ExternalServiceException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }


    public ExternalServiceException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
}
