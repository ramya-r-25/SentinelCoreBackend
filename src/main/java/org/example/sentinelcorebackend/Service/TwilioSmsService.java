package org.example.sentinelcorebackend.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    public String sendSms(String to, String messageText) {
        try {
            Twilio.init(accountSid, authToken);

            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(twilioPhoneNumber),
                    messageText
            ).create();

            System.out.println("SentinelCore SMS sent successfully!");
            System.out.println("Twilio Message SID: " + message.getSid());

            return message.getSid();
        } catch (Exception e) {
            System.err.println("Twilio SMS failed (e.g. Trial quota exceeded): " + e.getMessage());
            return null;
        }
    }
}