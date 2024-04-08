package com.dto.way.member.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

public class MemberRequestDto {

    @Getter
    @Setter
    public static class CreateMemberRequestDto {

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        private String name;

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "이메일 형식에 맞지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
                message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
        private String password;

        @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
        private String passwordCheck;

        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        @Pattern(regexp="^[A-Za-z0-9._]{5,30}$",
                message = "닉네임은 영문 대,소문자와 숫자, 특수문자(._)만 가능합니다. 길이는 5자 ~ 30자 입니다.")
        private String nickname;
    }
}