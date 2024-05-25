package com.dto.way.member.web.controller;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.service.FollowService;
import com.dto.way.member.domain.service.MemberService;
import com.dto.way.member.domain.service.NotificationService;
import com.dto.way.member.web.dto.FollowDTO;
import com.dto.way.member.web.response.ApiResponse;
import com.dto.way.message.NotificationMessage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dto.way.member.web.response.code.status.SuccessStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member-service/follow")
public class FollowController {

    private final MemberService memberService;
    private final FollowService followService;
    private final NotificationService notificationService;

    // 로그인 한 사용자가 팔로잉 하는 API
    @Operation(summary = "팔로잉 추가 API", description = "팔로잉 하려는 사용자의 닉네임을 path variable에 넣어주세요.")
    @PostMapping("/{friendNickname}")
    public ApiResponse follow(Authentication authentication, @PathVariable("friendNickname") String nickname) {
        Member login_member = memberService.findMemberByEmail(authentication.getName());
        Member to_user = memberService.findMemberByNickname(nickname);
        followService.follow(login_member, to_user);

        // 팔로우 알림 메세지 생성
        String message = login_member.getNickname() + "님이 회원님을 팔로우하기 시작했습니다.";
        NotificationMessage notificationMessage = notificationService.createNotificationMessage(to_user.getNickname(), message);

        // Kafka로 메세지 전송
        // notificationService.followNotificationCreate(notificationMessage);

        return ApiResponse.of(_OK, null);
    }

    // 로그인 한 사용자가 본인 혹은 다른 사람의 팔로잉 리스트를 보는 API
    @Operation(summary = "팔로잉 리스트 조회 API", description = "팔로잉 리스트를 조회하려는 사용자의 닉네임을 path variable에 넣어주세요.")
    @GetMapping("/{nickname}/following-list")
    public ApiResponse<List<FollowDTO>> getFollowingList(Authentication authentication, @PathVariable("nickname") String nickname) {
        Member from_member = memberService.findMemberByNickname(nickname);
        Member login_member = memberService.findMemberByEmail(authentication.getName());
        List<FollowDTO> followingList = followService.followingList(from_member, login_member);
        return ApiResponse.of(_OK, followingList);
    }

    // 로그인 한 사용자가 본인 혹은 다른 사람의 팔로워 리스트를 보는 API
    @Operation(summary = "팔로워 리스트 조회 API", description = "팔로워 리스트를 조회하려는 사용자의 닉네임을 path variable에 넣어주세요.")
    @GetMapping("/{nickname}/follower-list")
    public ApiResponse<List<FollowDTO>> getFollowerList(Authentication authentication, @PathVariable("nickname") String nickname) {
        Member to_member = memberService.findMemberByNickname(nickname);
        Member login_member = memberService.findMemberByEmail(authentication.getName());
        List<FollowDTO> followerList = followService.followerList(to_member, login_member);
        return ApiResponse.of(_OK, followerList);
    }

    // 로그인 한 사용자가 본인의 팔로잉 리스트에서 팔로잉을 삭제하는 API
    @Operation(summary = "팔로잉 삭제 API", description = "팔로잉 리스트 중 삭제하고 싶은 사용자의 닉네임을 path variable에 넣어주세요.")
    @DeleteMapping("/following-list/{friendNickname}")
    public ApiResponse deleteFollowing(@AuthenticationPrincipal UserDetails userDetails, Authentication authentication, @PathVariable("friendNickname") String nickname) {
        Member login_member = memberService.findMemberByEmail(authentication.getName());
        Member to_member = memberService.findMemberByNickname(nickname);
        followService.cancelFollow(login_member, to_member);

        return ApiResponse.of(_OK, null);
    }

    // 로그인 한 사용자가 본인의 팔로워 리스트에서 팔로워를 삭제하는 API
    @Operation(summary = "팔로잉 삭제 API", description = "팔로워 리스트 중 삭제하고 싶은 사용자의 닉네임을 path variable에 넣어주세요.")
    @DeleteMapping("/follower-list/{friendNickname}")
    public ApiResponse deleteFollower(Authentication authentication, @PathVariable("friendNickname") String nickname) {
        Member login_member = memberService.findMemberByEmail(authentication.getName());
        Member from_member = memberService.findMemberByNickname(nickname);
        followService.cancelFollow(from_member, login_member);

        return ApiResponse.of(_OK, null);
    }

}
