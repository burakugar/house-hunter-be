package com.house.hunter.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;

public interface LoggingAspect {

    Logger getLogger();

    default void logBeforeMethod(JoinPoint joinPoint) {
        getLogger().info("Executing: {} operation", joinPoint.getSignature());
    }

    default void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (result == null) {
            getLogger().info("Finished execution of: {}", joinPoint.getSignature());
            return;
        }
        getLogger().info("Finished execution of: {}, Returned value: {}", joinPoint.getSignature(), result);
    }

    default void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        getLogger().error("Exception in method: {}, Exception: {}", joinPoint.getSignature(), exception);
    }

    @Before("execution(* com.house.hunter.service.impl.*.*(..))")
    default void beforeMethod(JoinPoint joinPoint) {
        logBeforeMethod(joinPoint);
    }

    @AfterReturning(pointcut = "execution(* com.house.hunter.service.impl.*.*(..))", returning = "result")
    default void afterReturning(JoinPoint joinPoint, Object result) {
        logAfterReturning(joinPoint, result);
    }

    @AfterThrowing(pointcut = "execution(* com.house.hunter.service.impl.*.*(..))", throwing = "exception")
    default void afterThrowing(JoinPoint joinPoint, Throwable exception) {
        logAfterThrowing(joinPoint, exception);
    }

}
