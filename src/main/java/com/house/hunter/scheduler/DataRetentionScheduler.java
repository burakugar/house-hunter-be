package com.house.hunter.scheduler;

import com.house.hunter.constant.UserAccountStatus;
import com.house.hunter.model.entity.Property;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.ConfirmationTokenRepository;
import com.house.hunter.repository.DocumentRepository;
import com.house.hunter.repository.ImageRepository;
import com.house.hunter.repository.PropertyRepository;
import com.house.hunter.repository.UserRepository;
import com.house.hunter.service.EmailService;
import com.house.hunter.util.MailUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@Transactional
public class DataRetentionScheduler {
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final ImageRepository imageRepository;
    private final PropertyRepository propertyRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;

    @Value("${data.retention.period}")
    private int retentionPeriod;

    @Value("${data.retention.reminder.days}")
    private int reminderDays;

    public DataRetentionScheduler(UserRepository userRepository,
                                  DocumentRepository documentRepository,
                                  ImageRepository imageRepository,
                                  PropertyRepository propertyRepository,
                                  ConfirmationTokenRepository confirmationTokenRepository,
                                  EmailService emailService,
                                  @Value("${data.retention.period}") int retentionPeriod,
                                  @Value("${data.retention.reminder.days}") int reminderDays) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.imageRepository = imageRepository;
        this.propertyRepository = propertyRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailService = emailService;
        this.retentionPeriod = retentionPeriod;
        this.reminderDays = reminderDays;
    }

    // Run every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    @org.springframework.transaction.annotation.Transactional
    // Run every day at midnight
    public void handleDataRetention() {
        // Calculate retention and reminder thresholds
        LocalDateTime retentionThreshold = LocalDateTime.now().minusDays(retentionPeriod);
        LocalDateTime reminderThreshold = LocalDateTime.now().minusDays(retentionPeriod - reminderDays);
        // Find users to delete and users to remind
        List<User> usersToDelete = userRepository.findByAccountStatusAndCreatedAtBefore(UserAccountStatus.ACTIVE, retentionThreshold).get();
        List<User> usersToRemind = userRepository.findByCreatedAtBetween(retentionThreshold, reminderThreshold).get().stream().toList();
        for (User user : usersToDelete) {
            // Delete associated documents, images, and properties
            documentRepository.deleteByUserId(user.getId());
            List<Property> properties = propertyRepository.findByOwnerId(user.getId()).get();
            for (Property property : properties) {
                imageRepository.deleteByPropertyId(property.getId());
                propertyRepository.delete(property);
            }
            confirmationTokenRepository.deleteByUserId(user.getId());
            // Delete the user
            userRepository.delete(user);
        }
        // Send reminder emails
        for (User user : usersToRemind) {
            // Send reminder email to the user
            MimeMessagePreparator reminderEmail = MailUtil.buildDataRetentionReminderEmail(user.getEmail(), reminderDays);
            emailService.sendEmail(reminderEmail);
        }
    }

}
