package com.auction.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 이메일 전송 서비스
 */
@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 이메일 전송 메서드
     *
     * @param to    받는 사람 이메일
     * @param subject 이메일 제목
     * @param text 이메일 본문 (HTML 가능)
     */
    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // HTML 형식

            mailSender.send(message);
            System.out.println("✅ 이메일 전송 완료: " + to);
        } catch (MessagingException e) {
            System.out.println("❌ 이메일 전송 실패: " + e.getMessage());
        }
    }
}