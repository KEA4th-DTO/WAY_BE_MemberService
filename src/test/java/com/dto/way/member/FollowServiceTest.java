//package com.dto.way.member;
//
//import com.dto.way.member.domain.entity.*;
//import com.dto.way.member.domain.repository.FollowRepository;
//import com.dto.way.member.domain.repository.MemberRepository;
//import com.dto.way.member.domain.service.FollowService;
//import jakarta.annotation.PostConstruct;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//public class FollowServiceTest {
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Mock
//    private FollowRepository followRepository;
//
//    @InjectMocks
//    private FollowService followService;
//
//    private Member testMember1;
//    private Member testMember2;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // 테스트 데이터 생성
//        testMember1 = Member.builder()
//                .name("Test User")
//                .email("testuser@example.com")
//                .password("password")
//                .introduce("This is a test user.")
//                .profileImageUrl("https://example.com/default-profile.png")
//                .memberStatus(MemberStatus.ACTIVATE)
//                .loginType(LoginType.GENERAL)
//                .memberAuth(MemberAuth.CLIENT)
//                .nickname("testuser1")
//                .phoneNumber("123-456-7890")
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .roles(Arrays.asList("ROLE_USER"))
//                .build();
//
//        testMember2 = Member.builder()
//                .name("Test User")
//                .email("testuser@example.com")
//                .password("password")
//                .introduce("This is a test user.")
//                .profileImageUrl("https://example.com/default-profile.png")
//                .memberStatus(MemberStatus.ACTIVATE)
//                .loginType(LoginType.GENERAL)
//                .memberAuth(MemberAuth.CLIENT)
//                .nickname("testuser2")
//                .phoneNumber("123-456-7890")
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .roles(Arrays.asList("ROLE_USER"))
//                .build();
//
//        memberRepository.save(testMember1);
//        memberRepository.save(testMember2);
//    }
//
//    @Test
//    void testFollowSelf() {
//        assertEquals("CANNOT_FOLLOW_SELF", followService.follow(testMember1, testMember1));
//    }
//
//    @Test
//    void testFollowDuplicate() {
//
//        when(followRepository.findFollow(testMember1, testMember2)).thenReturn(Optional.of(new Follow()));
//
//        assertEquals("FOLLOW_NOT_DUPLICATED", followService.follow(testMember1, testMember2));
//        verify(followRepository, times(1)).findFollow(testMember1, testMember2);
//    }
//
//    @Test
//    void testFollowSuccess() {
//
//        when(followRepository.findFollow(testMember1, testMember2)).thenReturn(Optional.empty());
//
//        assertEquals("FOLLOW_SUCCESS", followService.follow(testMember1, testMember2));
//        verify(followRepository, times(1)).save(any(Follow.class));
//    }
//
//    @Test
//    void testFollowingList() {
//        Member member = new Member();
//        member.setId(1L);
//        Follow follow = new Follow();
//        follow.setToMember(new Member());
//
//        when(followRepository.findByFromMember(member)).thenReturn(Arrays.asList(follow));
//
//        List<MemberInfoResponseDTO> result = followService.followingList(member);
//        assertEquals(1, result.size());
//        verify(followRepository, times(1)).findByFromMember(member);
//    }
//
//    @Test
//    void testFollowerList() {
//        Member member = new Member();
//        member.setId(1L);
//        Follow follow = new Follow();
//        follow.setFromMember(new Member());
//
//        when(followRepository.findByToMember(member)).thenReturn(Arrays.asList(follow));
//
//        List<MemberInfoResponseDTO> result = followService.followerList(member);
//        assertEquals(1, result.size());
//        verify(followRepository, times(1)).findByToMember(member);
//    }
//
//    @Test
//    void testFindStatus() {
//        Member loginMember = new Member();
//        loginMember.setId(1L);
//        Member selectedMember = new Member();
//        selectedMember.setId(2L);
//
//        when(followRepository.findFollow(loginMember, selectedMember)).thenReturn(Optional.of(new Follow()));
//
//        assertTrue(followService.findStatus(loginMember, selectedMember));
//        verify(followRepository, times(1)).findFollow(loginMember, selectedMember);
//    }
//
//    @Test
//    void testCancelFollow() {
//        Member fromMember = new Member();
//        fromMember.setId(1L);
//        Member toMember = new Member();
//        toMember.setId(2L);
//
//        followService.cancelFollow(fromMember, toMember);
//        verify(followRepository, times(1)).deleteByFromMemberAndToMember(fromMember, toMember);
//    }
//
//    @Test
//    void testGetFollowingCount() {
//        Long memberId = 1L;
//        when(followRepository.countByFromMemberId(memberId)).thenReturn(5L);
//
//        assertEquals(5L, followService.getFollowingCount(memberId));
//        verify(followRepository, times(1)).countByFromMemberId(memberId);
//    }
//
//    @Test
//    void testGetFollowerCount() {
//        Long memberId = 1L;
//        when(followRepository.countByToMemberId(memberId)).thenReturn(10L);
//
//        assertEquals(10L, followService.getFollowerCount(memberId));
//        verify(followRepository, times(1)).countByToMemberId(memberId);
//    }
//}
