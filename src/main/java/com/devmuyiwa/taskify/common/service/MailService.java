package com.devmuyiwa.taskify.common.service;

import com.devmuyiwa.taskify.common.events.PasswordResetTokenGeneratedEvent;
import com.devmuyiwa.taskify.common.events.UserRegisteredEvent;
import com.devmuyiwa.taskify.common.events.WorkspaceMemberCreatedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWorkspaceMemberCreated(WorkspaceMemberCreatedEvent event) {
        try {
            String to = event.email();
            String subject = "Welcome to Taskify!";
            String body = String.format(
                    "Hi %s,\n\nWelcome to Taskify! Your workspace has been created successfully.\n\nBest regards,\nThe Taskify Team",
                    event.firstName()
            );

            sendEmailAsync(to, subject, body, false);
        } catch (Exception e) {
            log.error("Failed to send welcome email:", e);
            // Don't rethrow to avoid affecting the main flow
        }
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePasswordResetTokenGenerated(PasswordResetTokenGeneratedEvent event) {
        try {
            String to = event.email();
            String subject = "Password Reset Request - Taskify";

            // Use Thymeleaf template for HTML email
            sendPasswordResetEmailAsync(to, subject, event.firstName(), event.token(), event.expirationTime().toMinutes());
        } catch (Exception e) {
            log.error("Failed to send password reset email:", e);
        }
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(UserRegisteredEvent event) {
        String to = event.email();
        try {
            String subject = "Verify Your Email - Taskify";

            // Send HTML email with verification link
            sendVerificationEmailAsync(to, subject, event.firstName(), event.verificationToken(), event.expirationMinutes());
        } catch (Exception e) {
            log.error("Failed to send email verification email:", e);
        }
    }

    private void sendEmailAsync(String to, String subject, String body, boolean isHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, isHtml);
            
            mailSender.send(message);
            
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private void sendVerificationEmailAsync(String to, String subject, String firstName, String verificationToken, int expirationMinutes) {
        // Compose the full verification URL with token
        String fullVerificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;

        // Use Thymeleaf template
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("verificationToken", verificationToken);
        context.setVariable("verificationUrl", fullVerificationUrl);
        context.setVariable("expirationMinutes", expirationMinutes);

        String htmlContent = templateEngine.process("email/account_verification", context);

        // Use the unified email sending function
        sendEmailAsync(to, subject, htmlContent, true);
    }

    private void sendPasswordResetEmailAsync(String to, String subject, String firstName, String resetToken, long expirationMinutes) {
        // Compose the full reset URL with token as query parameter
        String fullResetUrl = frontendUrl + "/reset-password?token=" + resetToken;

        // Use Thymeleaf template for password reset email
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("resetUrl", fullResetUrl);
        context.setVariable("expirationMinutes", expirationMinutes);

        String htmlContent = templateEngine.process("email/password-reset", context);

        // Use the unified email sending function
        sendEmailAsync(to, subject, htmlContent, true);
    }


}
