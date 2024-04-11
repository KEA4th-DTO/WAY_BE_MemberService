package com.dto.way.member.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
<<<<<<< Updated upstream
=======
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
>>>>>>> Stashed changes

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
<<<<<<< Updated upstream
public class Member {
=======
@EqualsAndHashCode(of = "memberId")
public class Member implements UserDetails {
>>>>>>> Stashed changes

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
