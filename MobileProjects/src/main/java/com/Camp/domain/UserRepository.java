package com.Camp.domain;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final List<User> users = new ArrayList<>();

    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    public Optional<User> findByNickname(String nickname) {
        return users.stream()
                .filter(u -> u.getNickname().equals(nickname))
                .findFirst();
    }

    public void save(User user) {
        users.add(user);
    }
}