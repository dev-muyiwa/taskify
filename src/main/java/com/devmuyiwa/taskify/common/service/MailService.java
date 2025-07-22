package com.devmuyiwa.taskify.common.service;

import com.devmuyiwa.taskify.common.events.PasswordResetTokenGeneratedEvent;
import com.devmuyiwa.taskify.common.events.WorkspaceMemberCreatedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${resend.from}")
    private String fromAddress;

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWorkspaceMemberCreated(WorkspaceMemberCreatedEvent event) {
        try {
            log.info("Sending welcome email to: {}", event.email());
            
            String to = event.email();
            String subject = "Welcome to Taskify!";
            String body = String.format(
                    "Hi %s,\n\nWelcome to Taskify! Your workspace has been created successfully.\n\nBest regards,\nThe Taskify Team",
                    event.firstName()
            );

            sendEmailAsync(to, subject, body);
            log.info("Welcome email sent successfully to: {}", event.email());
            
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", event.email(), e);
            // Don't rethrow to avoid affecting the main flow
        }
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePasswordResetTokenGenerated(PasswordResetTokenGeneratedEvent event) {
        try {
            log.info("Sending password reset email to: {}", event.email());
            
            String to = event.email();
            String subject = "Password Reset Request";
            String body = String.format(
                    "You requested a password reset. Use this token: %s\n\nThis token expires in %d minutes.\n\nIf you didn't request this, please ignore this email.",
                    event.token(),
                    event.expirationTime().toMinutes()
            );

            sendEmailAsync(to, subject, body);
            log.info("Password reset email sent successfully to: {}", event.email());
            
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", event.email(), e);
        }
    }

    private void sendEmailAsync(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            
            mailSender.send(message);
            
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
