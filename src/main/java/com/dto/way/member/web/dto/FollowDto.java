package com.dto.way.member.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowDto {

    private String profileImageUrl;
    private String nickname;
    private String name;
    private String status;

}
