package com.dto.way.member.web.controller;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.service.MemberService;
import com.dto.way.member.domain.service.NotificationService;
import com.dto.way.member.web.response.ApiResponse;
import com.dto.way.message.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dto.way.member.web.response.code.status.SuccessStatus.NOTIFICATION_SENDED;
import static com.dto.way.member.web.response.code.status.ErrorStatus.NOTIFICATION_NOT_SENDED;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member-service/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberService memberService;

    @PostMapping("/follow")
    public ApiResponse<NotificationMessage> sendFollowNotification(Authentication authentication) {
        String email = authentication.getName();
        Member member = memberService.findMemberByEmail(email);
        if (member != null) {
            String nickname = member.getNickname();
            String message = nickname + "님이 팔로우 했습니다.";
            NotificationMessage notificationMessage = notificationService.followNotificationCreate(message);

            return ApiResponse.of(NOTIFICATION_SENDED, notificationMessage);
        }

        else {
            NotificationMessage notificationMessage = notificationService.followNotificationCreate("다시 시도해주세요!");
            return ApiResponse.onFailure(NOTIFICATION_NOT_SENDED.getCode(), NOTIFICATION_NOT_SENDED.getMessage(), notificationMessage);
        }
    }
}
