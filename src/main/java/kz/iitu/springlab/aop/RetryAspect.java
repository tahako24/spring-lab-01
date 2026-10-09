package kz.iitu.springlab.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(0) // Выше остальных, чтобы управлять повторами на самом внешнем уровне
public class RetryAspect {

    private static final Logger log = LoggerFactory.getLogger(RetryAspect.class);

    @Around("@annotation(retryOnFailure)")
    public Object retry(ProceedingJoinPoint pjp, RetryOnFailure retryOnFailure) throws Throwable {
        int maxAttempts = retryOnFailure.attempts();
        long delay = retryOnFailure.delay();
        Throwable lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                log.info("[RETRY] Attempt {} of {} for method: {}",
                        attempt, maxAttempts, pjp.getSignature().toShortString());
                return pjp.proceed();
            } catch (Throwable ex) {
                lastException = ex;
                log.warn("[RETRY] Attempt {} failed with error: {}. Retrying in {}ms...",
                        attempt, ex.getMessage(), delay);
                if (attempt < maxAttempts) {
                    Thread.sleep(delay);
                }
            }
        }

        log.error("[RETRY] All {} attempts failed for method: {}", maxAttempts, pjp.getSignature().toShortString());
        throw lastException;
    }
}