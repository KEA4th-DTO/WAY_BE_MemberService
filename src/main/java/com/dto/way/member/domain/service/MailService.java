package com.dto.way.member.domain.service;

import com.dto.way.member.global.CertificationGenerator;
import com.dto.way.member.web.response.EmailCertificationResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;
    private final CertificationGenerator certificationGenerator;

    public EmailCertificationResponse sendEmailForCertification(String email) throws NoSuchAlgorithmException, MessagingException {

        String certificationNumber = certificationGenerator.createCertificationNumber();
        String content = String.format("%s/member-service/verify?certificationNumber=%s&email=%s   해당 링크로 10분 이내에 접속해주세요.", "localhost:8080", certificationNumber, email);

        try {
            sendMail(email, content);
            return new EmailCertificationResponse(email, certificationNumber);
        } catch (MessagingException e) { // 메일 전송에 실패한 경우
            log.error("메일 전송 실패! 유효하지 않은 이메일: {}", email);
            throw e;
        }
    }

    private void sendMail(String email, String content) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(email);
        helper.setSubject("인증 메일 입니다.");
        helper.setText(content);
        javaMailSender.send(mimeMessage);
    }
}
