package org.example.sentinelcorebackend.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendAlertEmail_shouldConstructAndSendMail() {
        String toEmail = "admin@example.com";
        String assetName = "Prod Server";
        String severity = "CRITICAL";
        String messageText = "Disk usage is 99%";

        notificationService.sendAlertEmail(toEmail, assetName, severity, messageText);

        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mailCaptor.capture());

        SimpleMailMessage capturedMail = mailCaptor.getValue();
        assertNotNull(capturedMail);
        assertArrayEquals(new String[]{toEmail}, capturedMail.getTo());
        assertEquals("[SentinelCore] CRITICAL Alert: Prod Server", capturedMail.getSubject());
        assertEquals("Disk usage is 99%", capturedMail.getText());
    }

    @Test
    void sendAlertResolvedEmail_shouldConstructAndSendMail() {
        String toEmail = "admin@example.com";
        String assetName = "Prod Server";
        String severity = "CRITICAL";
        String messageText = "Disk usage is back to normal";

        notificationService.sendAlertResolvedEmail(toEmail, assetName, severity, messageText);

        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mailCaptor.capture());

        SimpleMailMessage capturedMail = mailCaptor.getValue();
        assertNotNull(capturedMail);
        assertArrayEquals(new String[]{toEmail}, capturedMail.getTo());
        assertEquals("[SentinelCore] RESOLVED: CRITICAL Alert on Prod Server", capturedMail.getSubject());
        assertTrue(capturedMail.getText().contains("RESOLVED"));
    }
}
