package com.pop.rules.repositories;

import com.pop.rules.entities.Event;
import com.pop.rules.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("""
    SELECT COALESCE(SUM(e.amount), 0)
    FROM Event e
    WHERE e.userId = :userId
      AND e.eventType = :eventType
      AND e.date BETWEEN :startDate AND :endDate
""")
    Double sumAmountByUserAndTypeAndDateRange(@Param("userId") Long userId,
                                              @Param("eventType") EventType eventType,
                                              @Param("startDate") LocalDateTime start,
                                              @Param("endDate") LocalDateTime end);
}
