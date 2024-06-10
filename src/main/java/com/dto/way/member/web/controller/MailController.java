package com.dto.way.member.web.controller;

import com.dto.way.member.domain.service.MailService;
import com.dto.way.member.web.dto.EmailCertificationRequest;
import com.dto.way.member.web.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

import static com.dto.way.member.web.response.code.status.ErrorStatus.*;
import static com.dto.way.member.web.response.code.status.SuccessStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member-service")
public class MailController {

    private final MailService mailSendService;

    // 이메일 인증 API
    @Operation(summary = "이메일 인증 메일 전송 API", description = "인증 메일을 받아야 할 사용자의 이메일을 request body에 넣어주세요.")
    @PostMapping("/send-mail-certification")
    public ApiResponse sendMailCertification(@Validated @RequestBody EmailCertificationRequest request) throws MessagingException, NoSuchAlgorithmException {

        mailSendService.sendEmailForCertification(request.getEmail());
        return ApiResponse.of(EMAIL_SENDED, null);
    }

}
