package com.house.hunter.service;

import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void sendEmail(MimeMessagePreparator mimeMessagePreparator);
}
