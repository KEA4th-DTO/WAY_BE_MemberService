package com.dto.way.member.web.controller;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.service.FollowService;
import com.dto.way.member.domain.service.MemberService;
import com.dto.way.member.global.JwtUtils;
import com.dto.way.member.web.dto.JwtToken;
import com.dto.way.member.web.response.ApiResponse;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
    private final JwtUtils jwtUtils;

    // 유저 정보 요청
    @Operation(summary = "프로필 조회 API", description = "path variable의 닉네임과 토큰 닉네임이 일치하면 isMyProfile이 true, 일치하지 않으면 false가 반환됩니다.")
    @GetMapping("/profile/{nickname}")
    public ApiResponse<GetProfileResponseDTO> getProfile(@PathVariable(value = "nickname") String nickname,
                                                         HttpServletRequest request) {

        // 토큰에서 요청 유저 정보 추출
        String token = jwtUtils.resolveToken(request);
        Claims claims = jwtUtils.parseClaims(token);

        Object loginNickname = claims.get("nickname");
        log.info("member Nickname: " + claims.get("nickname"));

        // 닉네임으로 프로필 조회 대상 멤버 정보를 가져옴
        Member profileMember = memberService.findMemberByNickname(nickname);

        if (Objects.equals(loginNickname.toString(), nickname)) { // 본인 프로필 조회인 경우

            GetProfileResponseDTO profile = memberService.createProfile(profileMember, true);
            return ApiResponse.of(_OK, profile);
        }

        else { // 남의 프로필 조회인 경우

            GetProfileResponseDTO profile = memberService.createProfile(profileMember, false);
            return ApiResponse.of(_OK, profile);
        }

    }

    // 프로필 수정
//    @PostMapping("/profile/{nickname}")
//    public ApiResponse updateProfile(@RequestBody UpdateProfileRequestDTO updateProfileRequestDTO,
//                                     @PathVariable(value = "nickname") String nickname,
//                                     HttpServletRequest request) {
//
//        // 토큰에서 요청 유저 정보 추출
//        String token = jwtUtils.resolveToken(request);
//        Claims claims = jwtUtils.parseClaims(token);
//
//        Object loginMemberId = claims.get("memberId");
//        log.info("loginMemberId " +  claims.get("memberId");
//
//        // 닉네임으로 프로필 조회 대상 멤버 정보를 가져옴
//        Member profileMember = memberService.findMemberByNickname(nickname);
//
//        // 토큰 유저 정보와 닉네임 유저 정보가 같아야만 수정이 가능하다.
//        if (profileMember.getId().toString().equals(loginMemberId.toString())) {
//
//        } else { // 일치하지 않는다면
//            return ApiResponse.onFailure();
//        }
//
//    }

    // 비밀번호 재설정
//    @PostMapping("/change/password")
//    public ApiResponse changePassword(@RequestBody ChangePasswordRequestDTO changePasswordRequestDTO) {
//
//    }

    // 닉네임 변경

    //


    @GetMapping("/member-info/id/{memberId}")
    public MemberInfoResponseDTO getMemberInfoByMemberId(@PathVariable Long memberId) {
        Member member = memberService.findMemberByMemberId(memberId);
        MemberInfoResponseDTO memberInfoResponseDTO = new MemberInfoResponseDTO();

        memberInfoResponseDTO.setMemberId(member.getId());
        memberInfoResponseDTO.setName(member.getName());
        memberInfoResponseDTO.setNickname(member.getNickname());
        memberInfoResponseDTO.setMemberStatus(member.getMemberStatus());
        memberInfoResponseDTO.setPhoneNumber(member.getPhoneNumber());
        memberInfoResponseDTO.setIntroduce(member.getIntroduce());
        memberInfoResponseDTO.setProfileImageUrl(member.getProfileImageUrl());

        return memberInfoResponseDTO;
    }

    @GetMapping("/member-info/nickname/{nickname}")
    public MemberInfoResponseDTO getMemberInfoByNickName(@PathVariable String nickname) {
        Member member = memberService.findMemberByNickname(nickname);
        MemberInfoResponseDTO memberInfoResponseDTO = new MemberInfoResponseDTO();

        memberInfoResponseDTO.setMemberId(member.getId());
        memberInfoResponseDTO.setName(member.getName());
        memberInfoResponseDTO.setNickname(member.getNickname());
        memberInfoResponseDTO.setMemberStatus(member.getMemberStatus());
        memberInfoResponseDTO.setPhoneNumber(member.getPhoneNumber());
        memberInfoResponseDTO.setIntroduce(member.getIntroduce());
        memberInfoResponseDTO.setProfileImageUrl(member.getProfileImageUrl());

        return memberInfoResponseDTO;
    }
}
