package com.example.security2pro.repository.repository_impls;

import com.example.security2pro.domain.model.User;
import com.example.security2pro.repository.jpa_repository.UserJpaRepository;
import com.example.security2pro.repository.repository_interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    @Override
    public Optional<User> findUserByUsername(String username) {
        return userJpaRepository.findUserByUsername(username);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public User getReferenceById(Long userId) {
        return userJpaRepository.getReferenceById(userId);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public void deleteByUsername(String username) {
        userJpaRepository.deleteByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }
}
