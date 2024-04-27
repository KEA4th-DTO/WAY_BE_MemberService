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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

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
    private final RedisService redisService;

    public boolean createMember(CreateMemberRequestDto createMemberRequestDto) {

        if (isEqualPassword(createMemberRequestDto)) return false;

        // 이메일 중복 검사

        // 닉네임 중복 검사

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

    public JwtToken login(LoginMemberRequestDto loginMemberRequestDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginMemberRequestDto.getEmail(), loginMemberRequestDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        saveRefreshToken(jwtToken.getRefreshToken(), authentication, Duration.ofDays(1));

        return jwtToken;
    }

    public void logout(JwtToken jwtToken) {
        redisService.deleteValues(jwtToken.getRefreshToken());
    }

    public JwtToken checkRefreshTokenisValid(String refreshToken) {

        if (redisService.getValues(refreshToken).equals("false")) { // 리프레시 토큰이 만료되었거나 없는 겅우

            return new JwtToken("Refresh Token이 만료되었거나 없습니다.", null, null);

        } else { // redis에 있는 email로 user정보를 가져온다.
            Optional<Member> memberOrNull = memberRepository.findByEmail(redisService.getValues(refreshToken));

            if (memberOrNull.isPresent()) {

                // 토큰을 재발급 받기 위해 내부적으로는 login 로직을 실행한다.
                Member member = memberOrNull.get();
                String email = member.getEmail();
                String password = member.getPassword();

                log.info("member.getPassword() = {}", member.getPassword());
                log.info("토큰 재발급!!!!");

                // 기존에 있던 토큰은 삭제
                redisService.deleteValues(refreshToken);

                return login(new LoginMemberRequestDto(email, password));
            }

            return new JwtToken("Refresh Token과 사용자 정보가 일치하지 않습니다.", null, null);
        }
    }



    // 비밀번호와 비밀번화 확인이 같은지 체크하는 메소드
    private static boolean isEqualPassword(CreateMemberRequestDto createMemberRequestDto) {
        if (!createMemberRequestDto.getPassword().equals(createMemberRequestDto.getPasswordCheck())) {
            return true;
        }
        return false;
    }

    private void saveRefreshToken(String refreshToken, Authentication authentication, Duration duration) {
        log.info("save token");
        redisService.setValues(refreshToken, authentication.getName(), duration);
    }
}
