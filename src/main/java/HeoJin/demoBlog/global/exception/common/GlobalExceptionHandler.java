package HeoJin.demoBlog.global.exception.common;


import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        int statusCode = e.getStatusCode();

        ErrorResponse response = ErrorResponse.builder()
                .message(e.getMessage())
                .statusCode(statusCode)
                .build();

        // validation 데이터가 있으면 복사
        if (!e.getValidation().isEmpty()) {
            e.getValidation().forEach(response::addValidation);
        }

        return ResponseEntity.status(statusCode).body(response);
    }

    // @Valid 관련 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        ErrorResponse response = ErrorResponse.builder()
                .message("입력값 검증에 실패했습니다.")
                .statusCode(400)
                .build();

        // 필드별 오류 메시지 추가 되는듯
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            response.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(response);
    }

    // 필수 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException e) {
        ErrorResponse response = ErrorResponse.builder()
                .message("필수 파라미터가 누락되었습니다.")
                .statusCode(400)
                .build();

        response.addValidation(e.getParameterName(), e.getParameterName() + "는 필수 파라미터입니다.");

        return ResponseEntity.badRequest().body(response);
    }

}
