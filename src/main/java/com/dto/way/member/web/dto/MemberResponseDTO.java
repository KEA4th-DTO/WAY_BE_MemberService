package com.dto.way.member.web.dto;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.entity.MemberStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

public class MemberResponseDTO {

    @Getter
    @Setter
    public static class LoginMemberResponseDTO {

        private String name;
        private String nickname;
        private String email;
        private JwtToken jwtToken;
    }

    @Getter
    @Setter
    public static class MemberInfoResponseDTO {
        private Long memberId;
        private String name;
        private String nickname;
        private String profileImageUrl;
        private String introduce;
        private MemberStatus memberStatus;
        private String phoneNumber;
    }

    @Getter
    @Setter
    @Builder
    public static class GetProfileResponseDTO {

        private String name;

        private String profileImageUrl;

        private String introduce;

        private String nickname;

        private Integer dailyCount;

        private Integer historyCount;

        private Long followingCount;

        private Long followerCount;

        private List<String> wayTags;

        private Boolean isMyProfile;

    }

    @Getter
    @Setter
    public static class SearchingMemberDTO {

        private String profileImageUrl;
        private String nickname;
        private String introduce;

    }

    @Getter
    @Setter
    public static class SearchingResultDTO {

        private Page<SearchingMemberDTO> list;// 프로필 이미지, 닉네임, 한줄소개,
        private int nowPage;
        private int startPage;
        private int endPage;

    }
}

