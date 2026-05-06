package com.hoc.backend.SpringBoot.repository;

import com.hoc.backend.SpringBoot.exception.InvalidAccountException;
import com.hoc.backend.SpringBoot.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class FakeUserRepository implements UserRepository{
    @Override
    public User findUser(String account) {
        if(account.equals("lenamle799@gmail.com")) {
            return new User("lenamle799@gmail.com","123456","admin"); // lấy dữ liệu giả
        }
        throw new InvalidAccountException("The account does't exist");
    }
}

