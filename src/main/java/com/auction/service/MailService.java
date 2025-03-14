package com.auction.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 이메일 전송 서비스 클래스
 * JavaMailSender를 사용하여 이메일을 발송하는 기능을 제공한다.
 */
@Service
public class MailService {

    private static final Logger LOGGER = Logger.getLogger(MailService.class.getName());
    private final JavaMailSender mailSender;

    /**
     * 생성자 - JavaMailSender 주입
     *
     * @param mailSender JavaMailSender 객체
     */
    public MailService(final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 이메일 전송 메서드
     *
     * @param to      받는 사람 이메일
     * @param subject 이메일 제목
     * @param text    이메일 본문 (HTML 가능)
     */
    public void sendEmail(final String to, final String subject, final String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // HTML 형식 지원

            mailSender.send(message);
            LOGGER.info("✅ 이메일 전송 성공: " + to);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ 이메일 전송 실패: " + e.getMessage(), e);
        }
    }
}