package com.example.ium.member.domain.repository;

import com.example.ium.member.domain.model.expert.ExpertSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertSpecializationJPARepository extends JpaRepository<ExpertSpecialization, Long> {
}
