package com.example.bugtracker.repository.repository_impls;

import com.example.bugtracker.domain.model.SprintIssueHistory;
import com.example.bugtracker.repository.jpa_repository.SprintIssueHistoryJpaRepository;
import com.example.bugtracker.repository.repository_interfaces.SprintIssueHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SprintIssueHistoryRepositoryImpl implements SprintIssueHistoryRepository {

    private final SprintIssueHistoryJpaRepository sprintIssueHistoryJpaRepository;

    @Override
    public Set<SprintIssueHistory> findAllByArchivedSprintId(Long sprintId) {
        return sprintIssueHistoryJpaRepository.findAllByArchivedSprintId(sprintId) ;
    }

    @Override
    public void saveAll(List<SprintIssueHistory> sprintIssueHistories) {
        sprintIssueHistoryJpaRepository.saveAll(sprintIssueHistories);
    }
}
