package com.example.security2pro.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SprintIssueHistory {

    @Id
    @Column(name="sprint_issue_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sprint_id")
    private Sprint archivedSprint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="issue_id")
    private Issue issue;

    private boolean complete;

    public SprintIssueHistory(Sprint sprint, Issue issue) {
        this.archivedSprint = sprint;
        this.issue = issue;
        this.complete = issue.archived;
        // if the issue is forced to be complete before its due date,
        // it has to happen before this constructor
    }

    public void forceCompleteSprintIssue(){
        issue.forceCompleteIssue();
        complete = true;
    }




    @Override
    public String toString() {
        return "SprintIssueHistory{" +
                "id=" + id +
                ", archivedSprint=" + archivedSprint +
                ", issue=" + issue +
                ", complete=" + complete +
                '}';
    }
}
