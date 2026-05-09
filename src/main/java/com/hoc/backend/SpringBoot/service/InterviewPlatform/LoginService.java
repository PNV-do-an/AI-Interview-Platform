package com.hoc.backend.SpringBoot.service.InterviewPlatform;

import com.hoc.backend.SpringBoot.exception.InvalidPassWordException;
import com.hoc.backend.SpringBoot.model.User;
import com.hoc.backend.SpringBoot.repository.UserRepository;
import com.hoc.backend.SpringBoot.security.JwtUtil;
import org.springframework.stereotype.Service;
@Service
public class LoginService  {

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public final String verify(String account, String passWorld) {
        User user = userRepository.findUser(account); // verify exist account
        //
        if (!user.getPassWord().equals(passWorld)) { // verify password
            throw new InvalidPassWordException("Wrong password");
        }
        return JwtUtil.generateToken (user.getAccount(),user.getRole());

    }
}
