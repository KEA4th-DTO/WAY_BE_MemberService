package com.dto.way.member.web.controller;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.service.FollowService;
import com.dto.way.member.domain.service.MemberService;
import com.dto.way.member.web.dto.FollowDto;
import com.dto.way.member.web.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dto.way.member.web.response.code.status.SuccessStatus.*;
import static com.dto.way.member.web.response.code.status.ErrorStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member-service/follow")
public class FollowController {

    private final MemberService memberService;
    private final FollowService followService;

    // 로그인 한 사용자가 팔로잉 하는 API
    @PostMapping("/{friendNickname}")
    public ApiResponse follow(Authentication authentication, @PathVariable("friendNickname") String nickname) {
        Member login_member = memberService.findMemberByEmail(authentication.getName());
        Member to_user = memberService.findMemberByNickname(nickname);
        followService.follow(login_member, to_user);
        return ApiResponse.of(_OK, null);
    }

    // 로그인 한 사용자가 본인 혹은 다른 사람의 팔로잉 리스트를 보는 API
    @GetMapping("/{nickname}/followingList")
    public ApiResponse<List<FollowDto>> getFollowingList(Authentication authentication, @PathVariable("nickname") String nickname) {
        Member from_member = memberService.findMemberByNickname(nickname);
        Member login_member = memberService.findMemberByEmail(authentication.getName());
        List<FollowDto> followingList = followService.followingList(from_member, login_member);
        return ApiResponse.of(_OK, followingList);
    }

    // 로그인 한 사용자가 본인 혹은 다른 사람의 팔로워 리스트를 보는 API
    @GetMapping("/{nickname}/followerList")
    public ApiResponse<List<FollowDto>> getFollowerList(Authentication authentication, @PathVariable("nickname") String nickname) {
        Member to_member = memberService.findMemberByNickname(nickname);
        Member login_member = memberService.findMemberByEmail(authentication.getName());
        List<FollowDto> followerList = followService.followerList(to_member, login_member);
        return ApiResponse.of(_OK, followerList);
    }

    // 로그인 한 사용자가 본인의 팔로잉 리스트에서 팔로잉을 삭제하는 API
    @DeleteMapping("/followingList/{friendNickname}")
    public ApiResponse deleteFollowing(Authentication authentication, @PathVariable("friendNickname") String nickname) {
        Member login_member = memberService.findMemberByEmail(authentication.getName());
        Member to_member = memberService.findMemberByNickname(nickname);
        followService.cancelFollow(login_member, to_member);

        return ApiResponse.of(_OK, null);
    }

    // 로그인 한 사용자가 본인의 팔로워 리스트에서 팔로워를 삭제하는 API
    @DeleteMapping("/followerList/{friendNickname}")
    public ApiResponse deleteFollower(Authentication authentication, @PathVariable("friendNickname") String nickname) {
        Member login_member = memberService.findMemberByEmail(authentication.getName());
        Member from_member = memberService.findMemberByNickname(nickname);
        followService.cancelFollow(from_member, login_member);

        return ApiResponse.of(_OK, null);
    }

}
