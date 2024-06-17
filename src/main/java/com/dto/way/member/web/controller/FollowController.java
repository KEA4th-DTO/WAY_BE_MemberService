package com.dto.way.member.web.controller;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.service.FollowService;
import com.dto.way.member.domain.service.MemberService;
import com.dto.way.member.domain.service.NotificationService;
import com.dto.way.member.global.JwtUtils;
import com.dto.way.member.web.dto.FollowResponseDTO;
import com.dto.way.member.web.response.ApiResponse;
import com.dto.way.message.NotificationMessage;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.dto.way.member.web.converter.FollowConverter.toIsFollowResponseDTO;
import static com.dto.way.member.web.dto.FollowResponseDTO.*;
import static com.dto.way.member.web.response.code.status.SuccessStatus.*;
import static com.dto.way.member.web.response.code.status.ErrorStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member-service/follow")
public class FollowController {

    private final MemberService memberService;
    private final FollowService followService;
    private final NotificationService notificationService;
    private final JwtUtils jwtUtils;

    // 로그인 한 사용자가 팔로잉 하는 API
    @Operation(summary = "팔로잉 추가 API", description = "팔로잉 하려는 사용자의 닉네임을 path variable에 넣어주세요.")
    @PostMapping("/{friendNickname}")
    public ApiResponse follow(@PathVariable("friendNickname") String nickname,
                              HttpServletRequest request) {

        // 토큰에서 요청 유저 정보 추출
        String loginMemberNickname = getMemberNicknameByRequest(request);

        // 닉네임으로 로그인한 유저 정보를 가져옴.
        Member loginMember = memberService.findMemberByNickname(loginMemberNickname);
        Member toMember = memberService.findMemberByNickname(nickname);
        String result = followService.follow(loginMember, toMember);

        String message = loginMemberNickname + "님이 팔로우하기 시작했습니다.";

        if (result.equals(FOLLOW_SUCCESS.getCode())) { // 팔로잉에 성공한 경우
            NotificationMessage notificationMessage = notificationService.createNotificationMessage(toMember.getId(), toMember.getNickname(), message);
            notificationService.followNotificationCreate(notificationMessage);
            return ApiResponse.of(FOLLOW_SUCCESS, null);
        }

        else if (result.equals(FOLLOW_CANNOT_SELF.getCode())) { // 본인을 팔로잉 한 경우
            return ApiResponse.onFailure(FOLLOW_CANNOT_SELF.getCode(), FOLLOW_CANNOT_SELF.getMessage(), null);
        }

        else if (result.equals(FOLLOW_NOT_DUPLICATED.getCode())) { // 팔로잉 중복인 경우
            return ApiResponse.onFailure(FOLLOW_NOT_DUPLICATED.getCode(), FOLLOW_NOT_DUPLICATED.getMessage(), null);
        }

        else {
            return ApiResponse.onFailure(_BAD_REQUEST.getCode(), _BAD_REQUEST.getMessage(), null);
        }

    }

    // 로그인 한 사용자가 본인 혹은 다른 사람의 팔로잉 리스트를 보는 API
    @Operation(summary = "팔로잉 리스트 조회 API", description = "팔로잉 리스트를 조회하려는 사용자의 닉네임을 path variable에 넣어주세요.")
    @GetMapping("/{nickname}/following-list")
    public ApiResponse<List<FollowListResponseDTO>> getFollowingList(@PathVariable("nickname") String nickname,
                                                                 HttpServletRequest request) {

        // 토큰에서 요청 유저 정보 추출
        String loginMemberNickname = getMemberNicknameByRequest(request);

        // 닉네임으로 로그인한 유저 정보를 가져옴.
        Member loginMember = memberService.findMemberByNickname(loginMemberNickname);
        Member from_member = memberService.findMemberByNickname(nickname);

        List<MemberInfoResponseDTO> followingList = followService.followingList(from_member);

        return getFollowList(loginMember, followingList);
    }



    // 로그인 한 사용자가 본인 혹은 다른 사람의 팔로워 리스트를 보는 API
    @Operation(summary = "팔로워 리스트 조회 API", description = "팔로워 리스트를 조회하려는 사용자의 닉네임을 path variable에 넣어주세요.")
    @GetMapping("/{nickname}/follower-list")
    public ApiResponse<List<FollowListResponseDTO>> getFollowerList(@PathVariable("nickname") String nickname,
                                                                HttpServletRequest request) {

        // 토큰에서 요청 유저 정보 추출
        String loginMemberNickname = getMemberNicknameByRequest(request);

        // 닉네임으로 로그인한 유저 정보를 가져옴.
        Member loginMember = memberService.findMemberByNickname(loginMemberNickname);
        Member toMember = memberService.findMemberByNickname(nickname);
        List<MemberInfoResponseDTO> followerList = followService.followerList(toMember);

        return getFollowList(loginMember, followerList);
    }

