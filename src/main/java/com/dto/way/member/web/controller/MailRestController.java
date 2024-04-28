package com.dto.way.member.web.controller;

import com.dto.way.member.domain.service.MailService;
import com.dto.way.member.web.dto.EmailCertificationRequest;
import com.dto.way.member.web.response.ApiResponse;
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
public class MailRestController {

    private final MailService mailSendService;

    @PostMapping("/sendMailCertification")
    public ApiResponse sendMailCertification(@Validated @RequestBody EmailCertificationRequest request) throws MessagingException, NoSuchAlgorithmException {

        mailSendService.sendEmailForCertification(request.getEmail());
        return ApiResponse.of(EMAIL_SENDED, null);
    }

    @GetMapping("/verify")
    public ApiResponse verifyEmail(@RequestParam String email,
                                     @RequestParam String certificationNumber) {

        if (mailSendService.verifyEmail(email, certificationNumber)) {
            return ApiResponse.of(EMAIL_VERIFIED, null);
        } else {
            return ApiResponse.onFailure(EMAIL_NOT_VERIFIED.getCode(), EMAIL_NOT_VERIFIED.getMessage(), null);
        }

    }
}
