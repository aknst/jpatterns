package ru.mirea.prac22.aspects;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class LogAspect {
  @Around("execution(public * ru.mirea.prac22.*.*.*(..))")
  public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    long executionTime = System.currentTimeMillis() - startTime;

    String methodName = joinPoint.getSignature().getName();
    Class<?> declaringType = joinPoint.getSignature().getDeclaringType();
    log.info("Executed {} on {}, took {} ms with arguments {}",
        methodName, declaringType.getSimpleName(), executionTime, Arrays.toString(joinPoint.getArgs()));

    return result;
  }
}
