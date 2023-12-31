package com.example.security2pro.repository.jpa_repository;


import com.example.security2pro.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    //타입은 User여야 한다.
    // SecurityUser 클래스는 DB에 저장되는 정보를 저장하는것이 아니라
    // authentication 프로세스 도중에만 쓰임

    void deleteByUsername(String username);
    Optional<User> findUserByUsername(String username);


}
