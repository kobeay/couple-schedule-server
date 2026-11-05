package com.kobeay.couple_schedule.server.schedule.service;

import com.kobeay.couple_schedule.server.couple.entity.Couple;
import com.kobeay.couple_schedule.server.couple.repository.CoupleRepository;
import com.kobeay.couple_schedule.server.schedule.dto.ScheduleCreateRequest;
import com.kobeay.couple_schedule.server.schedule.entity.Schedule;
import com.kobeay.couple_schedule.server.schedule.entity.ScheduleType;
import com.kobeay.couple_schedule.server.schedule.repository.ScheduleRepository;
import com.kobeay.couple_schedule.server.user.entity.User;
import com.kobeay.couple_schedule.server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {
    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(UserRepository userRepository, CoupleRepository coupleRepository, ScheduleRepository scheduleRepository) {
        this.userRepository = userRepository;
        this.coupleRepository = coupleRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public void createSchedule(Long userId, ScheduleCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Couple couple = null;

        if (request.getType() == ScheduleType.COUPLE) {
            couple = coupleRepository.findByUser1_IdOrUser2_Id(userId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("커플이 연결되어 있지 않아요."));
        }

        if ((request.getStartTime() == null) != (request.getEndTime() == null)) {
            throw new IllegalArgumentException("시작 시간과 종료 시간을 모두 입력해 주세요.");
        }

        if (request.getStartTime() != null && !request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("종료 시간이 더 늦어야 합니다.");
        }

        Schedule schedule = new Schedule(
                user,
                couple,
                request.getTitle(),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getMemo(),
                request.getType()
        );

        scheduleRepository.save(schedule);
    }
}
