package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.RegisterAttempt;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Repository
public class FakeRegisterAttempt implements RegisterAttemptRepository{

    private ArrayList<RegisterAttempt> registerAttemptArrayList = new ArrayList<>();

    @Override
    public RegisterAttempt findRegisterAttempt(String ipClient) {
        for (RegisterAttempt temp : registerAttemptArrayList) {
            if (temp.getAddress().equals(ipClient)) {
                return temp;
            }
        }
        return null;
    }

    @Override
    public RegisterAttempt findRegisterAttemptByEmail(String email) {
        for (RegisterAttempt temp : registerAttemptArrayList) {
            if (email.equals(temp.getAccount())) {
                return temp;
            }
        }
        return null;
    }

    @Override
    public Boolean checkRegisterAttempt(String ipClient) {
            for (RegisterAttempt temp : registerAttemptArrayList) {
                if (temp.getAddress().equals(ipClient)) {
                    return true;
                }
            }
            return false;
    }

    @Override
    public Boolean addRegisterAttempt(RegisterAttempt registerAttempt) {
        return registerAttemptArrayList.add(registerAttempt);
    }

    @Override
    public long countFailedByIp(String ip, LocalDateTime since) {
        return registerAttemptArrayList.stream()
                .filter(a -> a.getAddress().equals(ip))
                .filter(a -> a.getTimestamp() != null && a.getTimestamp().isAfter(since))
                .count();
    }
}
