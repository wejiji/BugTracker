package com.example.bugtracker.repository.jpa_repository;

import com.example.bugtracker.domain.model.auth.RefreshTokenData;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenJpaRepository extends JpaRepository<RefreshTokenData, Long> {

    @EntityGraph("RefreshTokenData.withUser")
    @Query("select rf from RefreshTokenData rf where rf.refreshTokenString=:refreshTokenString")
    Optional<RefreshTokenData> findByRefreshTokenStringWithUserAuthorities(@Param("refreshTokenString") String refreshTokenString);

    @Query("delete from RefreshTokenData rf where rf.refreshTokenString=:refreshTokenString")
    @Modifying
    void deleteByRefreshTokenString(@Param("refreshTokenString") String refreshTokenString);
    @Query("select rf from RefreshTokenData rf where rf.user.username=:username")
    Optional<RefreshTokenData> findByUsername(@Param("username") String username);


}
