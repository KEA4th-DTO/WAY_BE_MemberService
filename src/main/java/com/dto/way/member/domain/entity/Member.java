package com.dto.way.member.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    // pk, name, email, password, introduce, profile_image_url, birth, status, login_type, auth, nickname, created_at, updated_at

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @NonNull
    private String name;

    @NonNull
    private String email;

    @NonNull
    private String password;

    private String introduce;

    private String profileImageUrl;

    private LocalDate birth;

    private MemberStatus memberStatus;

    private LoginType loginType;

    @NonNull
    private MemberAuth memberAuth;

    @NonNull
    private String nickname;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;



}
