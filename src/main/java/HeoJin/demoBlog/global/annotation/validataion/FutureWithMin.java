package HeoJin.demoBlog.global.annotation.validataion;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = FutureWithMinValidator.class) // 제약조건
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureWithMin {
    String message() default "KST 기준 30 분 이상 차이나야 합니다..";
    Class<?>[] groups() default {}; // 언제 검사할지 
    Class<? extends Payload>[] payload() default {}; // 실패 시 어떤 부가정보 붙일 지
    int minutes() default 30;
}
