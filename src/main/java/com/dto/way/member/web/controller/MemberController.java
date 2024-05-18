package com.dto.way.member.web.controller;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.service.MemberService;
import com.dto.way.member.web.dto.JwtToken;
import com.dto.way.member.web.dto.MemberResponseDTO;
import com.dto.way.member.web.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.dto.way.member.web.dto.MemberRequestDTO.*;
import static com.dto.way.member.web.dto.MemberResponseDTO.*;
import static com.dto.way.member.web.response.code.status.SuccessStatus.*;
import static com.dto.way.member.web.response.code.status.ErrorStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member-service")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ApiResponse<CreateMemberRequestDTO> signUp(@Valid @RequestBody CreateMemberRequestDTO createMemberRequestDTO) {

        String result = memberService.createMember(createMemberRequestDTO);

        if (result.equals(MEMBER_SIGNUP.getCode())) { // 회원가입에 성공한 경우
            return ApiResponse.of(MEMBER_SIGNUP, createMemberRequestDTO);
        }

        else if (result.equals(MEMBER_EMAIL_DUPLICATED.getCode())) { // 이메일 중복인 경우
            return ApiResponse.onFailure(MEMBER_EMAIL_DUPLICATED.getCode(), MEMBER_EMAIL_DUPLICATED.getMessage(), createMemberRequestDTO);
        }

        else if (result.equals(MEMBER_NICKNAME_DUPLICATED.getCode())) { // 닉네임 중복인 경우
            return ApiResponse.onFailure(MEMBER_NICKNAME_DUPLICATED.getCode(), MEMBER_NICKNAME_DUPLICATED.getMessage(), createMemberRequestDTO);
        }

        else { // 비밀번호가 일치하지 않는 경우
            return ApiResponse.onFailure(MEMBER_PASSWORD_NOT_MATCHED.getCode(), MEMBER_PASSWORD_NOT_MATCHED.getMessage(), createMemberRequestDTO);
        }
    }

    @PostMapping("/login")
    public ApiResponse<LoginMemberResponseDTO> login(@Valid @RequestBody LoginMemberRequestDTO loginMemberRequestDTO) {

        JwtToken jwtToken = memberService.login(loginMemberRequestDTO);

        LoginMemberResponseDTO loginMemberResponseDTO = new LoginMemberResponseDTO();

        if (jwtToken.getGrantType().equals(MEMBER_LOGIN_FAILED.getCode())) {
            return ApiResponse.onFailure(MEMBER_LOGIN_FAILED.getCode(), MEMBER_LOGIN_FAILED.getMessage(), loginMemberResponseDTO);
        } else {
            Member loginMember = memberService.findMemberByEmail(loginMemberRequestDTO.getEmail());
            loginMemberResponseDTO.setLoginMember(loginMember);
            loginMemberResponseDTO.setJwtToken(jwtToken);

            return ApiResponse.of(MEMBER_LOGIN, loginMemberResponseDTO);
        }
    }

    @PostMapping("/logout")
    public ApiResponse<JwtToken> logout(@RequestBody JwtToken jwtToken) {
        memberService.logout(jwtToken);

        return ApiResponse.of(MEMBER_LOGOUT, jwtToken);
    }

    @GetMapping("/testCICD")
    public String testCICD() {

        return "OK";
    }

    @PostMapping("/recreate-token")
    public JwtToken recreateToken(@RequestBody JwtToken jwtToken) {
        String refreshToken = jwtToken.getRefreshToken();
        return memberService.checkRefreshTokenisValid(refreshToken);
    }
}
