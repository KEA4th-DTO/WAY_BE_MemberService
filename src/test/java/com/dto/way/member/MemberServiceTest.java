package com.dto.way.member;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.entity.MemberStatus;
import com.dto.way.member.domain.repository.MemberRepository;
import com.dto.way.member.domain.repository.RecommendRepository;
import com.dto.way.member.domain.repository.TagRepository;
import com.dto.way.member.domain.service.FollowService;
import com.dto.way.member.domain.service.MemberService;
import com.dto.way.member.global.AmazonS3Manager;
import com.dto.way.member.global.config.AmazonS3Config;
import com.dto.way.member.web.dto.FeignReturnDTO;
import com.dto.way.member.web.dto.MemberRequestDTO;
import com.dto.way.member.web.dto.MemberResponseDTO;
import com.dto.way.member.web.dto.TagDTO;
import com.dto.way.member.web.feign.AiFeignClient;
import com.dto.way.member.web.feign.PostFeignCilent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private AmazonS3Config amazonS3Config;

    @Mock
    private AmazonS3Manager amazonS3Manager;

    @Mock
    private RecommendRepository recommendRepository;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("닉네임 중복 체크 테스트")
    public void testCheckNicknameDuplication() {
        String nickname = "nickname";
        when(memberRepository.existsByNickname(nickname)).thenReturn(true);
        assertTrue(memberService.checkNicknameDuplication(nickname));
        verify(memberRepository, times(1)).existsByNickname(nickname);
    }

    @Test
    @DisplayName("이메일 중복 체크 테스트")
    public void testCheckEmailDuplication() {
        String email = "email@example.com";
        when(memberRepository.existsByEmail(email)).thenReturn(true);
        assertTrue(memberService.checkEmailDuplication(email));
        verify(memberRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("닉네임으로 멤버 찾기 테스트")
    public void testFindMemberByNickname() {
        String nickname = "nickname";
        Member member = new Member();
        when(memberRepository.findByNickname(nickname)).thenReturn(Optional.of(member));
        assertEquals(member, memberService.findMemberByNickname(nickname));
        verify(memberRepository, times(1)).findByNickname(nickname);
    }

    @Test
    @DisplayName("멤버 ID로 멤버 찾기 테스트")
    public void testFindMemberByMemberId() {
        Long memberId = 1L;
        Member member = new Member();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        assertEquals(member, memberService.findMemberByMemberId(memberId));
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("멤버 상태로 멤버 찾기 테스트")
    public void testFindMembersByMemberStatus() {
        MemberStatus memberStatus = MemberStatus.ACTIVE;
        List<Member> members = Arrays.asList(new Member(), new Member());
        when(memberRepository.findByMemberStatus(memberStatus)).thenReturn(members);
        assertEquals(2, memberService.findMembersByMemberStatus(memberStatus).size());
        verify(memberRepository, times(1)).findByMemberStatus(memberStatus);
    }

    @Test
    @DisplayName("멤버의 태그 찾기 테스트")
    public void testGetTagsByMember() {
        Member member = new Member();
        List<Object[]> tagObjects = Arrays.asList(
                new Object[]{"tag1", "tag2", "tag3"},
                new Object[]{"tag4", "tag5", "tag6"}
        );
        when(tagRepository.findTagsByMember(member)).thenReturn(tagObjects);
        List<TagDTO> tags = memberService.getTagsByMember(member);
        assertEquals(2, tags.size());
        verify(tagRepository, times(1)).findTagsByMember(member);
    }

    @Test
    @DisplayName("멤버의 태그 문자열로 찾기 테스트")
    public void testGetTagStringsByMember() {
        Member member = new Member();
        List<TagDTO> tagDTOs = Arrays.asList(
                new TagDTO("tag1", "tag2", "tag3"),
                new TagDTO("tag4", "tag5", "tag6")
        );
        when(tagRepository.findTagsByMember(member)).thenReturn(
                tagDTOs.stream().map(tagDTO -> new Object[]{tagDTO.getWayTag1(), tagDTO.getWayTag2(), tagDTO.getWayTag3()})
                        .collect(Collectors.toList())
        );
        List<String> tagStrings = memberService.getTagStringsByMember(member);
        assertEquals(6, tagStrings.size());
        verify(tagRepository, times(1)).findTagsByMember(member);
    }

    @Test
    @DisplayName("AI 이미지 저장 테스트")
    public void testSaveAiImage() throws IOException {
        Long memberId = 1L;
        MultipartFile aiImage = mock(MultipartFile.class);
        String imageUrl = "https://way-bucket-s3.s3.ap-northeast-2.amazonaws.com/ai_image/ai_image_" + memberId + ".png";
        when(amazonS3Config.getAiImagePath()).thenReturn("ai_image");
        when(amazonS3Manager.getObjectByUrl(imageUrl)).thenReturn(ResponseEntity.ok().body(new byte[0]));
        when(amazonS3Manager.uploadFileToDirectory(anyString(), anyString(), any(MultipartFile.class))).thenReturn(imageUrl);
        String result = memberService.saveAiImage(aiImage, memberId);
        assertEquals(imageUrl, result);
        verify(amazonS3Manager, times(1)).deleteFile(imageUrl);
        verify(amazonS3Manager, times(1)).uploadFileToDirectory("ai_image", "ai_image_" + memberId + ".png", aiImage);
    }

    @Test
    @DisplayName("텍스트 URL 저장 테스트")
    public void testSaveTextURL() throws IOException {
        Long memberId = 1L;
        String textURL = "https://way-bucket-s3.s3.ap-northeast-2.amazonaws.com/ai_text/text_member_id_" + memberId + ".txt";
        String result = memberService.saveTextURL(memberId);
        assertEquals(textURL, result);
        verify(memberRepository, times(1)).updateMemberTextUrlById(memberId, textURL);
    }

    @Test
    @DisplayName("멤버의 모든 ID 찾기 테스트")
    public void testGetAllMemberIdsByMember() {
        Member member = new Member();
        when(recommendRepository.findMemberId1ByMember(member)).thenReturn(Arrays.asList(1L, 2L));
        when(recommendRepository.findMemberId2ByMember(member)).thenReturn(Arrays.asList(3L, 4L));
        when(recommendRepository.findMemberId3ByMember(member)).thenReturn(Arrays.asList(5L, 6L));
        List<Long> memberIds = memberService.getAllMemberIdsByMember(member);
        assertEquals(6, memberIds.size());
        verify(recommendRepository, times(1)).findMemberId1ByMember(member);
        verify(recommendRepository, times(1)).findMemberId2ByMember(member);
        verify(recommendRepository, times(1)).findMemberId3ByMember(member);
    }

    @Test
    @DisplayName("닉네임 포함 검색 테스트")
    public void testFindByNicknameContaining() {
        String keyword = "nickname";
        Page<Member> members = new PageImpl<>(Arrays.asList(new Member(), new Member()));
        when(memberRepository.findByNicknameContaining(PageRequest.of(0, 8), keyword)).thenReturn(members);
        assertEquals(2, memberService.findByNicknameContaining(0, keyword).getTotalElements());
        verify(memberRepository, times(1)).findByNicknameContaining(PageRequest.of(0, 8), keyword);
    }

}

