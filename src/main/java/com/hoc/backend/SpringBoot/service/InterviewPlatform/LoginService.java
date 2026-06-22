package com.hoc.backend.SpringBoot.service.InterviewPlatform;

import com.hoc.backend.SpringBoot.dto.LoginResponse;
import com.hoc.backend.SpringBoot.exception.InvaildAccountLockedException;
import com.hoc.backend.SpringBoot.exception.InvalidPassWordException;
import com.hoc.backend.SpringBoot.model.User;
import com.hoc.backend.SpringBoot.repository.UserRepository;
import com.hoc.backend.SpringBoot.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginService  {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public final LoginResponse verify(String account, String passWord) {

        User user = userRepository.findUser(account);

        if (user.getLocked()) {

            if (LocalDateTime.now().isBefore(user.getLockedUntil())) {

                throw new InvaildAccountLockedException(
                        "Account locked until : " , user.getLockedUntil()
                );
            }

            user.setLocked(false);
            user.setCounterFail(0);
            user.setLockedUntil(null);
        }

        if (!passwordEncoder.matches(passWord, user.getPassWord())) {
            user.setCounterFail(user.getCounterFail() +1);

            if (user.getCounterFail() >= 5) {

                user.setLocked(true);

                user.setLockedUntil(
                        LocalDateTime.now().plusMinutes(5)
                );

                throw new InvaildAccountLockedException(
                        "Account locked in 5 minutes " , user.getLockedUntil()
                );
            }

            System.out.println( "CounterFailed = " + user.getCounterFail());
            throw new InvalidPassWordException(("Wrong password"));
        }

        user.setCounterFail(0);

        user.setLocked(false);

        user.setLockedUntil(null);

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

    public LoginResponse refreshAccessToken(String refreshToken) {
        Claims claims = JwtUtil.validateRefreshToken(refreshToken);

        String account = claims.getSubject();
        String role  = (String) claims.get("role");

        String newAccessToken = JwtUtil.generateAccessToken(account,role);

        return new LoginResponse(newAccessToken,refreshToken);

    }
}
