package com.dto.way.member.web.converter;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.web.dto.FollowResponseDTO;

import static com.dto.way.member.web.dto.FollowResponseDTO.*;

public class FollowConverter {

    public static IsFollowResponseDTO toIsFollowResponseDTO(Member fromMember, Member toMember, Boolean isFollowing) {
        return IsFollowResponseDTO.builder()
                .fromMember(fromMember.getId())
                .toMember(toMember.getId())
                .isFollowing(isFollowing)
                .build();
    }
}
