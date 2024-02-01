package com.example.bugtracker.repository.repository_interfaces;

import com.example.bugtracker.domain.model.SprintIssueHistory;
import java.util.List;
import java.util.Set;

public interface SprintIssueHistoryRepository {

    Set<SprintIssueHistory> findAllByArchivedSprintId(Long sprintId);

    void saveAll(List<SprintIssueHistory> sprintIssueHistoryList);

}
