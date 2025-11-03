package HeoJin.demoBlog.global.exception;


import HeoJin.demoBlog.global.exception.common.CustomException;
import org.springframework.http.HttpStatus;

public class ExistCategoryPostException extends CustomException {


    private static final String MESSAGE = "해당 카테고리에 post가 존재합니다.";
    public ExistCategoryPostException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value(); // 400
    }
}
