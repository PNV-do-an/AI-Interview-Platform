package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.InterviewSlot;
import com.aiinterview.platform.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InterviewSlotRepository extends JpaRepository<InterviewSlot, Long> {

    List<InterviewSlot> findByEmployerAndCancelledFalseOrderByStartTimeAsc(User employer);

    List<InterviewSlot> findByEmployerAndBookedByIsNullAndCancelledFalseAndStartTimeAfterOrderByStartTimeAsc(
            User employer, LocalDateTime from);

    Optional<InterviewSlot> findByIdAndCancelledFalse(Long id);

    List<InterviewSlot> findByBookedByOrderByStartTimeAsc(User candidate);
}
