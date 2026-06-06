package com.hoc.backend.SpringBoot.repository;

import com.hoc.backend.SpringBoot.exception.InvalidAccountException;
import com.hoc.backend.SpringBoot.model.RegisterAttempt;
import com.hoc.backend.SpringBoot.model.User;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Repository
public class FakeUserRepository implements UserRepository {

    public ArrayList<User> userArrayList = new ArrayList<>();
//    public ArrayList<RegisterAttempt> registerAttemptArrayList = new ArrayList<>();

    public FakeUserRepository() {
        userArrayList.add(
                new User("lenamle799@gmail.com", "123456", "admin")
        );
    }

    @Override
    public User findUser(String account) {
        for (User user : userArrayList) {
            if (user.getAccount().equals(account)) {
                return user;
            }
        }

        System.out.println("Throw exection");
        throw new InvalidAccountException("The account does't exist");
    }

    @Override
    public  Boolean checkExistUser (String account) {
        for (User user : userArrayList) {
            if (user.getAccount().equals(account)) {
                throw new InvalidAccountException("Account is exist");
            }
        }
        return false;
    }

    @Override
    public Boolean addUser(User user) {
        return userArrayList.add(user);
    }


//    Override
//    public
}