package com.house.hunter.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserServiceLoggingAspect implements LoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceLoggingAspect.class);

    @Override
    public Logger getLogger() {
        return LOGGER;
    }
}

