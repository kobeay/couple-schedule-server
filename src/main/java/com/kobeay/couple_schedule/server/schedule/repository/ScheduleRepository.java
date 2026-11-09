package com.kobeay.couple_schedule.server.schedule.repository;

import com.kobeay.couple_schedule.server.schedule.entity.Schedule;
import com.kobeay.couple_schedule.server.schedule.entity.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("""
    SELECT s FROM Schedule s
    WHERE s.date BETWEEN :startDate AND :endDate
    AND (
        (s.type = :personalType AND s.user.id = :userId)
        OR
        (s.type = :coupleType AND s.couple.id = :coupleId)
    )
    """)
    List<Schedule> findMonthlySchedules(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("userId") Long userId,
            @Param("coupleId") Long coupleId,
            @Param("personalType") ScheduleType personalType,
            @Param("coupleType") ScheduleType coupleType
    );
}
