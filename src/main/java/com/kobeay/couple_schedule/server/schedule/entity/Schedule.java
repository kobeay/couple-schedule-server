package com.kobeay.couple_schedule.server.schedule.entity;

import com.kobeay.couple_schedule.server.couple.entity.Couple;
import com.kobeay.couple_schedule.server.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_id")
    private Couple couple;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleType type;

    public Schedule(
            User user,
            Couple couple,
            String title,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            String memo,
            ScheduleType type
    ) {
        this.user = user;
        this.couple = couple;
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.memo = memo;
        this.type = type;
    }

    public void update(
            Couple couple,
            String title,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            String memo,
            ScheduleType type
    ) {
        this.couple = couple;
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.memo = memo;
        this.type = type;
    }
}
