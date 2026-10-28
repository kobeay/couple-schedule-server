package com.kobeay.couple_schedule.server.couple.entity;

import com.kobeay.couple_schedule.server.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "couples")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Couple {
    public Couple(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.connectedAt = Instant.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @Column(name = "connected_at", nullable = false)
    private Instant connectedAt;

    @Column(name = "met_date")
    private LocalDate metDate;
}
