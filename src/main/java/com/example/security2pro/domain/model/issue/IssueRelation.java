package com.example.security2pro.domain.model.issue;

import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.exception.directmessageconcretes.InvalidIssueRelationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueRelation {

    // As a child entity of 'Issue', its lifecycle depends entirely on its parent 'Issue'.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_relation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affected_issue_id", referencedColumnName = "issue_id", updatable = false)
    private Issue affectedIssue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cause_issue_id", referencedColumnName = "issue_id", updatable = false)
    private Issue causeIssue;

    private String relationDescription;

    protected IssueRelation(
            Issue affectedIssue
            , Issue causeIssue
            , String relationDescription) {

        this.affectedIssue = affectedIssue;
        this.causeIssue = causeIssue;
        this.relationDescription = relationDescription;
    }

    public static IssueRelation createIssueRelation(
            Issue affectedIssue
            , Issue causeIssue
            , String relationDescription) {
        /*
         * Ensure that the access modifier of 'IssueRelation' constructors is set to protected
         * so that only this static factory method can be called outside this class to create 'IssueRelation' objects.
         */
        if (affectedIssue.getId().equals(causeIssue.getId())
            || causeIssue.getStatus().equals(IssueStatus.DONE)) {
            throw new InvalidIssueRelationException(
                    "invalid issue relation. " +
                    "cause issue cannot be the same as the affected issue. " +
                    "cause Issue with 'DONE' state cannot be newly added as a cause issue. ");
        }
        return new IssueRelation(affectedIssue, causeIssue, relationDescription);
    }

    public IssueRelation update(String relationDescription) {
        this.relationDescription = relationDescription;
        return this;
    }

    public void assignAffectedIssue(Issue affectedIssue) {
        this.affectedIssue = affectedIssue;
    }


}
