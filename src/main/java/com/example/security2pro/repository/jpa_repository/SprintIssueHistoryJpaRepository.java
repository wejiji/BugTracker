package com.example.security2pro.repository.jpa_repository;

import com.example.security2pro.domain.model.SprintIssueHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SprintIssueHistoryJpaRepository extends JpaRepository<SprintIssueHistory, Long> {
    @Query("select sih from SprintIssueHistory sih where sih.archivedSprint.id =:archivedSprintId and sih.archivedSprint.project.id=:projectId")
    public Set<SprintIssueHistory> findBySprintIdAndProjectId(@Param("archivedSprintId") Long archivedSprintId, @Param("projectId") Long projectId);

    @Query("select sih from SprintIssueHistory sih where sih.archivedSprint.id =:archivedSprintId")
    public Set<SprintIssueHistory> findAllByArchivedSprintId(@Param("archivedSprintId") Long archivedSprintId);

}
