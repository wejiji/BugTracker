package com.example.bugtracker.repository.repository_impls;

import com.example.bugtracker.domain.model.Sprint;
import com.example.bugtracker.repository.jpa_repository.SprintJpaRepository;
import com.example.bugtracker.repository.repository_interfaces.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class SprintRepositoryImpl implements SprintRepository {

    private final SprintJpaRepository sprintJpaRepository;

    @Override
    public Optional<Sprint> findById(Long sprintId) {
        return sprintJpaRepository.findById(sprintId);
    }

    @Override
    public Set<Sprint> findByProjectIdAndArchivedFalse(Long projectId) {
        return sprintJpaRepository.findByProjectIdAndArchivedFalse(projectId);
    }

    @Override
    public Sprint save(Sprint sprint) {
        return sprintJpaRepository.save(sprint);
    }

    @Override
    public Sprint getReferenceById(Long sprintId) {
        return sprintJpaRepository.getReferenceById(sprintId);
    }

    @Override
    public void deleteById(Long sprintId) {
        sprintJpaRepository.deleteById(sprintId);
    }

    @Override
    public Set<Sprint> findByProjectIdAndArchivedTrue(Long projectId) {
        return sprintJpaRepository.findByProjectIdAndArchivedTrue(projectId);
    }

    @Override
    public Optional<Sprint> findByIdAndArchivedTrue(Long sprintId) {
        return sprintJpaRepository.findByIdAndArchivedTrue(sprintId);
    }

    @Override
    public Optional<Sprint> findByIdAndProjectIdAndArchivedFalse(Long sprintId, Long projectId) {
        return sprintJpaRepository.findByIdAndProjectIdAndArchivedFalse(sprintId, projectId);
    }

    @Override
    public Set<Sprint> findActiveSprintsByIdAndProjectId(Set<Long> sprintIds, Long projectId) {
        return sprintJpaRepository.findActiveSprintsByIdAndProjectId(sprintIds, projectId);
    }

    @Override
    public Optional<Sprint> findByIdAndArchivedFalse(Long sprintId) {
        return sprintJpaRepository.findByIdAndArchivedFalse(sprintId);
    }

    @Override
    public Optional<Sprint> getNext() {
        return sprintJpaRepository.getNext();
    }

    @Override
    public void deleteAllByIdInBatch(Set<Long> sprintIds) {
        sprintJpaRepository.deleteAllByIdInBatch(sprintIds);
    }

    @Override
    public Set<Sprint> findAllByProjectId(Long projectId) {
        return sprintJpaRepository.findAllByProjectId(projectId);
    }

    @Override
    public Set<Sprint> saveAll(Set<Sprint> sprints) {
        return new HashSet<>(sprintJpaRepository.saveAll(sprints));
    }


}
