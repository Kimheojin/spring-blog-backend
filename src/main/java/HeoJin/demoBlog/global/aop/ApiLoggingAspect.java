package HeoJin.demoBlog.global.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
@Aspect
public class ApiLoggingAspect {


//    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String apiName = joinPoint
                .getSignature()
                .getDeclaringType().getSimpleName()
                + " . " + joinPoint.getSignature().getName();

        log.info("==== API 시작 {} ====", apiName);
        try {
            Object result = joinPoint.proceed();
            log.info("=== API 완료: {} ===", apiName);
            return result;
        } catch (Exception e) {
            log.error("=== API 에러: {} - {}  ===", apiName,e.getMessage());
            throw e;
        }
    }
}
