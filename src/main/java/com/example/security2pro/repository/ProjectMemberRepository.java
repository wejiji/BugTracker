package com.example.security2pro.repository;


import com.example.security2pro.domain.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {


    @Query("select pm from ProjectMember pm join fetch pm.authorities where pm.project.id =:projectId and pm.user.id =:userId" )
    Optional<ProjectMember> findByUserIdAndProjectIdWithAuthorities(@Param("userId") Long userId, @Param("projectId") Long projectId);

    @Query("select pm from ProjectMember pm join fetch pm.authorities join fetch pm.user where pm.project.id =:projectId" )
    Set<ProjectMember> findAllMemberByProjectIdWithUser(@Param("projectId") Long projectId);
    //needs more optimization because of eager fetch

    @Query("select pm from ProjectMember pm join fetch pm.authorities join fetch pm.user where pm.user.username in:usernames and pm.project.id =:projectId" )
    Set<ProjectMember> findAllByUsernameAndProjectIdWithUser(@Param("usernames")Collection<String> usernames, @Param("projectId") Long projectId);


    @Query("select pm from ProjectMember pm where pm.user.id=:userId and pm.project.id=:projectId")
    Optional<ProjectMember> findByUserIdAndProjectId(Long userId, Long projectId);


}
