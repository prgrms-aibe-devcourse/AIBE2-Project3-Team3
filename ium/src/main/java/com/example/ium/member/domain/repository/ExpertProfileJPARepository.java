package com.example.ium.member.domain.repository;

import com.example.ium.member.domain.model.expert.ExpertProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertProfileJPARepository extends JpaRepository<ExpertProfile, Long> {
}
