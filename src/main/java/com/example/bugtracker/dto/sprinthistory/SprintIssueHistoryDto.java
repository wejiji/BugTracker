package com.example.bugtracker.dto.sprinthistory;

import com.example.bugtracker.domain.enums.IssueStatus;

import com.example.bugtracker.domain.model.SprintIssueHistory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SprintIssueHistoryDto {

    private Long id;

    private Long sprintId;

    private Long issueId;

    private String issueName;

    private String issueDescription;

    private IssueStatus issueStatus;

    private boolean complete;

    public SprintIssueHistoryDto(SprintIssueHistory sprintissue){

        id = sprintissue.getId();
        sprintId = sprintissue.getArchivedSprint().getId();
        issueId = sprintissue.getIssue().getId();
        issueName = sprintissue.getIssue().getTitle();
        issueDescription = sprintissue.getIssue().getDescription();
        issueStatus = sprintissue.getIssue().getStatus();
        complete = sprintissue.isComplete();
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
