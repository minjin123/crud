package com.site.Services;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class EmailService {


    private final JavaMailSender mailSender;
    private final Map<String, String> idVerificationMap = new ConcurrentHashMap<>();
    private final Map<String,Map<String,String>> passwordVerificationMap = new ConcurrentHashMap<>();


    public void VerificationForId(String email, String verificationCode) {
        sendVerificationCodeToEmail(email, verificationCode);
        idVerificationMap.put(email, verificationCode);
    }
    public void VerificationForPassword(String id, String email, String verificationCode) {
        sendVerificationCodeToEmail(email, verificationCode);
        passwordVerificationMap.putIfAbsent(id, new ConcurrentHashMap<>());
        passwordVerificationMap.get(id).put(email, verificationCode);
    }
    @Async
    public void sendVerificationCodeToEmail(String email, String verificationCode) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(email);
            helper.setSubject("게시판 회원 인증번호");
            helper.setText("인증 번호 코드:"+verificationCode);
            mailSender.send(mimeMessage);

            //디버깅
            System.out.println("이메일 전송 완료: " + email);
            System.out.println("인증번호:"+verificationCode);

        }catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyVerificationCodeForId(String email, String verificationCode) {
        String code = idVerificationMap.get(email);
        return code != null && code.equals(verificationCode);
    }
    public boolean verifyVerificationCodeForPassword(String username, String email, String verificationCode) {
        String code = passwordVerificationMap.get(username).get(email);
        return code != null && code.equals(verificationCode);
    }
    public String generateVerificationCode() {
        return String.valueOf((int) ((Math.random() * 899999) + 100000)); // 6자리 랜덤 숫자 생성
    }

}
