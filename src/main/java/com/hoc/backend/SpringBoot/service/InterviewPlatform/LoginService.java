package com.hoc.backend.SpringBoot.service.InterviewPlatform;

import com.hoc.backend.SpringBoot.dto.LoginResponse;
import com.hoc.backend.SpringBoot.exception.InvalidPassWordException;
import com.hoc.backend.SpringBoot.model.User;
import com.hoc.backend.SpringBoot.repository.UserRepository;
import com.hoc.backend.SpringBoot.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoginService  {

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //    private int counterFail = 0; // counter how many times password is incorect <- but now it's a gobal variable, every user when enter
    // incorrect password always counterFail plus 1 -> often lock login function. -> so assign a new attribute at object user
    public final LoginResponse verify(String account, String passWorld) {

        User user = userRepository.findUser(account); // verify exist account

        // verify account locked or unlocked
        if (user.getLocked()) {

            // verify time unlock
            if (LocalDateTime.now().isBefore(user.getLockedUntil())) {

                throw new RuntimeException(
                        "Account locked until : " + user.getLockedUntil()
                );
            }

            // unlock account when time expired
            user.setLocked(false);
            user.setCounterFail(0);
            user.setLockedUntil(null);
        }

        // verify password
        if (!user.getPassWord().equals(passWorld)) {

            user.setCounterFail(user.getCounterFail() +1);

            // verify how many times incorrect password
            if (user.getCounterFail() >= 5) {

                user.setLocked(true);

                // lock account in 5 minutes
                user.setLockedUntil(
                        LocalDateTime.now().plusMinutes(5)
                );

                // save change status user
                // userRepository.save(user);

                throw new RuntimeException(
                        "Account locked in 5 minutes"
                );
            }

            // save fail counter
            // userRepository.save(user);

            throw new InvalidPassWordException(
                    "Wrong password"
            );
        }

        // reset counter fail when login success
        user.setCounterFail(0);

        // unlock if login success
        user.setLocked(false);

        // clear time lock
        user.setLockedUntil(null);

        // save login success
        // userRepository.save(user);

       String  accessToken =  JwtUtil.generateAccessToken(
                user.getAccount(),
                user.getRole()
        );

       String refreshToken = JwtUtil.genarateRefreshToken(
               user.getAccount(),
               user.getRole()
       );

       return new LoginResponse(accessToken,refreshToken);
    }
    public LoginResponse resfreshAccesToken(String refreshToken) {
        Claims claims = JwtUtil.validateRefreshToken(refreshToken);

        String account = claims.getSubject();
        String role  = (String) claims.get("role");

        String newAccessToken = JwtUtil.generateAccessToken(account,role);

        return new LoginResponse(newAccessToken,refreshToken);


    }
}
