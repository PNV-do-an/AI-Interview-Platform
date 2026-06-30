package com.hoc.backend.SpringBoot.repository;

import com.hoc.backend.SpringBoot.exception.InvalidAccountException;
import com.hoc.backend.SpringBoot.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public class FakeUserRepository implements UserRepository {

    public ArrayList<User> userArrayList = new ArrayList<>();

    @Override
    public User findUserByEmail(String email) {
        for (User user : userArrayList) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        throw new InvalidAccountException("Email không tồn tại");
    }

    @Override
    public  Boolean checkExistUser (String email) {
        for (User user : userArrayList) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean addUser(User user) {
        return userArrayList.add(user);
    }

    @Override
    public Long generateIdUser() {
        if (userArrayList.isEmpty()) {
            return 1L;
        }
        Long lastestId = userArrayList.get(userArrayList.size()-1).getId();
        return ++lastestId;
    }
}
