package com.example.security2pro.dto.sprinthistory;

import com.example.security2pro.domain.enums.IssueStatus;

import com.example.security2pro.domain.model.SprintIssueHistory;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class SprintIssueHistoryDto {

    private final Long id;

    private final Long sprintId;

    private final Long issueId;

    private final String issueName;

    private final String issueDescription;

    private final IssueStatus issueStatus;

    private final boolean complete;

    public SprintIssueHistoryDto(SprintIssueHistory sprintissue){
        id = sprintissue.getId();
        sprintId = sprintissue.getArchivedSprint().getId();
        issueId = sprintissue.getIssue().getId();
        issueName = sprintissue.getIssue().getTitle();
        issueDescription = sprintissue.getIssue().getDescription();
        issueStatus = sprintissue.getIssue().getStatus();
        complete = sprintissue.isComplete();

        //complete = false;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SprintIssueHistoryDto that = (SprintIssueHistoryDto) object;
        return complete == that.complete && Objects.equals(id, that.id) && Objects.equals(sprintId, that.sprintId) && Objects.equals(issueId, that.issueId) && Objects.equals(issueName, that.issueName) && Objects.equals(issueDescription, that.issueDescription) && issueStatus == that.issueStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sprintId, issueId, issueName, issueDescription, issueStatus, complete);
    }
}
