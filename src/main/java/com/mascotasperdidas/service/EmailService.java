package com.mascotasperdidas.service;

import com.mascotasperdidas.model.NoticeDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final String EMAIL_PARAM = "email";

    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailForNotice(NoticeDTO notice) {
        log.info("Enviando notificaciones de notice: {}", notice);
        String email = notice.getContactInfo().get(EMAIL_PARAM);
        if (email == null) {
            return;
        }
        String body = String.format("Tu aviso fue creado correctamente. " +
                        "Ingresa <a href=\"app.com/%s/%s\" target=\"_blank\">aquí</a> para poder modificarlo."
                , notice.getNoticeId()
                , notice.getToken());
        try {
            this.send(
                    email,
                    String.format("Aviso %s creado", notice.getTitle()),
                    body);
        } catch (MessagingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void send(String destinatary, String subject, String body) throws MessagingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
        helper.setFrom(username);
        helper.setTo(destinatary);
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(msg);
    }
}
