package com.example.bugtracker.repository.jpa_repository;

import com.example.bugtracker.domain.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProjectMemberJpaRepository extends JpaRepository<ProjectMember, Long> {


    @Query("select distinct pm from ProjectMember pm join fetch pm.authorities where pm.project.id =:projectId and pm.user.username =:username" )
    Optional<ProjectMember> findByUsernameAndProjectIdWithAuthorities(@Param("username") String username, @Param("projectId") Long projectId);

    @Query("select distinct pm from ProjectMember pm join fetch pm.authorities where pm.project.id =:projectId" )
    Set<ProjectMember> findAllMemberByProjectIdWithAuthorities(@Param("projectId") Long projectId);

    @Query("select pm from ProjectMember pm where pm.user.username=:username and pm.project.id=:projectId")
    Optional<ProjectMember> findByUsernameAndProjectId(@Param("username")String username,@Param("projectId") Long projectId);

    @Query("select distinct pm from ProjectMember pm join fetch pm.authorities where pm.id=:projectMemberId")
    Optional<ProjectMember> findByIdWithAuthorities(Long projectMemberId);

    @Query("select distinct pm from ProjectMember pm join fetch pm.authorities join fetch pm.user where pm.user.username in:usernames and pm.project.id =:projectId" )
    Set<ProjectMember> findAllByUsernameAndProjectIdWithUser(@Param("usernames")Collection<String> usernames, @Param("projectId") Long projectId);
    Set<ProjectMember> findAllByProjectId(Long projectId);

    @Query("select distinct pm from ProjectMember pm join fetch pm.authorities where pm.user.username=:username")
    Set<ProjectMember> findAllByUsernameWithProjectMemberAuthorities(String username);
}
