package com.example.security2pro.service;


import com.example.security2pro.domain.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface UserService extends UserDetailsService {

    void createUser(User user);

    void updateUser(User user);

    void deleteUser(String username);


    List<User> findAll();

    boolean userExists(String username);

    Optional<User> findById(Long userId);

    User getReferenceById(Long userId);



}
