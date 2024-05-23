package com.dto.way.member.web.dto;

import com.dto.way.member.domain.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    public static class MemberInfoResponseDTO {
        private String name;
        private String nickname;
        private String profileImageUrl;
        private String introduce;
        private String memberStatus;
        private String phoneNumber;
    }
}
