package com.example.bugtracker.repository.jpa_repository;


import com.example.bugtracker.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    // Repository for User type
    // -SecurityUser is only used during authentication process.
    void deleteByUsername(String username);
    @Query("select distinct u from User u join fetch u.authorities where u.username=:username")
    Optional<User> loadUserByUsernameWithAuthorities(String username);

    Optional<User> findUserByUsername(String username);
    User getReferenceByUsername(String username);
}
