package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String username;

    public void sendEmail(String to, String subject, String text) {
        try {
            log.info("Попытка отправки письма на: {}", to);
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(username);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            emailSender.send(message);
        } catch (Exception e) {
            log.error("Ошибка отправки: {}", e.getMessage());
            throw new RuntimeException("Ошибка отправки письма", e);
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String text,
                                        byte[] fileData, String fileName) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(username);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            // Используем ByteArrayResource вместо InputStreamResource
            helper.addAttachment(fileName, new ByteArrayResource(fileData) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            });

            emailSender.send(message);
            log.info("Письмо с вложением успешно отправлено на {}", to);

        } catch (MessagingException e) {
            log.error("Ошибка отправки: {}", e.getMessage());
            throw new CommonBackendException("Ошибка отправки email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
