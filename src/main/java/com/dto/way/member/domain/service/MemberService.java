package com.dto.way.member.domain.service;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.repository.MemberRepository;
import com.dto.way.member.global.JwtTokenProvider;
import com.dto.way.member.web.dto.JwtToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.dto.way.member.domain.entity.MemberAuth.*;
import static com.dto.way.member.web.dto.MemberRequestDto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    public boolean createMember(CreateMemberRequestDto createMemberRequestDto) {

        if (isEqualPassword(createMemberRequestDto)) return false;

        // 이메일 중복 검사

        // 닉네임 중복 검사

        // 비밀번호 암호화
        String password = passwordEncoder.encode(createMemberRequestDto.getPassword());

        Member member = Member.builder()
                .name(createMemberRequestDto.getName())
                .email(createMemberRequestDto.getEmail())
                .password(password)
                .nickname(createMemberRequestDto.getNickname())
                .createdAt(LocalDateTime.now())
                .memberAuth(CLIENT)
                .build();

        memberRepository.save(member);

        return true;

    }

    public JwtToken login(LoginMemberDto loginMemberDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginMemberDto.getEmail(), loginMemberDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return jwtToken;
    }



    // 비밀번호와 비밀번화 확인이 같은지 체크하는 메소드
    private static boolean isEqualPassword(CreateMemberRequestDto createMemberRequestDto) {
        if (!createMemberRequestDto.getPassword().equals(createMemberRequestDto.getPasswordCheck())) {
            return true;
        }
        return false;
    }
}
