package com.example.ium.specialization.domain.repository;

import com.example.ium.specialization.domain.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecializationJPARepository extends JpaRepository<Specialization, Long> {

}
