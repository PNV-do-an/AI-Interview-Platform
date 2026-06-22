package com.hoc.backend.SpringBoot.service.InterviewPlatform;

import com.hoc.backend.SpringBoot.dto.ResgiterResponse;
import com.hoc.backend.SpringBoot.exception.InvalidAccountException;
import com.hoc.backend.SpringBoot.exception.InvalidPassWordException;
import com.hoc.backend.SpringBoot.model.RegisterAttempt;
import com.hoc.backend.SpringBoot.model.User;
import com.hoc.backend.SpringBoot.repository.RegisterAttemptRepository;
import com.hoc.backend.SpringBoot.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.hoc.backend.SpringBoot.security.utils.SecurityUtils.getClientIP;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final RegisterAttemptRepository registerAttemptRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterService (UserRepository userRepository, RegisterAttemptRepository registerAttemptRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.registerAttemptRepository = registerAttemptRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResgiterResponse resgiter (String account, String passWord, HttpServletRequest request) {

        String ipClient = getClientIP(request);

        RegisterAttempt attempt = registerAttemptRepository.findRegisterAttempt(ipClient);
        if (attempt == null) {
            attempt = new RegisterAttempt(ipClient, account);
            registerAttemptRepository.addRegisterAttempt(attempt);
        }

        if (attempt.getLocked() != null && attempt.getLocked()) {
            if (LocalDateTime.now().isBefore(attempt.getLockUntil())) {
                throw new InvalidPassWordException("Too many registration attempts. Try again after " + attempt.getLockUntil());
            }
            attempt.setLocked(false);
            attempt.setCounterFail(0);
            attempt.setLockUntil(null);
        }

        if (userRepository.checkExistUser(account)) {
            throw new InvalidAccountException("Account existed");
        }

        if (passWord == null || passWord.length() < 8) {
            attempt.setCounterFail(attempt.getCounterFail() + 1);

            if (attempt.getCounterFail() >= 5) {
                attempt.setLocked(true);
                attempt.setLockUntil(LocalDateTime.now().plusMinutes(5));
            }

            throw new InvalidPassWordException("Length of password need more than 8");
        }

        User newUser = new User(
                userRepository.generateIdUser(),
                account,
                passwordEncoder.encode(passWord),
                "user"
        );
        userRepository.addUser(newUser);

        attempt.setIdUser(newUser.getId());
        attempt.setCounterFail(0);
        attempt.setLocked(false);
        attempt.setLockUntil(null);

        return new ResgiterResponse("Create Success");
    }
}

