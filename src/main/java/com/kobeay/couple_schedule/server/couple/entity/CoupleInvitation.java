package com.kobeay.couple_schedule.server.couple.entity;

import com.kobeay.couple_schedule.server.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "couple_invitations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoupleInvitation {
    public CoupleInvitation(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = InvitationStatus.PENDING;
        this.createdAt = Instant.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public void accept() {
        this.status = InvitationStatus.ACCEPTED;
    }

    public void reject() {
        this.status = InvitationStatus.REJECTED;
    }
}
