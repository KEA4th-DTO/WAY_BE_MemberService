package com.dto.way.member.domain.repository;

import com.dto.way.member.domain.entity.Follow;
import com.dto.way.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    List<Follow> findByFromMember(Member from_member);
    List<Follow> findByToMember(Member to_member);

    // 로그인 한 사용자가 팔로잉을 삭제함
    void deleteByFromMemberAndToMember(Member from_member, Member to_member);

    @Query("select f from Follow f where f.fromMember = :from and f.toMember = :to")
    Optional<Follow> findFollow(@Param("from") Member from_member, @Param("to") Member to_Member);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.fromMember.id = :memberId")
    Long countByFromMemberId(@Param("memberId") Long memberId);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.toMember.id = :memberId")
    Long countByToMemberId(@Param("memberId") Long memberId);
}
