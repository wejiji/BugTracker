package com.example.security2pro.dto;

import com.example.security2pro.domain.enums.IssueStatus;

import com.example.security2pro.domain.model.SprintIssueHistory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SprintIssueHistoryDto {

    private Long id;

    private Long sprintId;

    private String issueName;

    private String issueDescription;

    private IssueStatus issueStatus;

    private boolean complete;

    public SprintIssueHistoryDto() {
    }

    public SprintIssueHistoryDto(SprintIssueHistory sprintissue){
        sprintId = sprintissue.getArchivedSprint().getId();
        issueName = sprintissue.getIssue().getTitle();
        issueDescription = sprintissue.getIssue().getDescription();
        issueStatus = sprintissue.getIssue().getStatus();
        complete = false;
    }

    @Override
    public String toString() {
        return "SprintIssueDto{" +
                "id=" + id +
                ", sprintId=" + sprintId +
                ", issueName='" + issueName + '\'' +
                ", issueDescription='" + issueDescription + '\'' +
                ", issueStatus=" + issueStatus +

                '}';
    }


    public void completeSprintIssue(){
        complete = true;
    }



}
