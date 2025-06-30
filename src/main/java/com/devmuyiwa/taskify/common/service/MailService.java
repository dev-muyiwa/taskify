package com.devmuyiwa.taskify.common.service;

import com.devmuyiwa.taskify.common.events.WorkspaceMemberCreatedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${resend.from}")
    private String fromAddress;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWorkspaceMemberCreated(WorkspaceMemberCreatedEvent event) {
        System.out.println("Received workspace member created event: " + event);
        String to = event.email();
        String subject = "Welcome to Taskify!";
        String body = String.format(
                "<h1>Welcome %s!</h1><p>You have created an account.</p>",
                event.firstName()
        );
        sendMail(to, subject, body);

        System.out.println("Welcome email sent to: " + to);
    }

    private void sendMail(String to, String subject, String body) {
        System.out.println("Starting to send email to: " + to);
        try {
            System.out.println("Creating MimeMessage...");
            MimeMessage message = mailSender.createMimeMessage();
            
            System.out.println("Setting up MimeMessageHelper...");
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            System.out.println("Sending email...");
            mailSender.send(message);
            System.out.println("Email sent successfully!");
            
        } catch (MessagingException e) {
            System.err.println("MessagingException while sending email to " + to + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
