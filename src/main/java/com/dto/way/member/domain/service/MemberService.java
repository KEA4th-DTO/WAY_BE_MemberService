package com.dto.way.member.domain.service;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

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
}
