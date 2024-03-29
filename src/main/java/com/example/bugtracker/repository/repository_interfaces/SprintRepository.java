package com.example.bugtracker.repository.repository_interfaces;

import com.example.bugtracker.domain.model.Sprint;
import java.util.Optional;
import java.util.Set;

public interface SprintRepository {

    Optional<Sprint> findById(Long targetId);

    Set<Sprint> findByProjectIdAndArchivedFalse(Long projectId);

    Sprint save(Sprint sprint);

    Sprint getReferenceById(Long sprintId);

    void deleteById(Long id);

    Set<Sprint> findByProjectIdAndArchivedTrue(Long projectId);

    Optional<Sprint> findByIdAndArchivedTrue(Long sprintId);

    Optional<Sprint> findByIdAndProjectIdAndArchivedFalse(Long sprintId, Long projectId);

    Set<Sprint> findActiveSprintsByIdAndProjectId(Set<Long> sprintIds, Long projectId);

    Optional<Sprint> findByIdAndArchivedFalse(Long sprintId);

    Optional<Sprint> getNext();

    void deleteAllByIdInBatch(Set<Long> sprintIds);

    Set<Sprint> findAllByProjectId(Long projectId);


    Set<Sprint> saveAll(Set<Sprint> sprints);
}
