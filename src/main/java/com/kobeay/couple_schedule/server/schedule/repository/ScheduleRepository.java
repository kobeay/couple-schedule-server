package com.kobeay.couple_schedule.server.schedule.repository;

import com.kobeay.couple_schedule.server.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
