package com.dto.way.member.domain.service;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.dto.way.member.domain.entity.MemberAuth.*;
import static com.dto.way.member.web.dto.MemberRequestDto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public boolean createMember(CreateMemberRequestDto createMemberRequestDto) {

        if (isEqualPassword(createMemberRequestDto)) return false;

        // 이메일 중복 검사

        // 닉네임 중복 검사

        Member member = Member.builder()
                .name(createMemberRequestDto.getName())
                .email(createMemberRequestDto.getEmail())
                .password(createMemberRequestDto.getPassword())
                .nickname(createMemberRequestDto.getNickname())
                .createdAt(LocalDateTime.now())
                .memberAuth(CLIENT)
                .build();

        memberRepository.save(member);

        return true;

    }

    // 비밀번호와 비밀번화 확인이 같은지 체크하는 메소드
    private static boolean isEqualPassword(CreateMemberRequestDto createMemberRequestDto) {
        if (!createMemberRequestDto.getPassword().equals(createMemberRequestDto.getPasswordCheck())) {
            return true;
        }
        return false;
    }
}
