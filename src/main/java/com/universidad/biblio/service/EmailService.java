package com.universidad.biblio.service;

import com.universidad.biblio.model.User;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean enabled;
    private final String from;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                        @Value("${codexlibrary.mail.enabled:false}") boolean enabled,
                        @Value("${codexlibrary.mail.from:no-reply@codexlibrary.local}") String from) {
        this.mailSenderProvider = mailSenderProvider;
        this.enabled = enabled;
        this.from = from;
    }

    public boolean sendNotification(User user, String subject, String message) {
        if (!enabled) {
            logger.info("Email notification skipped: codexlibrary.mail.enabled=false");
            return false;
        }

        if (user == null) {
            logger.warn("Email notification skipped: target user is null");
            return false;
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            logger.warn("Email notification skipped: user {} has no email", user.getId());
            return false;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            logger.warn("Email notification skipped for {}: JavaMailSender is not configured", user.getEmail());
            return false;
        }

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(from);
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailSender.send(mailMessage);
            logger.info("Email notification sent to {}", user.getEmail());
            return true;
        } catch (MailException ex) {
            logger.error("Email notification failed for {}", user.getEmail(), ex);
            return false;
        }
    }
}
