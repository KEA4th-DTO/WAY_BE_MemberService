package com.dto.way.member.web.controller;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.service.MemberService;
import com.dto.way.member.global.JwtUtils;
import com.dto.way.member.web.converter.SearchingResultConverter;
import com.dto.way.member.web.response.ApiResponse;
import com.dto.way.member.web.response.code.status.SuccessStatus;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.dto.way.member.web.converter.SearchingResultConverter.*;
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

    @Operation(summary = "프로필 조회 API", description = "path variable의 닉네임과 토큰 닉네임이 일치하면 isMyProfile이 true, 일치하지 않으면 false가 반환됩니다.")
    @GetMapping("/profile/{nickname}")
    public ApiResponse<GetProfileResponseDTO> getProfile(@PathVariable(value = "nickname") String nickname,
                                                         HttpServletRequest request) {

        // 토큰에서 요청 유저 정보 추출
        String token = jwtUtils.resolveToken(request);
        Claims claims = jwtUtils.parseClaims(token);

        String loginNickname = claims.get("nickname", String.class);
        log.info("member Nickname: " + claims.get("nickname", String.class));

        // 닉네임으로 프로필 조회 대상 멤버 정보를 가져옴
        Member profileMember = memberService.findMemberByNickname(nickname);

        if (loginNickname.equals(profileMember.getNickname())) { // 본인 프로필 조회인 경우

            GetProfileResponseDTO profile = memberService.createProfile(profileMember, true);
            return ApiResponse.of(_OK, profile);
        } else { // 남의 프로필 조회인 경우

            GetProfileResponseDTO profile = memberService.createProfile(profileMember, false);
            return ApiResponse.of(_OK, profile);
        }

    }

    @Operation(summary = "프로필 수정 API", description = "path variable의 닉네임과 토큰 닉네임이 일치해야만 수정이 가능합니다.")
    @PostMapping(value = "/profile/{nickname}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse updateProfile(HttpServletRequest request,
                                     @PathVariable String nickname,
                                     @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                                     @Valid @RequestPart(value = "updateProfileRequestDTO") UpdateProfileRequestDTO updateProfileRequestDTO) throws IOException {

        // 토큰에서 요청 유저 정보 추출
        String token = jwtUtils.resolveToken(request);
        Claims claims = jwtUtils.parseClaims(token);

        Long loginMemberId = claims.get("memberId", Long.class);
        log.info("loginMemberId " + claims.get("memberId"));

        // 닉네임으로 프로필 조회 대상 멤버 정보를 가져옴
        Member profileMember = memberService.findMemberByNickname(nickname);

        // 토큰 유저 정보와 닉네임 유저 정보가 같아야만 수정이 가능하다.
        if (Objects.equals(loginMemberId, profileMember.getId())) {

            memberService.updateProfile(updateProfileRequestDTO, profileImage, profileMember);
            return ApiResponse.of(_OK, null);

        } else { // 일치하지 않는다면

            return ApiResponse.onFailure(MEMBER_UPDATE_FAILED.getCode(), MEMBER_UPDATE_FAILED.getMessage(), null);
        }

    }

    @Operation(summary = "사용자 검색 API", description = "키워드가 포함된 닉네임을 가진 유저를 반환합니다. 멤버 리스트, 시작 페이지, 현재 페이지, 마지막 페이지가 반환됩니다.")
    @GetMapping("/search/body")
    public ApiResponse<MemberSearchResultListDTO> searchNickname(@RequestParam(name = "keyword") String keyword,
                                                                 @RequestParam(name = "page") Integer page) {

        Page<Member> memberPage = memberService.findByNicknameContaining(page - 1, keyword);
        MemberSearchResultListDTO result = toMemberSearchResultListDTO(memberPage);

        if (keyword == null) {
            return ApiResponse.onFailure(SEARCH_KEYWORD_NOT_NULL.getCode(), SEARCH_KEYWORD_NOT_NULL.getMessage(), null);
        } else if (result.getList().isEmpty()) {
            return ApiResponse.onFailure(SEARCH_RESULT_NULL.getCode(), SEARCH_RESULT_NULL.getMessage(), null);
        } else {
            return ApiResponse.of(_OK, result);
        }
    }

    @Operation(summary = "AI서비스 호출 API", description = "사용자의 마이맵 이미지를 넘기면 사용자 AI 분석이 업데이트 됩니다.")
    @PostMapping(value = "/mymap-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse saveMymapImage(HttpServletRequest request,
                                      @RequestPart(value = "myMapImage") MultipartFile myMapImage) throws IOException {

        // 토큰에서 요청 유저 정보 추출
        String token = jwtUtils.resolveToken(request);
        Claims claims = jwtUtils.parseClaims(token);

        Long loginMemberId = claims.get("memberId", Long.class);
        log.info("loginMemberId " + claims.get("memberId"));

        String imageUrl = memberService.saveAiImage(myMapImage, loginMemberId);
        String textUrl = memberService.saveTextURL(loginMemberId);

        memberService.requestWayTags(loginMemberId, imageUrl, textUrl);

        return ApiResponse.of(_OK, null);
    }


    // 비밀번호 재설정
//    @PostMapping("/change/password")
//    public ApiResponse changePassword(@RequestBody ChangePasswordRequestDTO changePasswordRequestDTO,
//                                      HttpServletRequest request) {
//
//
//
//        return ApiResponse.of(_OK, null);
//    }

    // 전화번호 인증을 통한 이메일 찾기

}
