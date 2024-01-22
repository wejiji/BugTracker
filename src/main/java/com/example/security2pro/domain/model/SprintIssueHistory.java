package com.example.security2pro.domain.model;

import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.model.issue.Issue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


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

    protected SprintIssueHistory(Long id,Sprint sprint, Issue issue) {
        this.id = id;
        this.archivedSprint = sprint;
        this.issue = issue;
        this.complete = issue.getStatus().equals(IssueStatus.DONE);
    }

    public static SprintIssueHistory createSprintIssueHistory(Long id,Sprint sprint, Issue issue){

        /*
         * Tests if an IllegalArgumentException is thrown
         * when attempting to create a SprintIssueHistory object
         * with an Issue argument having a non-reset 'currentSprint' field,
         * still having the just-archived sprint
         * that will be passed along with the Issue argument to 'createSprintIssueHistory' method
         */

        if(!sprint.isArchived()){
            throw new IllegalArgumentException("sprint is not archived");
        }
        if(issue.getCurrentSprint().isPresent() && issue.getCurrentSprint().get().getId().equals(sprint.getId())){
            throw new IllegalArgumentException("issue's current sprint field hasn't been reset");
        }

        return new SprintIssueHistory(id,sprint,issue);
    }



}
