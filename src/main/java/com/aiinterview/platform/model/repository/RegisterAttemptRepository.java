package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.RegisterAttempt;

public interface RegisterAttemptRepository {

    public RegisterAttempt findRegisterAttempt (String ipClient);

    public Boolean checkRegisterAttempt(String ipClient);

    public Boolean addRegisterAttempt (RegisterAttempt registerAttempt);

}
