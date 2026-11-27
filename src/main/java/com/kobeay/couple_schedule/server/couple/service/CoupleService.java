package com.kobeay.couple_schedule.server.couple.service;

import com.kobeay.couple_schedule.server.couple.dto.CoupleInvitationResponse;
import com.kobeay.couple_schedule.server.couple.dto.CoupleMetDateUpdateRequest;
import com.kobeay.couple_schedule.server.couple.dto.CoupleRequest;
import com.kobeay.couple_schedule.server.couple.dto.CoupleResponse;
import com.kobeay.couple_schedule.server.couple.entity.Couple;
import com.kobeay.couple_schedule.server.couple.entity.CoupleInvitation;
import com.kobeay.couple_schedule.server.couple.entity.InvitationStatus;
import com.kobeay.couple_schedule.server.couple.repository.CoupleInvitationRepository;
import com.kobeay.couple_schedule.server.couple.repository.CoupleRepository;
import com.kobeay.couple_schedule.server.global.service.GcsService;
import com.kobeay.couple_schedule.server.schedule.repository.ScheduleRepository;
import com.kobeay.couple_schedule.server.user.entity.User;
import com.kobeay.couple_schedule.server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CoupleService {
    private final CoupleRepository coupleRepository;
    private final UserRepository userRepository;
    private final CoupleInvitationRepository coupleInvitationRepository;
    private final ScheduleRepository scheduleRepository;
    private final GcsService gcsService;

    public CoupleService(
            CoupleRepository coupleRepository,
            UserRepository userRepository,
            CoupleInvitationRepository coupleInvitationRepository,
            ScheduleRepository scheduleRepository,
            GcsService gcsService
    ) {
        this.coupleRepository = coupleRepository;
        this.userRepository = userRepository;
        this.coupleInvitationRepository = coupleInvitationRepository;
        this.scheduleRepository = scheduleRepository;
        this.gcsService = gcsService;
    }

    public void sendInvitation(Long userId, CoupleRequest request) {
        if (coupleRepository.existsByUser1_IdOrUser2_Id(userId, userId)) {
            throw new IllegalArgumentException("이미 커플 연결이 되어 있습니다.");
        }

        User sender = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );

        User receiver = userRepository.findByInviteCode(request.getInviteCode())
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 초대 코드입니다.")
                );

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("자기 자신에게 초대 요청은 불가능합니다.");
        }

        if (coupleRepository.existsByUser1_IdOrUser2_Id(receiver.getId(), receiver.getId())) {
            throw new IllegalArgumentException("상대방은 이미 커플이 연결되어 있습니다.");
        }

        if (coupleInvitationRepository.existsBySender_IdAndReceiver_IdAndStatus(sender.getId(), receiver.getId(), InvitationStatus.PENDING)) {
            throw new IllegalArgumentException(("이미 초대 요청을 보냈습니다."));
        }

        if (coupleInvitationRepository.existsBySender_IdAndReceiver_IdAndStatus(receiver.getId(), sender.getId(), InvitationStatus.PENDING)) {
            throw new IllegalArgumentException("상대방이 이미 초대 요청을 보냈습니다.");
        }

        CoupleInvitation coupleInvitation = new CoupleInvitation(sender, receiver);

        coupleInvitationRepository.save(coupleInvitation);
    }

    @Transactional
    public void acceptInvitation(Long userId, Long invitationId) {
        CoupleInvitation coupleInvitation = coupleInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 초대 요청입니다."));

        if (!coupleInvitation.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("초대 요청의 수신자만 수락할 수 있습니다.");
        }

        if (coupleInvitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("대기 중인 초대 요청이 아닙니다.");
        }

        if (coupleRepository.existsByUser1_IdOrUser2_Id(userId, userId)) {
            throw new IllegalArgumentException("이미 커플 연결이 되어 있습니다.");
        }

        if (coupleRepository.existsByUser1_IdOrUser2_Id(coupleInvitation.getSender().getId(), coupleInvitation.getSender().getId())) {
            throw new IllegalArgumentException("초대 요청자가 이미 커플로 연결되어 있습니다.");
        }

        User sender =coupleInvitation.getSender();

        User receiver = coupleInvitation.getReceiver();

        Couple couple = new Couple(sender, receiver);

        coupleInvitation.accept();

        coupleRepository.save(couple);
    }

    @Transactional
    public void rejectInvitation(Long userId, Long invitationId) {
        CoupleInvitation coupleInvitation = coupleInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 초대 요청입니다."));

        if (!coupleInvitation.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("초대 요청의 수신자만 거절할 수 있습니다.");
        }

        if (coupleInvitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("대기 중인 초대 요청이 아닙니다.");
        }

        coupleInvitation.reject();
    }

    public List<CoupleInvitationResponse> getReceivedInvitations(Long userId) {
        List<CoupleInvitation> invitations = coupleInvitationRepository.findByReceiver_IdAndStatus(userId, InvitationStatus.PENDING);

        return invitations.stream()
                .map(invitation -> new CoupleInvitationResponse(
                        invitation.getId(),
                        invitation.getSender().getNickname(),
                        invitation.getCreatedAt()
                ))
                .toList();
    }

    public CoupleResponse getCoupleInfo(Long userId) {
        Optional<Couple> optionalCouple = coupleRepository.findByUser1_IdOrUser2_Id(userId, userId);

        if (optionalCouple.isEmpty()) {
            return new CoupleResponse(false, null, null, null, null);
        }

        Couple couple = optionalCouple.get();

        User partner;

        if (couple.getUser1().getId().equals(userId)) {
            partner = couple.getUser2();
        } else {
            partner = couple.getUser1();
        }

        String partnerNickname = partner.getNickname();

        String profileImageUrl = null;

        if (partner.getProfileImageObjectName() != null) {
            profileImageUrl = gcsService.generateSignedUrl(partner.getProfileImageObjectName());
        }

        return new CoupleResponse(true, partnerNickname, couple.getConnectedAt(), couple.getMetDate(), profileImageUrl);
    }

    @Transactional
    public void disconnectCouple(Long userId) {
        Couple couple = coupleRepository.findByUser1_IdOrUser2_Id(userId, userId)
                        .orElseThrow(() -> new IllegalArgumentException("연결된 커플이 없습니다."));

        scheduleRepository.deleteByCoupleId(couple.getId());

        coupleRepository.delete(couple);
    }

    @Transactional
    public void updateMetDate(Long userId, CoupleMetDateUpdateRequest request) {
        Couple couple = coupleRepository.findByUser1_IdOrUser2_Id(userId, userId)
                .orElseThrow(() -> new IllegalArgumentException("커플이 연결되어 있지 않습니다."));

        LocalDate metDate = request.getMetDate();

        if (metDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("미래 날짜는 선택할 수 없습니다.");
        }

        couple.updateMetDate(metDate);
    }
}
