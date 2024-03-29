package com.example.bugtracker.domain.model;

import com.example.bugtracker.domain.enums.IssueStatus;
import com.example.bugtracker.exception.directmessageconcretes.InvalidIssueArgumentException;
import com.example.bugtracker.exception.directmessageconcretes.InvalidSprintArgumentException;
import com.example.bugtracker.domain.model.issue.Issue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SprintIssueHistory {
    // Keeps a record of whether an issue has been completed within the duration of the archived sprint.

    @Id
    @Column(name="sprint_issue_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sprint_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Sprint archivedSprint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="issue_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Issue issue;

    private boolean complete;

    protected SprintIssueHistory(Long id,Sprint sprint, Issue issue) {
        this.id = id;
        this.archivedSprint = sprint;
        this.issue = issue;
        this.complete = issue.getStatus().equals(IssueStatus.DONE);
    }

    /**
     * Creates a 'SprintIssueHistory' instance with the provided parameter.
     *
     *  Ensure that the access modifier of 'SprintIssueHistory' constructors is set to protected
     * so that only this static factory method can be
     * called outside this class to create 'SprintIssueHistory' objects.
     *
     * @param id    The id of the 'SprintIssueHistory' to be created.
     * @param sprint The archived 'Sprint' related to the history entry.
     * @param issue  The 'Issue' involved in the history entry.
     *               An exception will be thrown if the 'Issue' still
     *               has its current sprint field set to the provided 'Sprint'.
     * @return A new 'SprintIssueHistory' instance.
     */
    public static SprintIssueHistory createSprintIssueHistory(Long id,Sprint sprint, Issue issue){

        if(!sprint.isArchived()){
            throw new InvalidSprintArgumentException(
                    "sprint is not archived");
        }
        if(issue.getCurrentSprint().isPresent()
           && issue.getCurrentSprint().get().getId().equals(sprint.getId())){
            throw new InvalidIssueArgumentException(
                    "issue's current sprint field hasn't been reset");
        }

        return new SprintIssueHistory(id,sprint,issue);
    }



}
