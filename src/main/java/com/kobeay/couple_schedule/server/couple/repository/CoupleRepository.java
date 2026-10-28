package com.kobeay.couple_schedule.server.couple.repository;

import com.kobeay.couple_schedule.server.couple.entity.Couple;
import com.kobeay.couple_schedule.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoupleRepository extends JpaRepository<Couple, Long> {
    Optional<Couple> findByUser1_IdOrUser2_Id(Long user1Id, Long user2Id);
    boolean existsByUser1_IdOrUser2_Id(Long user1Id, Long user2Id);
}