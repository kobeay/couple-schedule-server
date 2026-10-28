package com.kobeay.couple_schedule.server.couple.repository;

import com.kobeay.couple_schedule.server.couple.entity.CoupleInvitation;
import com.kobeay.couple_schedule.server.couple.entity.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoupleInvitationRepository extends JpaRepository<CoupleInvitation, Long> {
    boolean existsBySender_IdAndReceiver_IdAndStatus(
            Long senderId,
            Long receiverId,
            InvitationStatus status
    );
    List<CoupleInvitation> findByReceiver_IdAndStatus(Long receiverId, InvitationStatus status);
}
