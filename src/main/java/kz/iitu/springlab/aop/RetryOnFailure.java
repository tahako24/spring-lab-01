package kz.iitu.springlab.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RetryOnFailure {
    int attempts() default 3;
    long delay() default 1000; // задержка между попытками в миллисекундах
}