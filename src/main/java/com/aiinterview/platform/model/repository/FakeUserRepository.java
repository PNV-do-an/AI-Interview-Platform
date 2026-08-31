package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Optional<User> findByVerificationToken(String verificationToken) {
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
        Optional<User> existing = findById(user.getId());
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
    public void deleteAll() {
        userArrayList.clear();
    }

    @Override
    public Page<User> findAllWithFilters(String search, Role role, String status, Pageable pageable) {
        List<User> filtered = userArrayList.stream()
                .filter(u -> {
                    if (search != null) {
                        String s = search.toLowerCase();
                        boolean match = u.getEmail().toLowerCase().contains(s)
                                || (u.getFullName() != null && u.getFullName().toLowerCase().contains(s));
                        if (!match) return false;
                    }
                    if (role != null && !role.equals(u.getRole())) return false;
                    if (status != null) {
                        return switch (status) {
                            case "ACTIVE"   -> u.isEnabled() && !u.isLocked() && u.getDeletedAt() == null;
                            case "INACTIVE" -> !u.isEnabled() && u.getDeletedAt() == null;
                            case "LOCKED"   -> u.isLocked() && u.getDeletedAt() == null;
                            default -> true;
                        };
                    }
                    return true;
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<User> page = (start <= end) ? filtered.subList(start, end) : List.of();
        return new PageImpl<>(page, pageable, filtered.size());
    }

    private Long generateIdUser() {
        if (userArrayList.isEmpty()) {
            return 1L;
        }
        Long latestId = userArrayList.stream()
                .map(User::getId)
                .filter(id -> id != null)
                .max(Long::compareTo)
                .orElse(0L);
        return latestId + 1;
    }
}
