package com.example.ium.workrequest.repository;

import com.example.ium.workrequest.entity.WorkRequestEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class WorkRequestSpecification {

    public static Specification<WorkRequestEntity> filterWorkRequestsByConditions(Long expertId, String status) {
        return (Root<WorkRequestEntity> root,CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("expert"), expertId);
            log.info("status: {}", status);
            if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), WorkRequestEntity.Status.valueOf(status)));
            }

            return predicate;
        };
    }
}
