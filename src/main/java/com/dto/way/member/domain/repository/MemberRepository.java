package com.dto.way.member.domain.repository;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.entity.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findById(Long id);

    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.nickname = :nickname")
    Optional<Member> findByNickname(String nickname);

    @Query("SELECT m FROM Member m WHERE m.memberStatus = :memberStatus")
    List<Member> findByMemberStatus(MemberStatus memberStatus);

    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.nickname = :nickname, m.introduce = :introduce, m.profileImageUrl = :profileImageUrl WHERE m.id = :id")
    int updateMemberProfile(@Param("id") Long id, @Param("nickname") String nickname, @Param("introduce") String introduce, @Param("profileImageUrl") String profileImageUrl);


    Page<Member> findByNicknameContaining(String keyword, Pageable pageable);
    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);
}
