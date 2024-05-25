package com.dto.way.member.web.controller;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.service.MemberService;
import com.dto.way.member.web.dto.JwtToken;
import com.dto.way.member.web.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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

    // 프로필 수정

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
