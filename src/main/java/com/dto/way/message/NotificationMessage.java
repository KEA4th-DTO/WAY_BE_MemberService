package com.dto.way.message;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class NotificationMessage {

    private final String message;
    private LocalDateTime createdAt;

}
