package com.kobeay.couple_schedule.server.schedule.service;

import com.kobeay.couple_schedule.server.couple.entity.Couple;
import com.kobeay.couple_schedule.server.couple.repository.CoupleRepository;
import com.kobeay.couple_schedule.server.schedule.dto.ScheduleCreateRequest;
import com.kobeay.couple_schedule.server.schedule.dto.ScheduleListResponse;
import com.kobeay.couple_schedule.server.schedule.dto.ScheduleUpdateRequest;
import com.kobeay.couple_schedule.server.schedule.entity.Schedule;
import com.kobeay.couple_schedule.server.schedule.entity.ScheduleType;
import com.kobeay.couple_schedule.server.schedule.repository.ScheduleRepository;
import com.kobeay.couple_schedule.server.user.entity.User;
import com.kobeay.couple_schedule.server.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

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

        if (request.getStartTime() == null && request.getEndTime() != null) {
            throw new IllegalArgumentException("시작 시간을 먼저 입력해 주세요.");
        }

        if (request.getStartTime() != null && request.getEndTime() != null && !request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 늦어야 해요.");
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

    @Transactional(readOnly = true)
    public List<ScheduleListResponse> getMonthlySchedules(Long userId, int year, int month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Couple couple = coupleRepository.findByUser1_IdOrUser2_Id(userId, userId)
                .orElse(null);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Long coupleId = couple != null ? couple.getId() : null;

        Long partnerId = null;

        if (couple != null) {
            if (couple.getUser1().getId().equals(userId)) {
                partnerId = couple.getUser2().getId();
            } else {
                partnerId = couple.getUser1().getId();
            }
        }

        List<Schedule> schedules = scheduleRepository.findMonthlySchedules(startDate, endDate, userId, partnerId, coupleId, ScheduleType.PERSONAL, ScheduleType.COUPLE);

        return schedules.stream()
                .map(ScheduleListResponse::from)
                .toList();
    }

    @Transactional
    public void updateSchedule(ScheduleUpdateRequest request, Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        boolean isOwner = schedule.getUser().getId().equals(userId);

        boolean isCoupleSchedule = schedule.getType() == ScheduleType.COUPLE;

        Couple currentCouple = coupleRepository.findByUser1_IdOrUser2_Id(userId, userId)
                .orElse(null);

        boolean isSameCouple = isCoupleSchedule
                && currentCouple != null
                && schedule.getCouple() != null
                && schedule.getCouple().getId().equals(currentCouple.getId());

        if (!isOwner && !isSameCouple) {
            throw new IllegalArgumentException("일정을 수정할 권한이 없어요.");
        }

        if (isCoupleSchedule && !isSameCouple) {
            throw new IllegalArgumentException("현재 연결된 커플의 일정만 수정할 수 있어요.");
        }

        if (!isOwner && schedule.getType() != request.getType()) {
            throw new IllegalArgumentException("일정 종류를 변경할 권한이 없어요.");
        }

        Couple couple = null;

        if (request.getType() == ScheduleType.COUPLE) {
            if (currentCouple == null) {
                throw new IllegalArgumentException("커플이 연결되어 있지 않아요.");
            }

            couple = currentCouple;
        }

        if (request.getStartTime() == null && request.getEndTime() != null) {
            throw new IllegalArgumentException("시작 시간을 먼저 입력해 주세요.");
        }

        if (request.getStartTime() != null && request.getEndTime() != null && !request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 늦어야 해요.");
        }

        schedule.update(
                couple,
                request.getTitle(),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getMemo(),
                request.getType()
        );
    }

    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        if (schedule.getType() == ScheduleType.PERSONAL) {
            if (!schedule.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("일정을 삭제할 권한이 없습니다.");
            }
        }

        if (schedule.getType() == ScheduleType.COUPLE) {
            Couple currentCouple = coupleRepository.findByUser1_IdOrUser2_Id(userId, userId)
                    .orElse(null);

            if (currentCouple == null
                    || schedule.getCouple() == null
                    || !schedule.getCouple().getId().equals(currentCouple.getId())) {
                throw new IllegalArgumentException("일저을 삭제할 권한이 없습니다.");
            }
        }

        scheduleRepository.delete(schedule);
    }
}
