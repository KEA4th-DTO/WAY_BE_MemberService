package com.dto.way.member.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTagRequestDTO {

    private Long userId;
    private String imageUrl;
    private String textUrl;
}