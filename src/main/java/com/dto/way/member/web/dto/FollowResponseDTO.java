package com.dto.way.member.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class FollowResponseDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class MemberInfoResponseDTO {
        private Long memberId;
        private String name;
        private String nickname;
        private String profileImageUrl;
    }

    @Getter
    @Setter
    @Builder
    public static class IsFollowResponseDTO {
        private Long fromMember;
        private Long toMember;
        private Boolean isFollowing;
    }

    @Getter
    @Setter
    public static class FollowListResponseDTO {
        private MemberInfoResponseDTO memberInfoResponseDTO;
        private Boolean isFollowing;
    }

}
