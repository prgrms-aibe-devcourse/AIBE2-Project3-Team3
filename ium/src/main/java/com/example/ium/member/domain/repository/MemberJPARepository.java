package com.example.ium.member.domain.repository;

import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.infrastructure.repository.MemberMetaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberJPARepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(Email email);
    boolean existsByEmail(Email email);

    @Query("""
        SELECT 
            m.email AS email,
            m.username AS username,
            ep.memberId AS expertProfileId,
            ep.activated AS isExpert,
            es.id.specializationId AS specializationId
        FROM Member m
            LEFT JOIN ExpertProfile ep ON m.id = ep.member.id 
            LEFT JOIN ExpertSpecialization es ON ep.memberId = es.expertProfile.memberId
        WHERE m.email.value = :email
    """)
    List<MemberMetaProjection> findMemberMetaByEmail(@Param("email") String email);
}
