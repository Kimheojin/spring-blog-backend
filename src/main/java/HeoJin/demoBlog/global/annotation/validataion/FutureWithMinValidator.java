package HeoJin.demoBlog.global.annotation.validataion;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class FutureWithMinValidator implements ConstraintValidator<FutureWithMin, LocalDateTime> {


    private int minutes;
    @Override
    public void initialize(FutureWithMin constraintAnnotation) {
        this.minutes = constraintAnnotation.minutes();
    }
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null 값 처리는 다른 annotation 에서
        }
        return value.isAfter(LocalDateTime.now().plusMinutes(minutes)); // 입력하 시간 이후
    }
}
