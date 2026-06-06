package com.hoc.backend.SpringBoot.repository;

import com.hoc.backend.SpringBoot.model.RegisterAttempt;

public interface RegisterAttemptRepository {

    public RegisterAttempt findRegisterAttempt (String ipClient);

    public Boolean checkRegisterAttempt(String ipClient);

    public Boolean addRegisterAttempt (RegisterAttempt registerAttempt);
}
