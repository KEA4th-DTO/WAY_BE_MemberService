package com.dto.way.member.domain.repository;

import com.dto.way.member.domain.entity.Member;
import com.dto.way.member.domain.entity.Tag;
import com.dto.way.member.web.dto.TagDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT t.wayTag1, t.wayTag2, t.wayTag3 FROM Tag t WHERE t.taggedMember = :member")
    List<Object[]> findTagsByMember(@Param("member") Member member);

    @Transactional
    @Modifying
    @Query("UPDATE Tag t SET t.wayTag1 = :wayTag1, t.wayTag2 = :wayTag2, t.wayTag3 = :wayTag3 WHERE t.taggedMember.id = :memberId")
    void updateTagWayTagsByMemberId(Long memberId, String wayTag1, String wayTag2, String wayTag3);
}
