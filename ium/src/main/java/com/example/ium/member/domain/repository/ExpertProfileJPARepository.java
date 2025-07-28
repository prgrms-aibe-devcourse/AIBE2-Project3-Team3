package com.example.ium.member.domain.repository;

import com.example.ium.member.domain.model.expert.ExpertProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpertProfileJPARepository extends JpaRepository<ExpertProfile, Long> {
    @Query("SELECT ep " +
            "FROM ExpertProfile ep " +
            "LEFT JOIN FETCH ep.attachments " +
            "LEFT JOIN FETCH ep.expertSpecialization " +
            "WHERE ep.memberId = :memberId")
    Optional<ExpertProfile> findByIdByEagerLoading(Long memberId);
}
