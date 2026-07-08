package com.hoc.backend.SpringBoot.repository;

import com.hoc.backend.SpringBoot.model.User;

public interface UserRepository {

    public User findUserByEmail(String email);

    public Boolean checkExistUser(String email);

    public Boolean addUser(User user);

    public Long generateIdUser();
}
