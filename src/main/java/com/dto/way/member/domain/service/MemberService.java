package com.dto.way.member.domain.service;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.entity.MemberStatus;
import com.dto.way.member.domain.repository.MemberRepository;
import com.dto.way.member.domain.repository.TagRepository;
import com.dto.way.member.global.AmazonS3Manager;
import com.dto.way.member.global.config.AmazonS3Config;
import com.dto.way.member.web.feign.AiFeignClient;
import com.dto.way.member.web.feign.PostFeignCilent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.dto.way.member.web.dto.FeignReturnDTO.*;
import static com.dto.way.member.web.dto.MemberRequestDTO.*;
import static com.dto.way.member.web.dto.MemberResponseDTO.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final FollowService followService;
    private final PostFeignCilent postFeignCilent;
    private final AiFeignClient aiFeignClient;
    private final AmazonS3Config amazonS3Config;
    private final AmazonS3Manager amazonS3Manager;

    // 닉네임 중복 검사 메소드
    public boolean checkNicknameDuplication(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    // 이메일 중복 검사 메소드
    public boolean checkEmailDuplication(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Member findMemberByNickname(String nickname) {
        Optional<Member> member = memberRepository.findByNickname(nickname);
        return member.orElse(null);
    }

    @Transactional(readOnly = true)
    public Member findMemberByMemberId(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        return member.orElse(null);
    }

    @Transactional(readOnly = true)
    public List<MemberInfoResponseDTO> findMembersByMemberStatus(MemberStatus memberStatus) {
        List<Member> memberList = memberRepository.findByMemberStatus(memberStatus);

        return memberList.stream()
                .map(member -> {
                    MemberInfoResponseDTO memberInfoResponseDTO = new MemberInfoResponseDTO();
                    memberInfoResponseDTO.setMemberId(member.getId());
                    memberInfoResponseDTO.setName(member.getName());
                    memberInfoResponseDTO.setNickname(member.getNickname());
                    memberInfoResponseDTO.setProfileImageUrl(member.getProfileImageUrl());
                    memberInfoResponseDTO.setIntroduce(member.getIntroduce());
                    memberInfoResponseDTO.setMemberStatus(member.getMemberStatus());
                    memberInfoResponseDTO.setPhoneNumber(member.getPhoneNumber());
                    return memberInfoResponseDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public GetProfileResponseDTO createProfile(Member profileMember, Boolean isMyProfile) {

        // 팔로잉, 팔로워 수를 가져온다.
        Long followingCount = followService.getFollowingCount(profileMember.getId());
        Long followerCount = followService.getFollowerCount(profileMember.getId());

        PostCountDTO postsCount = postFeignCilent.getPostsCount(profileMember.getId());

        // 웨이태그를 불러와 List로 만든다.
        List<String> tags = tagRepository.findTagsByMember(profileMember);

        // 프로필을 만든다.
        return GetProfileResponseDTO.builder()
                .name(profileMember.getName())
                .profileImageUrl(profileMember.getProfileImageUrl())
                .introduce(profileMember.getIntroduce())
                .nickname(profileMember.getNickname())
                .dailyCount(postsCount.getDailyCount()) // API 개발되고 나면 수정할 예정
                .historyCount(postsCount.getHistoryCount())
                .followingCount(followingCount)
                .followerCount(followerCount)
                .isMyProfile(isMyProfile)
                .wayTags(tags)
                .build();
    }

    @Transactional
    public void updateProfile(UpdateProfileRequestDTO updateProfileRequestDTO, MultipartFile profileImage, Member profileMember) throws IOException {

        // 프로필 이미지가 이미 있다면 이미지를 삭제
        String oldImageUrl = amazonS3Config.getProfileImagePath() + "/" + profileMember.getEmail();

        if (!isDefaultProfileImage(profileMember.getProfileImageUrl())) {
            amazonS3Manager.deleteFile(oldImageUrl);
        }

        // 이미지를 S3에 저장, 파일 이름은 email로 지정
        String newImageUrl = amazonS3Manager.uploadFileToDirectory(amazonS3Config.getProfileImagePath(), profileMember.getEmail(), profileImage);

        Long memberId = profileMember.getId();
        String introduce = updateProfileRequestDTO.getIntroduce();
        String nickname = updateProfileRequestDTO.getNickname();

        memberRepository.updateMemberProfile(memberId, nickname, introduce, newImageUrl);
    }

    public String saveAiImage(MultipartFile aiImage, Long memberId) throws IOException {
        String imageUrl = amazonS3Config.getAiImagePath() + "/" + "ai_image_" + memberId;

        ResponseEntity<byte[]> objectByUrl = amazonS3Manager.getObjectByUrl(imageUrl);
        if (objectByUrl == null) {
            amazonS3Manager.uploadFileToDirectory(amazonS3Config.getAiImagePath(), "ai_image_" + memberId, aiImage);
        } else {
            amazonS3Manager.deleteFile(imageUrl);
            amazonS3Manager.uploadFileToDirectory(amazonS3Config.getAiImagePath(), "ai_image_" + memberId, aiImage);
        }

        return imageUrl;
    }

    @Transactional
    public String saveTextURL(Long memberId) throws IOException {
        String textURL = "https://way-bucket-s3.s3.ap-northeast-2.amazonaws.com/ai_text/text_member_id_" + memberId;

        memberRepository.updateMemberTextUrlById(memberId, textURL);

        return textURL;
    }

    @Async
    public CompletableFuture<String> requestWayTags(Long userId, String imageUrl, String textUrl) {
        CompletableFuture<String> future = new CompletableFuture<>();

        try {
            List<String> tags = aiFeignClient.getUserTags(userId, imageUrl, textUrl);
            tagRepository.updateTagWayTagsByMemberId(userId, tags.get(0), tags.get(1), tags.get(2));

            log.info("Tags updated successfully");
            future.complete("Tags updated successfully");
        } catch (Exception e) {
            log.info("Tags updated failed");
            future.completeExceptionally(e);
        }

        return future;
    }

    public boolean isDefaultProfileImage(String profileImageUrl) {
        return profileImageUrl != null && profileImageUrl.endsWith("default.png");
    }

    @Transactional(readOnly = true)
    public Page<Member> findByNicknameContaining (String keyword, Pageable pageable){
        return memberRepository.findByNicknameContaining(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Member> findAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }
}
