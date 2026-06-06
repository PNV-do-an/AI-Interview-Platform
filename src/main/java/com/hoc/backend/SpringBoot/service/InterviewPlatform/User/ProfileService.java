package com.hoc.backend.SpringBoot.service.InterviewPlatform.User;

import com.hoc.backend.SpringBoot.dto.ProfileResponse;
import com.hoc.backend.SpringBoot.model.User;
import com.hoc.backend.SpringBoot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileService (UserRepository userRepository) {this.userRepository = userRepository;}

    public ProfileResponse getInfor(String account) {

        User user = userRepository.findUser(account);

        return new ProfileResponse(user.getAccount(),user.getCounterFail(),user.getRole());
    }
}
