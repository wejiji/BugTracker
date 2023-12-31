package com.example.security2pro.repository.repository_impls;

import com.example.security2pro.domain.model.SprintIssueHistory;
import com.example.security2pro.repository.jpa_repository.SprintIssueHistoryJpaRepository;
import com.example.security2pro.repository.repository_interfaces.SprintIssueHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
    public void saveAll(ArrayList<SprintIssueHistory> sprintIssueHistories) {
        sprintIssueHistoryJpaRepository.saveAll(sprintIssueHistories);
    }
}
