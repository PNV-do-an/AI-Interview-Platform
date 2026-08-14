package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public class FakeUserRepository implements UserRepository {

    private final ArrayList<User> userArrayList = new ArrayList<>();

    @Override
    public Optional<User> findByEmail(String email) {
        return userArrayList.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userArrayList.stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public Optional<User> findByVerificationToken(String verificationToken) { //
        return userArrayList.stream()
                .filter(user -> verificationToken.equals(user.getVerificationToken()))
                .findFirst();
    }

    @Override
    public Optional<User> findByResetToken(String resetToken) {
        return userArrayList.stream()
                .filter(user -> resetToken != null && resetToken.equals(user.getResetToken()))
                .findFirst();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(generateIdUser());
        }
        Optional<User> existing = findByEmail(user.getEmail());
        if (existing.isPresent()) {
            int index = userArrayList.indexOf(existing.get());
            userArrayList.set(index, user);
        } else {
            userArrayList.add(user);
        }
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userArrayList.stream()
                .filter(user -> id.equals(user.getId()))
                .findFirst();
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
        Long latestId = userArrayList.get(userArrayList.size() - 1).getId();
        return latestId + 1;
    }
}
