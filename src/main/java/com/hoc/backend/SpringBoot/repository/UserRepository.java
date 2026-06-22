package com.hoc.backend.SpringBoot.repository;

import com.hoc.backend.SpringBoot.model.User;

public interface UserRepository {

    public User findUser(String account);

    public Boolean checkExistUser (String account);

    public Boolean addUser (User user);

    public Long generateIdUser();
}