    // 로그인 유저 - 조회 대상 간의 팔로잉 상태
    @Operation(summary = "팔로잉 단건 조회 API", description = "로그인 한 유저와 path variable에 넣은 유저 간의 팔로잉 상태를 조회하는 API입니다.")
    @GetMapping("/follow-status/{friendNickname}")
    public ApiResponse<IsFollowResponseDTO> getFollowStatus(@PathVariable("friendNickname") String friendNickname,
                                                            HttpServletRequest request) {

        // 토큰에서 요청 유저 정보 추출
        String loginMemberNickname = getMemberNicknameByRequest(request);

        // 닉네임으로 로그인한 유저 정보를 가져옴.
        Member loginMember = memberService.findMemberByNickname(loginMemberNickname);
        Member toMember = memberService.findMemberByNickname(friendNickname);

        Boolean isFollowing = followService.findStatus(loginMember, toMember);

        IsFollowResponseDTO isFollowResponseDTO = toIsFollowResponseDTO(loginMember, toMember, isFollowing);

        return ApiResponse.of(FOLLOW_FOUND_FOLLOWONE, isFollowResponseDTO);
    }

    // 로그인 한 사용자가 본인의 팔로잉 리스트에서 팔로잉을 삭제하는 API
    @Operation(summary = "팔로잉 삭제 API", description = "팔로잉 리스트 중 삭제하고 싶은 사용자의 닉네임을 path variable에 넣어주세요.")
    @DeleteMapping("/following-list/{friendNickname}")
    public ApiResponse deleteFollowing(@PathVariable("friendNickname") String friendNickname,
                                       HttpServletRequest request) {

        // 토큰에서 요청 유저 정보 추출
        String loginMemberNickname = getMemberNicknameByRequest(request);

        // 닉네임으로 로그인한 유저 정보를 가져옴.
        Member loginMember = memberService.findMemberByNickname(loginMemberNickname);
        Member to_member = memberService.findMemberByNickname(friendNickname);
        followService.cancelFollow(loginMember, to_member);

        return ApiResponse.of(FOLLOW_DELETE_FOLLOWING, null);
    }

    // 로그인 한 사용자가 본인의 팔로워 리스트에서 팔로워를 삭제하는 API
    @Operation(summary = "팔로워 삭제 API", description = "팔로워 리스트 중 삭제하고 싶은 사용자의 닉네임을 path variable에 넣어주세요.")
    @DeleteMapping("/follower-list/{friendNickname}")
    public ApiResponse deleteFollower(@PathVariable("friendNickname") String friendNickname,
                                      HttpServletRequest request) {

        // 토큰에서 요청 유저 정보 추출
        String loginMemberNickname = getMemberNicknameByRequest(request);

        // 닉네임으로 프로필 조회 대상 멤버 정보를 가져옴
        Member loginMember = memberService.findMemberByNickname(loginMemberNickname);
        Member from_member = memberService.findMemberByNickname(friendNickname);
        followService.cancelFollow(from_member, loginMember);

        return ApiResponse.of(FOLLOW_DELETE_FOLLOWER, null);
    }

    // request에서 토큰을 뽑고 토큰에서 로그인 한 nickname 추출
    private String getMemberNicknameByRequest(HttpServletRequest request) {

        String token = jwtUtils.resolveToken(request);
        Claims claims = jwtUtils.parseClaims(token);

        String loginMemberNickname = claims.get("nickname", String.class);
        log.info("member Nickname: " + claims.get("nickname", String.class));

        return loginMemberNickname;
    }

    // 팔로우 리스트 DTO에 맞게 변환하는 메소드
    private ApiResponse<List<FollowListResponseDTO>> getFollowList(Member loginMember, List<MemberInfoResponseDTO> followingList) {
        List<FollowListResponseDTO> list = followingList.stream().map(following -> {
            Long memberId = following.getMemberId();
            Member memberByMemberId = memberService.findMemberByMemberId(memberId);

            FollowListResponseDTO followListResponseDTO = new FollowListResponseDTO();
            followListResponseDTO.setMemberInfoResponseDTO(following);
            followListResponseDTO.setIsFollowing(followService.findStatus(loginMember, memberByMemberId));

            return followListResponseDTO;
        }).collect(Collectors.toList());

        return ApiResponse.of(_OK, list);
    }
}
