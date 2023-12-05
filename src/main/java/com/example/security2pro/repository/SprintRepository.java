package com.example.security2pro.repository;

import com.example.security2pro.domain.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SprintRepository extends JpaRepository<Sprint,Long> {

    @Query("select s from Sprint s where s.id=:sprintId")
    public Set<Sprint> findSprintsById(@Param("sprintId") Long sprintId);
    @Query("select s from Sprint s where s.id=:sprintId and s.archived=false")
    public Optional<Sprint> findActiveSprintById(@Param("sprintId") Long sprintId);

    @Query("select s from Sprint s where s.project.id=:projectId and s.archived=false")
    public Set<Sprint> findActiveSprintsByProjectId(@Param("projectId") Long projectId);


}
