package com.dto.way.member.domain.service;

import com.dto.way.member.domain.entity.Follow;
import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.repository.FollowRepository;
import com.dto.way.member.domain.repository.MemberRepository;
import com.dto.way.member.web.dto.FollowDto;
import com.dto.way.member.web.exception.FollowException;
import com.dto.way.member.web.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.dto.way.member.web.response.code.status.ErrorStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    @Transactional
    public String follow(Member from_member, Member to_member) {

        // 자기 자신 follow 불가능
        if (from_member == to_member) {
            throw new FollowException(FOLLOW_CANNOT_SELF);
        }

        // 중복 follow 불가능
        if (followRepository.findFollow(from_member, to_member).isPresent()) {
            throw new FollowException(FOLLOW_NOT_DUPLICATED);
        }

        Follow follow = Follow.builder()
                .toMember(to_member)
                .fromMember(from_member)
                .build();

        followRepository.save(follow);

        return "success";
    }

    @Transactional(readOnly = true)
    public List<FollowDto> followingList(Member selectedMember, Member loginMember) {
        List<Follow> list = followRepository.findByFromMember(selectedMember);

        return list.stream()
                .map(follow -> {
                    FollowDto followDto = new FollowDto();
                    followDto.setName(follow.getToMember().getName());
                    followDto.setNickname(follow.getToMember().getNickname());
                    followDto.setProfileImageUrl(follow.getToMember().getProfileImageUrl());
                    followDto.setStatus(findStatus(selectedMember, loginMember));
                    return followDto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FollowDto> followerList(Member selectedMember, Member loginMember) {
        List<Follow> list = followRepository.findByToMember(selectedMember);

        return list.stream()
                .map(follow -> {
                    FollowDto followDto = new FollowDto();
                    followDto.setName(follow.getFromMember().getName());
                    followDto.setNickname(follow.getFromMember().getNickname());
                    followDto.setProfileImageUrl(follow.getFromMember().getProfileImageUrl());
                    followDto.setStatus(findStatus(selectedMember, loginMember));
                    return followDto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    protected String findStatus(Member selectedMember, Member loginMember) {
        if (selectedMember.getEmail().equals(loginMember.getEmail())) {
            return "self";
        }

        if (followRepository.findFollow(selectedMember, loginMember).isEmpty()) {
            return "none";
        }

        return "following";
    }

    @Transactional
    public String cancelFollow(Member from_member, Member to_Member) {
        followRepository.deleteByFromMemberAndToMember(from_member, to_Member);
        return "success";
    }
}
