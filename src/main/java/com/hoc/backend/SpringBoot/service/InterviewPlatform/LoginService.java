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

//    private int counterFail = 0; // counter how many times password is incorect <- but now it's a gobal variable, every user when enter
                                    // incorrect password always counterFail plus 1 -> often lock login function. -> so assign a new attribute at object user
    public final String verify(String account, String passWorld) {
        User user = userRepository.findUser(account); // verify exist account
        //
        if (!user.getPassWord().equals(passWorld) && !user.getLocked()) { // verify password
            user.setCounterFail(user.getCounterFail() +1);
            if (user.getCounterFail() >= 5) {
                user.setLocked(true);
                // this is the time lock sign up  will unlock (how to do it)

            }
            throw new InvalidPassWordException("Wrong password");
        }
        else if (user.getLocked() &&  // điều kiện thời gian ) {

        }
        else {
            user.setCounterFail(0);
        }

        return JwtUtil.generateToken (user.getAccount(),user.getRole());

    }
}
