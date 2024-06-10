package com.dto.way.member.web.controller;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.service.FollowService;
import com.dto.way.member.domain.service.MemberService;
import com.dto.way.member.global.JwtUtils;
import com.dto.way.member.web.response.ApiResponse;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.dto.way.member.web.converter.MemberConverter.*;
import static com.dto.way.member.web.dto.MemberRequestDTO.*;
import static com.dto.way.member.web.dto.MemberResponseDTO.*;
import static com.dto.way.member.web.response.code.status.SuccessStatus.*;
import static com.dto.way.member.web.response.code.status.ErrorStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member-service")
public class MemberController {

    private final MemberService memberService;
    private final JwtUtils jwtUtils;
    private final FollowService followService;

    @Operation(summary = "프로필 조회 API", description = "path variable의 닉네임과 토큰 닉네임이 일치하면 isMyProfile이 true, 일치하지 않으면 false가 반환됩니다.")
    @GetMapping("/profile/{nickname}")
    public ApiResponse<GetProfileResponseDTO> getProfile(@PathVariable(value = "nickname") String nickname,
                                                         HttpServletRequest request) {

        // 현재 로그인 한 유저의 닉네임
        String loginMemberNickname = getMemberNicknameByRequest(request);

        Member profileMember = memberService.findMemberByNickname(nickname);

        if (loginMemberNickname.equals(profileMember.getNickname())) { // 본인 프로필 조회인 경우

            GetProfileResponseDTO profile = memberService.createProfile(profileMember, true);
            return ApiResponse.of(MEMBER_FOUND_PROFILE, profile);
        } else { // 남의 프로필 조회인 경우

            GetProfileResponseDTO profile = memberService.createProfile(profileMember, false);
            return ApiResponse.of(MEMBER_FOUND_PROFILE, profile);
        }

    }

    @Operation(summary = "프로필 수정 API", description = "path variable의 닉네임과 토큰 닉네임이 일치해야만 수정이 가능합니다.")
    @PostMapping(value = "/profile/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse updateProfile(HttpServletRequest request,
                                     @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                                     @Valid @RequestPart(value = "updateProfileRequestDTO") UpdateProfileRequestDTO updateProfileRequestDTO) throws IOException {

        // 현재 로그인 한 유저의 id
        Long loginMemberId = getMemberIdByRequest(request);

        // 닉네임으로 프로필 조회 대상 멤버 정보를 가져옴
        Member profileMember = memberService.findMemberByMemberId(loginMemberId);

        String result = memberService.updateProfile(updateProfileRequestDTO, profileImage, profileMember);
        if (result.equals(MEMBER_NICKNAME_DUPLICATED.getCode())) { // 수정하려는 닉네임이 이미 사용 중인 경우
            return ApiResponse.onFailure(MEMBER_NICKNAME_DUPLICATED.getCode(), MEMBER_NICKNAME_DUPLICATED.getMessage(), null);
        } else {
            return ApiResponse.of(MEMBER_UPDATE_PROFILE, null);
        }

    }

    @Operation(summary = "사용자 검색 API", description = "키워드가 포함된 닉네임을 가진 유저를 반환합니다. 멤버 리스트, 시작 페이지, 현재 페이지, 마지막 페이지가 반환됩니다.")
    @GetMapping("/search/body")
    public ApiResponse<MemberSearchResultListDTO> searchNickname(@RequestParam(name = "keyword") String keyword,
                                                                 @RequestParam(name = "page") Integer page) {

        // 키워드 검색 결과를 페이징
        Page<Member> memberPage = memberService.findByNicknameContaining(page - 1, keyword);
        MemberSearchResultListDTO result = toMemberSearchResultListDTO(memberPage);

        if (keyword == null) { // 키워드가 없는 경우
            return ApiResponse.onFailure(SEARCH_KEYWORD_NOT_NULL.getCode(), SEARCH_KEYWORD_NOT_NULL.getMessage(), null);
        } else if (result.getList().isEmpty()) { // 조회 결과가 없는 경우
            return ApiResponse.of(SEARCH_NO_RESULT, null);
        } else {
            return ApiResponse.of(_OK, result);
        }
    }

    @Operation(summary = "추천 유저 조회 API", description = "사용자에게 추천 유저 3명의 정보를 넘겨줍니다.")
    @GetMapping("/search/recommend")
    public ApiResponse<List<RecommendResponseDTO>> searchRecommend(HttpServletRequest request) {

        // 현재 로그인 한 유저의 id
        Long loginMemberId = getMemberIdByRequest(request);

        Member loginMember = memberService.findMemberByMemberId(loginMemberId);

        // 로그인한 유저의 추천 유저를 불러옴
        List<Long> recommends = memberService.getAllMemberIdsByMember(loginMember);

        log.info("recommends = {}", recommends);
        if (recommends.isEmpty()) { // 추천 유저가 아직 생성되지 않은 경우
            return ApiResponse.of(MEMBER_NO_RECOMMEND, null);
        } else {
            List<RecommendResponseDTO> result = recommends.stream()
                    .limit(3)
                    .map(recommendMemberId -> {
                        Member recommendMember = memberService.findMemberByMemberId(recommendMemberId);
                        Boolean isFollowing = followService.findStatus(loginMember, recommendMember);
                        List<String> tags = memberService.getTagStringsByMember(recommendMember);
                        return toRecommendResponseDTO(recommendMember, tags, isFollowing);
                    })
                    .toList();

            return ApiResponse.of(MEMBER_CREATE_RECOMMEND, result);
        }
    }


    @Operation(summary = "AI서비스 호출 API", description = "사용자의 마이맵 이미지를 넘기면 사용자 AI 분석이 업데이트 됩니다.")
    @PostMapping(value = "/mymap-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse saveMymapImage(HttpServletRequest request,
                                      @RequestPart(value = "myMapImage") MultipartFile myMapImage) throws IOException {

        // 토큰에서 로그인 한 id 가져옴
        Long loginMemberId = getMemberIdByRequest(request);

        String imageUrl = memberService.saveAiImage(myMapImage, loginMemberId);
        String textUrl = memberService.saveTextURL(loginMemberId);

        memberService.requestWayTags(loginMemberId, imageUrl, textUrl);
        // memberService.requestRecommendUser(loginMemberId);

        return ApiResponse.of(_OK, null);
    }

    // request에서 토큰을 뽑고 토큰에서 로그인 한 nickname 추출
    private String getMemberNicknameByRequest(HttpServletRequest request) {

        String token = jwtUtils.resolveToken(request);
        Claims claims = jwtUtils.parseClaims(token);

        String loginMemberNickname = claims.get("nickname", String.class);
        log.info("member Nickname: " + claims.get("nickname", String.class));

        return loginMemberNickname;
    }

    // request에서 토큰을 뽑고 토큰에서 로그인 한 id 추출
    private Long getMemberIdByRequest(HttpServletRequest request) {

        String token = jwtUtils.resolveToken(request);
        Claims claims = jwtUtils.parseClaims(token);

        Long loginMemberId = claims.get("memberId", Long.class);
        log.info("loginMemberId " + claims.get("memberId"));

        return loginMemberId;
    }

    // 비밀번호 재설정

    // 전화번호 인증을 통한 이메일 찾기

}
