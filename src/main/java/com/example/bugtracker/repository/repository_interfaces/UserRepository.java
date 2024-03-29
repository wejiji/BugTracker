package com.example.bugtracker.repository.repository_interfaces;

import com.example.bugtracker.domain.model.User;

import java.util.List;
import java.util.Optional;


public interface UserRepository {

    Optional<User> findUserByUsername(String username);

    Optional<User> loadUserByUsernameWithAuthorities(String username);

    Optional<User> findById(Long userId);

    User getReferenceById(Long userId);

    User save(User user);

    void deleteByUsername(String username);

    List<User> findAll();

    User getReferenceByUsername(String username);

}
