package com.dto.way.member.web.dto;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.entity.MemberStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class MemberResponseDTO {

    @Getter
    @Setter
    public static class LoginMemberResponseDTO {

        private String name;
        private String nickname;
        private String email;
        private JwtToken jwtToken;
    }

    @Getter
    @Setter
    public static class MemberInfoResponseDTO {
        private Long memberId;
        private String name;
        private String nickname;
        private String profileImageUrl;
        private String introduce;
        private MemberStatus memberStatus;
        private String phoneNumber;
    }

    @Getter
    @Setter
    @Builder
    public static class GetProfileResponseDTO {

        private String name;

        private String profileImageUrl;

        private String introduce;

        private String nickname;

        private Long dailyCount;

        private Long historyCount;

        private Long followingCount;

        private Long followerCount;

        private Boolean isMyProfile;

    }
}

