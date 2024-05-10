package com.dto.way.member.domain.service;

import com.dto.way.message.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j(topic = "follow")
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    public NotificationMessage followNotificationCreate(String message) {
        NotificationMessage notificationMessage = new NotificationMessage(message);
        notificationMessage.setCreatedAt(LocalDateTime.now());
        log.info("팔로우 알림 전송! 메세지: {} ", message);
        kafkaTemplate.send("follow-notification", notificationMessage);

        return notificationMessage;
    }
}
