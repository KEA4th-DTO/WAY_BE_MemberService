package com.dto.way.member.web.controller;

import com.dto.way.member.domain.service.MemberService;
import com.dto.way.member.domain.service.RedisService;
import com.dto.way.member.web.dto.JwtToken;
import com.dto.way.member.web.response.ApiResponse;
import com.dto.way.member.web.response.code.status.ErrorStatus;
import com.dto.way.member.web.response.code.status.SuccessStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.dto.way.member.web.dto.MemberRequestDto.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member-service")
public class MemberRestController {

    private final MemberService memberService;

    @PostMapping("/signUp")
    public ApiResponse<CreateMemberRequestDto> signUp(@RequestBody CreateMemberRequestDto createMemberRequestDto) {

        if (memberService.createMember(createMemberRequestDto)) { // 회원가입에 성공한 경우
            return ApiResponse.of(SuccessStatus.MEMBER_SIGNUP, createMemberRequestDto);
        } else { // 회원가입에 실패한 경우
            return ApiResponse.onFailure(ErrorStatus.MEMBER_NICKNAME_DUPLICATED.getCode(), "회원가입 실패", createMemberRequestDto);
        }
    }

    @PostMapping("/login")
    public JwtToken login(@RequestBody LoginMemberRequestDto loginMemberRequestDto) {

        JwtToken jwtToken = memberService.login(loginMemberRequestDto);
        log.info("==========USER INFO==========");
        log.info("email = {} ", loginMemberRequestDto.getEmail());
        log.info("==========JWT TOKEN==========");
        log.info("Access Token = {} ", jwtToken.getAccessToken());
        log.info("Refresh Token = {} ", jwtToken.getRefreshToken());


        return jwtToken;
    }

    @PostMapping("/logout")
    public ApiResponse<JwtToken> logout(@RequestBody JwtToken jwtToken) {
        memberService.logout(jwtToken);

        return ApiResponse.of(SuccessStatus.MEMBER_LOGOUT, jwtToken);
    }

    @PostMapping("/testJwtToken")
    public String testJwtToken() {

        return "JWT TOKEN 성공!!!";
    }

    @PostMapping("/recreateToken")
    public JwtToken recreateToken(@RequestBody JwtToken jwtToken) {
        String refreshToken = jwtToken.getRefreshToken();
        return memberService.checkRefreshTokenisValid(refreshToken);
    }
}
