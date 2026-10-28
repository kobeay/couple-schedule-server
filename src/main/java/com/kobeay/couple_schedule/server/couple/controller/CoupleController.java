package com.kobeay.couple_schedule.server.couple.controller;

import com.kobeay.couple_schedule.server.couple.dto.CoupleInvitationResponse;
import com.kobeay.couple_schedule.server.couple.dto.CoupleRequest;
import com.kobeay.couple_schedule.server.couple.service.CoupleService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/couples")
public class CoupleController {

    private final CoupleService coupleService;

    public CoupleController(CoupleService coupleService) {
        this.coupleService = coupleService;
    }

    @PostMapping("/invitations")
    public void sendInvitation(
            Authentication authentication,
            @Valid @RequestBody CoupleRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        coupleService.sendInvitation(userId, request);
    }

    @PostMapping("/invitations/{invitationId}/accept")
    public void acceptInvitation(
            Authentication authentication,
            @PathVariable Long invitationId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        coupleService.acceptInvitation(userId, invitationId);
    }

    @PostMapping("/invitations/{invitationId}/reject")
    public void rejectInvitation(
            Authentication authentication,
            @PathVariable Long invitationId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        coupleService.rejectInvitation(userId, invitationId);
    }

    @GetMapping("/invitations/received")
    public List<CoupleInvitationResponse> getReceivedInvitations(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        return coupleService.getReceivedInvitations(userId);
    }
}