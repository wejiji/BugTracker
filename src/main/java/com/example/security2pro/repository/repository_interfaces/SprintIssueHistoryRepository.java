package com.example.security2pro.repository.repository_interfaces;

import com.example.security2pro.domain.model.SprintIssueHistory;

import java.util.ArrayList;
import java.util.Set;

public interface SprintIssueHistoryRepository {
    Set<SprintIssueHistory> findAllByArchivedSprintId(Long sprintId);

    void saveAll(ArrayList<SprintIssueHistory> collect);

}
