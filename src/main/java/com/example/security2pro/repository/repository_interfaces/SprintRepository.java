package com.example.security2pro.repository.repository_interfaces;

import com.example.security2pro.domain.model.Sprint;

import java.util.Collection;
import java.util.List;
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
    Optional<Sprint> getNext(Long id);
    void deleteAllByIdInBatch(Set<Long> sprintIds);
    Set<Sprint> findAllByProjectId(Long projectId);

    Set<Sprint> saveAll(Set<Sprint> sprints);
}
