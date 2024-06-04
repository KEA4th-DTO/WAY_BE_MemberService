package com.dto.way.member.domain.repository;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.entity.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, Long> {

    @Query("SELECT r.memberId1 FROM Recommend r WHERE r.recommendedMember = :member")
    List<Long> findMemberId1ByMember(@Param("member") Member member);

    @Query("SELECT r.memberId2 FROM Recommend r WHERE r.recommendedMember = :member")
    List<Long> findMemberId2ByMember(@Param("member") Member member);

    @Query("SELECT r.memberId3 FROM Recommend r WHERE r.recommendedMember = :member")
    List<Long> findMemberId3ByMember(@Param("member") Member member);

    @Transactional
    @Modifying
    @Query("UPDATE Recommend r SET r.memberId1 = :recommendMember1, r.memberId2 = :recommendMember2, r.memberId3 = :recommendMember3 WHERE r.recommendedMember.id = :memberId")
    void updateRecommendMemberByMemberId(Long memberId, Long recommendMember1, Long recommendMember2, Long recommendMember3);
}
