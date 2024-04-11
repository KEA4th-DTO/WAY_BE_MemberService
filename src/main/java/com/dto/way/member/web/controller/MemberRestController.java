package com.dto.way.member.web.controller;

import com.dto.way.member.domain.service.MemberService;
import com.dto.way.member.web.response.ApiResponse;
import com.dto.way.member.web.response.code.status.ErrorStatus;
import com.dto.way.member.web.response.code.status.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.dto.way.member.web.dto.MemberRequestDto.*;

@RestController
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberService memberService;

    @PostMapping("/signUp")
    public ApiResponse<CreateMemberRequestDto> signUp(@RequestBody CreateMemberRequestDto createMemberRequestDto) {

        if (memberService.createMember(createMemberRequestDto)) { // 회원가입에 성공한 경우
            return ApiResponse.of(SuccessStatus.MEMBER_SIGNUP, createMemberRequestDto);
        } else { // 회원가입에 실패한 경우
            return ApiResponse.onFailure(ErrorStatus.MEMBER_NICKNAME_DUPLICATED.getCode(),"회원가입 실패", createMemberRequestDto);
        }
    }
<<<<<<< Updated upstream
=======

    @PostMapping("/login")
    public JwtToken login(@RequestBody LoginMemberDto loginMemberDto) {

        JwtToken jwtToken = memberService.login(loginMemberDto);
        log.info("==========USER INFO==========");
        log.info("email = {} ", loginMemberDto.getEmail());
        log.info("==========JWT TOKEN==========");
        log.info("Access Token = {} ", jwtToken.getAccessToken());
        log.info("Refresh Token = {} ", jwtToken.getRefreshToken());

        return jwtToken;
    }

    @PostMapping("/testJwtToken")
    public String testJwtToken() {
        return "JWT TOKEN 성공!!!";
    }
>>>>>>> Stashed changes
}
