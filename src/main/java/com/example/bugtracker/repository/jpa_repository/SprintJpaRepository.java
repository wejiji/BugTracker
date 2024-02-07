package com.example.bugtracker.repository.jpa_repository;

import com.example.bugtracker.domain.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface SprintJpaRepository extends JpaRepository<Sprint,Long> {

    @Query("select s from Sprint s where s.id in:sprintIds and s.archived=false and s.project.id=:projectId")
    public Set<Sprint> findActiveSprintsByIdAndProjectId(@Param("sprintIds") Set<Long> sprintIds, @Param("projectId") Long projectId);


    @Query("select s from Sprint s where s.id=:sprintId and s.project.id=:projectId and s.archived=false")
    public Optional<Sprint> findByIdAndProjectIdAndArchivedFalse(@Param("sprintId") Long sprintId, @Param("projectId") Long projectId);

    public Set<Sprint> findByProjectIdAndArchivedTrue(Long projectId);

    public Set<Sprint> findByProjectIdAndArchivedFalse(Long projectId);

    public Optional<Sprint> findByIdAndArchivedFalse(Long sprintId);

    public Optional<Sprint> findByIdAndArchivedTrue(Long sprintId);

    @Query("select s from Sprint s where s.archived=false order by s.id asc limit 1")
    public Optional<Sprint> getNext();

    public Set<Sprint> findAllByProjectId(Long projectId);


}
