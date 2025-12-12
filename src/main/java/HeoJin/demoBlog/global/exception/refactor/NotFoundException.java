package HeoJin.demoBlog.global.exception.refactor;

import HeoJin.demoBlog.global.exception.common.CustomException;

public class NotFoundException extends CustomException {

    private final int statusCode;

    public NotFoundException(String message, int status){
        super(message);
        this.statusCode = status;
    }

    public NotFoundException(String message, Throwable cause, int status){
        super(message, cause);
        this.statusCode = status;
    }

    @Override
    public int getStatusCode(){
        return statusCode;
    }
}
