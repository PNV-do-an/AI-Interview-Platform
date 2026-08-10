package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.RegisterAttempt;

public interface RegisterAttemptRepository {

    RegisterAttempt findRegisterAttempt(String ipClient);

    RegisterAttempt findRegisterAttemptByEmail(String email);

    Boolean checkRegisterAttempt(String ipClient);

    Boolean addRegisterAttempt(RegisterAttempt registerAttempt);

    long countFailedByIp(String ip, java.time.LocalDateTime since);

}
