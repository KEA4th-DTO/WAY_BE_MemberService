package com.dto.way.member.web.controller;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.entity.MemberStatus;
import com.dto.way.member.domain.service.MemberService;
import com.dto.way.member.global.AmazonS3Manager;
import com.dto.way.member.global.JwtUtils;
import com.dto.way.member.web.dto.MemberResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dto.way.member.web.dto.MemberResponseDTO.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member-service")
public class FeignController {

    private final MemberService memberService;

    @Operation(summary = "멤버정보 조회 API", description = "(프론트 사용 X) path variable에 조회하고 싶은 멤버의 memberId를 넣어주세요.")
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

    @Operation(summary = "멤버정보 조회 API", description = "(프론트 사용 X) path variable에 조회하고 싶은 멤버의 nickname을 넣어주세요.")
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

    @Operation(summary = "memberStatus로 멤버목록을 조회하는 API", description = "(프론트 사용 X) path variable에 memberStatus를 넣어주세요.")
    @GetMapping("/member-list/{memberStatus}")
    public List<MemberInfoResponseDTO> getMemberListByMemberStatus(@PathVariable MemberStatus memberStatus) {
        return memberService.findMembersByMemberStatus(memberStatus);
    }
}
