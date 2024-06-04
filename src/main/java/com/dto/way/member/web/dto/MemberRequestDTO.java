package com.dto.way.member.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class MemberRequestDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CheckNicknameRequestDTO {

        @NotBlank(message = "값을 입력해주세요.")
        @Pattern(regexp="^[A-Za-z0-9._]{4,10}$",
                message = "닉네임은 영문 대,소문자와 숫자, 특수문자(._)만 가능합니다. 길이는 4자 ~ 10자 입니다.")
        private String nickname;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CheckEmailRequestDTO {

        @NotBlank(message = "값을 입력해주세요.")
        @Email(message = "이메일 형식에 맞지 않습니다.")
        private String email;

    }

    @Getter
    @Setter
    public static class ChangePasswordRequestDTO {

        @NotBlank(message = "값을 입력해주세요.")
        private String oldPassword;

        @NotBlank(message = "값을 입력해주세요.")
        private String newPassword;

        @NotBlank(message = "값을 입력해주세요.")
        private String newPasswordCheck;

    }

    @Getter
    @Setter
    public static class UpdateProfileRequestDTO {

        @NotBlank(message = "값을 입력해주세요.")
        @Pattern(regexp="^[A-Za-z0-9._]{4,10}$",
                message = "닉네임은 영문 대,소문자와 숫자, 특수문자(._)만 가능합니다. 길이는 4자 ~ 10자 입니다.")
        private String nickname;

        private String introduce;

    }
}
