package HeoJin.demoBlog.global.exception.refactor;

import HeoJin.demoBlog.global.exception.common.CustomException;

public class BusinessException extends CustomException {

    private final int statusCode;

    public BusinessException(String message, int status){
        super(message);
        this.statusCode = status;
    }

    public BusinessException(String message, Throwable cause, int status){
        super(message, cause);
        this.statusCode = status;
    }

    @Override
    public int getStatusCode(){
        return statusCode;
    }
}
