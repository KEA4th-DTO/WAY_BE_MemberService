package com.dto.way.member.web.converter;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.web.dto.MemberResponseDTO;

import static com.dto.way.member.web.dto.MemberResponseDTO.*;

public class FeignConverter {

    public static MemberInfoResponseDTO toMemberInfoResponseDTO(Member member) {
        return MemberInfoResponseDTO.builder()
                .memberId(member.getId())
                .name(member.getName())
                .nickname(member.getNickname())
                .memberStatus(member.getMemberStatus())
                .phoneNumber(member.getPhoneNumber())
                .introduce(member.getIntroduce())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
