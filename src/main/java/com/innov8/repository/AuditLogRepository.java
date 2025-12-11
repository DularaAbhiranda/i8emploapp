package com.innov8.repository;

import com.innov8.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUsernameOrderByTimestampDesc(String username);
    List<AuditLog> findByActionOrderByTimestampDesc(String action);
    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId);
    
    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :startTime AND :endTime ORDER BY a.timestamp DESC")
    List<AuditLog> findByTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT a FROM AuditLog a WHERE a.username = :username AND a.timestamp BETWEEN :startTime AND :endTime ORDER BY a.timestamp DESC")
    List<AuditLog> findUserActivityByDateRange(@Param("username") String username, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
