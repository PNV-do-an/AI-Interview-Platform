package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.InterviewAnswer;
import com.aiinterview.platform.model.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswer, Long> {

    List<InterviewAnswer> findBySessionOrderByQuestionIndexAsc(InterviewSession session);

    Optional<InterviewAnswer> findBySessionAndQuestionIndex(InterviewSession session, int questionIndex);
}
