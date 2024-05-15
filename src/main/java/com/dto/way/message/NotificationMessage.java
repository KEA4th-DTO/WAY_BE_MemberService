package com.dto.way.message;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {

    private String message;
    private String sendedMember;
    private LocalDateTime createdAt;

}
