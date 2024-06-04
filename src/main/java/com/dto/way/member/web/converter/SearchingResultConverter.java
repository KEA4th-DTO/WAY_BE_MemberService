package com.dto.way.member.web.converter;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.entity.Recommend;
import com.dto.way.member.web.dto.RecommendDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

import static com.dto.way.member.web.dto.MemberResponseDTO.*;

public class SearchingResultConverter {

    public static MemberSearchResultDTO toMemberSearchResultDTO(Member member) {
        return MemberSearchResultDTO.builder()
                .profileImageUrl(member.getProfileImageUrl())
                .introduce(member.getIntroduce())
                .nickname(member.getNickname())
                .build();
    }

    public static MemberSearchResultListDTO toMemberSearchResultListDTO(Page<Member> memberPage) {
        List<MemberSearchResultDTO> memberSearchResultDtoList = memberPage.stream()
                .map(SearchingResultConverter::toMemberSearchResultDTO).collect(Collectors.toList());

        return MemberSearchResultListDTO.builder()
                .list(memberSearchResultDtoList)
                .isFirst(memberPage.isFirst())
                .isLast(memberPage.isLast())
                .totalElements(memberPage.getTotalElements())
                .totalPage(memberPage.getTotalPages())
                .listSize(memberPage.getSize())
                .build();
    }

    public static RecommendResponseDTO toRecommendResponseDTO(Member recommendMember, List<String> tags, boolean isFollowing) {
        return RecommendResponseDTO.builder()
                .name(recommendMember.getName())
                .nickname(recommendMember.getNickname())
                .profileImageUrl(recommendMember.getProfileImageUrl())
                .introduce(recommendMember.getIntroduce())
                .isFollowing(isFollowing)
                .tags(tags)
                .build();
    }
}
