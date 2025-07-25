package com.example.ium.workrequest.repository;

import com.example.ium.workrequest.entity.WorkRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkRequestRepository extends JpaRepository<WorkRequestEntity, Long> {
    Optional<WorkRequestEntity> findTopByOrderByIdDesc();
    
    // ad_point 높은 순으로 정렬
    List<WorkRequestEntity> findAllByOrderByAdPointDesc();
    
    // 카테고리별 필터링 (ad_point 높은 순)
    @Query("SELECT w FROM WorkRequestEntity w WHERE " + "LOWER(w.category) LIKE LOWER(CONCAT('%', :category, '%')) " + "ORDER BY w.adPoint DESC")
    List<WorkRequestEntity> findByCategoryContainingIgnoreCaseOrderByAdPointDesc(@Param("category") String category);
    
    // 검색 기능 (제목, 내용, 카테고리에서 검색)
    @Query("SELECT w FROM WorkRequestEntity w WHERE " +
           "(LOWER(w.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(w.content) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(w.category) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY w.adPoint DESC")
    List<WorkRequestEntity> findBySearchTermOrderByAdPointDesc(@Param("search") String search);
    
    // 카테고리와 검색 조건 모두 적용
    @Query("SELECT w FROM WorkRequestEntity w WHERE " +
           "LOWER(w.category) LIKE LOWER(CONCAT('%', :category, '%')) AND " +
           "(LOWER(w.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(w.content) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(w.category) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY w.adPoint DESC")
    List<WorkRequestEntity> findByCategoryAndSearchOrderByAdPointDesc(@Param("category") String category, @Param("search") String search);

    @Query("SELECT w.status, COUNT(w) " +
           "FROM WorkRequestEntity w " +
           "WHERE w.expert = :memberId " +
           "GROUP BY w.status")
    List<Object[]> countMyWorkRequestsByStatus(@Param("memberId") Long memberId);
}