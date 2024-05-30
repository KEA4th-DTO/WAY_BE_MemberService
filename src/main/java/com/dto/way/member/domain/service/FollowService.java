package com.dto.way.member.domain.service;

import com.dto.way.member.domain.entity.Follow;
import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.repository.FollowRepository;
import com.dto.way.member.web.dto.FollowResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.dto.way.member.web.dto.FollowResponseDTO.*;
import static com.dto.way.member.web.response.code.status.ErrorStatus.*;
import static com.dto.way.member.web.response.code.status.SuccessStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    @Transactional
    public String follow(Member from_member, Member to_member) {

        // 자기 자신 follow 불가능
        if (from_member == to_member) {
            return FOLLOW_CANNOT_SELF.getCode();
        }

        // 중복 follow 불가능
        else if (followRepository.findFollow(from_member, to_member).isPresent()) {
            return FOLLOW_NOT_DUPLICATED.getCode();
        } else {
            Follow follow = Follow.builder()
                    .toMember(to_member)
                    .fromMember(from_member)
                    .build();

            followRepository.save(follow);

            return FOLLOW_SUCCESS.getCode();
        }
    }

    @Transactional(readOnly = true)
    public List<MemberInfoResponseDTO> followingList(Member selectedMember) {
        List<Follow> list = followRepository.findByFromMember(selectedMember);

        return list.stream()
                .map(follow -> {
                    MemberInfoResponseDTO memberInfoResponseDTO = new MemberInfoResponseDTO();
                    memberInfoResponseDTO.setMemberId(follow.getToMember().getId());
                    memberInfoResponseDTO.setName(follow.getToMember().getName());
                    memberInfoResponseDTO.setNickname(follow.getToMember().getNickname());
                    memberInfoResponseDTO.setProfileImageUrl(follow.getToMember().getProfileImageUrl());
                    return memberInfoResponseDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MemberInfoResponseDTO> followerList(Member selectedMember) {
        List<Follow> list = followRepository.findByToMember(selectedMember);

        return list.stream()
                .map(follow -> {
                    MemberInfoResponseDTO memberInfoResponseDTO = new MemberInfoResponseDTO();
                    memberInfoResponseDTO.setMemberId(follow.getFromMember().getId());
                    memberInfoResponseDTO.setName(follow.getFromMember().getName());
                    memberInfoResponseDTO.setNickname(follow.getFromMember().getNickname());
                    memberInfoResponseDTO.setProfileImageUrl(follow.getFromMember().getProfileImageUrl());
                    return memberInfoResponseDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Boolean findStatus(Member loginMember, Member selectedMember) {

        return followRepository.findFollow(loginMember, selectedMember).isPresent();

    }

    @Transactional
    public String cancelFollow(Member from_member, Member to_Member) {
        followRepository.deleteByFromMemberAndToMember(from_member, to_Member);
        return "success";
    }

    // 팔로워 수를 return
    @Transactional(readOnly = true)
    public Long getFollowingCount(Long memberId) {
        return followRepository.countByFromMemberId(memberId);
    }

    // 팔로잉 수를 return
    @Transactional(readOnly = true)
    public Long getFollowerCount(Long memberId) {
        return followRepository.countByToMemberId(memberId);
    }

}
