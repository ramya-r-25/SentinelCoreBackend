package org.example.sentinelcorebackend.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    public void sendAlertEmail(
            String toEmail,
            String assetName,
            String severity,
            String message) {

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(toEmail);
        mail.setSubject(
                "[SentinelCore] " + severity + " Alert: " + assetName
        );

        mail.setText(message);

        mailSender.send(mail);
    }

    public void sendAlertResolvedEmail(
            String toEmail,
            String assetName,
            String severity,
            String message) {

        try {
            SimpleMailMessage mail = new SimpleMailMessage();

            mail.setTo(toEmail);
            mail.setSubject(
                    "[SentinelCore] RESOLVED: " + severity + " Alert on " + assetName
            );

            mail.setText("The following alert has been RESOLVED:\n\n"
                    + "Asset: " + assetName + "\n"
                    + "Severity: " + severity + "\n"
                    + "Message: " + message);

            mailSender.send(mail);
            System.out.println("=== SENTINELCORE RESOLVED ALERT EMAIL SENT SUCCESSFULLY TO: " + toEmail + " ===");
        } catch (Exception e) {
            System.err.println("Failed to send resolved alert email: " + e.getMessage());
        }
    }
}