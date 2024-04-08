package com.dto.way.member.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String introduce;

    private String profileImageUrl;

    private LocalDate birth;

    private MemberStatus memberStatus;

    private LoginType loginType;

    @Column(nullable = false)
    private MemberAuth memberAuth;

    @Column(nullable = false)
    private String nickname;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;



}
