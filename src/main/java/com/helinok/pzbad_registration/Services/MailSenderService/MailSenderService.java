package com.helinok.pzbad_registration.Services.MailSenderService;

import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.resend.*;

import java.time.LocalDateTime;


@Service
@Slf4j
public class MailSenderService {
    private final Resend resend;
    private static final String FROM = "noreply@badistration.com";

    public MailSenderService(@Value("${resend.api-key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void sendMail(String to, String subject, String body) {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(FROM)
                .to(to)
                .subject(subject)
                .text(body)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            log.info("Email with id {} sent to {} at {}",data.getId(), to, LocalDateTime.now());
        } catch (ResendException e) {
            e.printStackTrace();
        }
    }
}
