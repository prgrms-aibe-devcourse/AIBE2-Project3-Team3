package com.example.ium.workrequest.repository;

import com.example.ium.workrequest.entity.WorkRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WorkRequestRepository extends JpaRepository<WorkRequestEntity, Long> {
    Optional<WorkRequestEntity> findTopByOrderByIdDesc();
}