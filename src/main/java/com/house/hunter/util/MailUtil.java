package com.house.hunter.util;

import com.house.hunter.model.dto.user.RequestFormDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

public class MailUtil {
    private static final String LOGO_PATH = "static/logo.png";
    private static final int VERIFICATION_EXPIRATION_MINUTES = 30;
    private final static String HOST = "http://localhost:8080";

    public static MimeMessagePreparator buildRegistrationEmail(String recipientEmail, String verificationToken) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("House Hunter - Confirm your email address");

            String expirationTime = LocalDateTime.now().plusMinutes(VERIFICATION_EXPIRATION_MINUTES)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            String message = "<html><body>" +
                    "<img src='cid:logo' alt='House Hunter Logo' style='width: 200px; height: auto;'><br><br>" +
                    "<p>Thank you for registering with House Hunter. Please click on the below link to activate your account.</p>" +
                    "<p><a href='" + HOST + "/api/v1/user/activate-account/verify?token=" + verificationToken + "'>Verify Email</a></p>" +
                    "<p>This verification link will expire at " + expirationTime + ".</p>" +
                    "<p>If you did not register with House Hunter, please ignore this email.</p>" +
                    "</body></html>";

            messageHelper.setText(message, true);
            messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
        };
    }

    //TODO
    public static MimeMessagePreparator buildVerificationConfirmationEmail(String recipientEmail) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("House Hunter - Email Verification Confirmation");

            String message = "<html><body>" +
                    "<img src='cid:logo' alt='House Hunter Logo' style='width: 200px; height: auto;'><br><br>" +
                    "<p>Your email address has been successfully verified. You can now log in to your account.</p>" +
                    "</body></html>";

            messageHelper.setText(message, true);
            messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
        };
    }

    public static MimeMessagePreparator buildResetPasswordEmail(String recipientEmail, String resetToken) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("House Hunter - Reset your password");

            String message = "<html><body>" +
                    "<img src='cid:logo' alt='House Hunter Logo' style='width: 200px; height: auto;'><br><br>" +
                    "<p>You have requested to reset your password. Please click on the below link to reset your password:</p>" +
                    "<p><a href='http://localhost:3000/house-hunt-fe#/reset-password?token=" + resetToken + "'>Reset Password</a></p>" +
                    "<p>If you did not request a password reset, please ignore this email.</p>" +
                    "</body></html>";

            messageHelper.setText(message, true);
            messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
        };
    }

    public static MimeMessagePreparator buildRequestEmail(String recipientEmail, RequestFormDTO requestFormDTO) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("New Complaint Received");

            String message = "<html><body>" +
                    "<img src='cid:logo' alt='House Hunter Logo' style='width: 200px; height: auto;'><br><br>" +
                    "<p>A new complaint has been submitted:</p>" +
                    "<ul>" +
                    "<li>Name: " + requestFormDTO.getName() + "</li>" +
                    "<li>Email: " + requestFormDTO.getEmail() + "</li>" +
                    "<li>Subject: " + requestFormDTO.getSubject() + "</li>" +
                    "<li>Message: " + requestFormDTO.getMessage() + "</li>" +
                    (requestFormDTO.getPropertyId() != null ? "<li>Property ID: " + requestFormDTO.getPropertyId() + "</li>" : "") +
                    "</ul>" +
                    "</body></html>";

            messageHelper.setText(message, true);
            messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
        };
    }

    public static MimeMessagePreparator buildComplaintEmail(String recipientEmail, RequestFormDTO requestFormDTO) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("New Complaint Received");

            String message = "<html><body>" +
                    "<img src='cid:logo' alt='House Hunter Logo' style='width: 200px; height: auto;'><br><br>" +
                    "<p>A new complaint has been submitted.:</p>" +
                    "<ul>" +
                    "<li>Name: " + requestFormDTO.getName() + "</li>" +
                    "<li>Email: " + requestFormDTO.getEmail() + "</li>" +
                    "<li>Subject: " + requestFormDTO.getSubject() + "</li>" +
                    "<li>Message: " + requestFormDTO.getMessage() + "</li>" +
                    (requestFormDTO.getPropertyId() != null ? "<li>Property ID: " + requestFormDTO.getPropertyId() + "</li>" : "") +
                    "</ul>" +
                    "</body></html>";

            messageHelper.setText(message, true);
            messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
        };
    }

    public static MimeMessagePreparator buildDataRetentionReminderEmail(String recipientEmail, int reminderDays) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            String token = generateDataRetentionToken(recipientEmail);
            String encodedToken = Base64.getUrlEncoder().encodeToString(token.getBytes());
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("Data Retention Reminder");

            String message = "<html><body>" +
                    "<img src='cid:logo' alt='House Hunter Logo' style='width: 200px; height: auto;'><br><br>" +
                    "<p>Dear user,</p>" +
                    "<p>This is a reminder that your data will be deleted in " + reminderDays + " days in accordance with our data retention policy.</p>" +
                    "<p>If you wish to extend the retention period, please click on the following link:</p>" +
                    "<p><a href='" + HOST + "/api/v1/user/extend-retention?token=" + encodedToken + "'>Extend Data Retention</a></p>" +
                    "<p>Thank you for using House Hunter.</p>" +
                    "</body></html>";

            messageHelper.setText(message, true);
            messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
        };
    }

    public static MimeMessagePreparator buildPropertyRejectionEmail(String recipientEmail, String propertyTitle, UUID propertyId) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("Property Advertisement Rejected");

            String message = "<html><body>" +
                    "<img src='cid:logo' alt='House Hunter Logo' style='width: 200px; height: auto;'><br><br>" +
                    "<p>Dear property owner,</p>" +
                    "<p>We regret to inform you that your advertisement for the property with the following details has been rejected by our admins:</p>" +
                    "<ul>" +
                    "<li>Property Title: " + propertyTitle + "</li>" +
                    "<li>Property ID: " + propertyId + "</li>" +
                    "</ul>" +
                    "<p>Please verify the validity of the information provided and try again with a valid document by creating another property.</p>" +
                    "<p>If you have any questions or concerns, please don't hesitate to contact our support team.</p>" +
                    "<p>Thank you for using House Hunter.</p>" +
                    "</body></html>";

            messageHelper.setText(message, true);
            messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
        };
    }

    public static MimeMessagePreparator buildPropertyVerificationEmail(String recipientEmail, String propertyTitle, UUID propertyId) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("Property Advertisement Verified");

            String message = "<html><body>" +
                    "<img src='cid:logo' alt='House Hunter Logo' style='width: 200px; height: auto;'><br><br>" +
                    "<p>Dear property owner,</p>" +
                    "<p>We are pleased to inform you that your advertisement for the property with the following details has been verified by our admins:</p>" +
                    "<ul>" +
                    "<li>Property Title: " + propertyTitle + "</li>" +
                    "<li>Property ID: " + propertyId + "</li>" +
                    "</ul>" +
                    "<p>Your property is now visible to potential tenants/buyers on our platform.</p>" +
                    "<p>Thank you for using House Hunter.</p>" +
                    "</body></html>";

            messageHelper.setText(message, true);
            messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
        };
    }

    public static MimeMessagePreparator buildUserActivationEmail(String recipientEmail) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("Account Activated");

            String message = "<html><body>" +
                    "<img src='cid:logo' alt='House Hunter Logo' style='width: 200px; height: auto;'><br><br>" +
                    "<p>Dear user,</p>" +
                    "<p>Your account has been activated by an administrator. You can now log in and start using House Hunter.</p>" +
                    "<p>Thank you for using House Hunter.</p>" +
                    "</body></html>";

            messageHelper.setText(message, true);
            messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
        };
    }

    public static MimeMessagePreparator buildUserNotVerifiedEmail(String recipientEmail) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("Account Verification Status Changed");

            String message = "<html><body>" +
                    "<img src='cid:logo' alt='House Hunter Logo' style='width: 200px; height: auto;'><br><br>" +
                    "<p>Dear user,</p>" +
                    "<p>We regret to inform you that your account verification status has been changed to 'Not Verified' by an administrator.</p>" +
                    "<p>Please contact our support team for further assistance.</p>" +
                    "<p>Thank you for using House Hunter.</p>" +
                    "</body></html>";

            messageHelper.setText(message, true);
            messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
        };
    }

    public static MimeMessagePreparator buildUserBlockedEmail(String recipientEmail) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipientEmail);
            messageHelper.setSubject("Account Blocked");

            String message = "<html><body>" +
                    "<img src='cid:logo' alt='House Hunter Logo' style='width: 200px; height: auto;'><br><br>" +
                    "<p>Dear user,</p>" +
                    "<p>We regret to inform you that your account has been blocked by an administrator.</p>" +
                    "<p>If you believe this action was taken in error, please contact our support team for further assistance.</p>" +
                    "<p>Thank you for using House Hunter.</p>" +
                    "</body></html>";

            messageHelper.setText(message, true);
            messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
        };
    }


    private static String generateDataRetentionToken(String email) {
        // Generate a unique token based on the user's email and current timestamp
        final String token = email + "_" + System.currentTimeMillis();
        return token;
    }

}
