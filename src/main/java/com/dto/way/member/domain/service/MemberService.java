package com.dto.way.member.domain.service;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.repository.MemberRepository;
import com.dto.way.member.web.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.dto.way.member.web.dto.MemberResponseDTO.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FollowService followService;

    // 닉네임 중복 검사 메소드
    public boolean checkNicknameDuplication(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    // 이메일 중복 검사 메소드
    public boolean checkEmailDuplication(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Member findMemberByEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        return member.orElse(null);
    }

    @Transactional(readOnly = true)
    public Member findMemberByNickname(String nickname) {
        Optional<Member> member = memberRepository.findByNickname(nickname);
        return member.orElse(null);
    }

    @Transactional(readOnly = true)
    public Member findMemberByMemberId(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        return member.orElse(null);
    }

    public GetProfileResponseDTO createProfile(Member profileMember, Boolean isMyProfile) {

        // 팔로잉, 팔로워 수를 가져온다.
        Long followingCount = followService.getFollowingCount(profileMember.getId());
        Long followerCount = followService.getFollowerCount(profileMember.getId());

        // 프로필을 만든다.
        GetProfileResponseDTO getProfileResponseDTO = GetProfileResponseDTO.builder()
                .name(profileMember.getName())
                .profileImageUrl(profileMember.getProfileImageUrl())
                .introduce(profileMember.getIntroduce())
                .nickname(profileMember.getNickname())
                .postCount(100L) // API 개발되고 나면 수정할 예정
                .followingCount(followingCount)
                .followerCount(followerCount)
                .isMyProfile(isMyProfile)
                .build();

        return getProfileResponseDTO;
    }
}
