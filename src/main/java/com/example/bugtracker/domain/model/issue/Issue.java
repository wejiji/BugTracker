package com.example.bugtracker.domain.model.issue;


import com.example.bugtracker.domain.enums.IssuePriority;
import com.example.bugtracker.domain.enums.IssueStatus;
import com.example.bugtracker.domain.enums.IssueType;
import com.example.bugtracker.exception.directmessageconcretes.InvalidSprintArgumentException;
import com.example.bugtracker.domain.model.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag = true)
public class Issue extends BaseEntity {

    @Id
    @Column(name = "issue_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    // A non-identifying relationship.
    // This makes things flexible in case business requirements change in the future


    @ManyToMany
    @JoinTable(name = "issue_user"
            , joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "username", referencedColumnName = "username"))
    private Set<User> assignees = new HashSet<>();
    // Many-to-many unidirectional relationship

    private String title;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private IssuePriority priority;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    @Enumerated(EnumType.STRING)
    private IssueType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private Sprint currentSprint;
    // Many-to-one unidirectional relationship
    // Note that only a non-archived sprint can be assigned to this field.

    @NotAudited
    @OneToMany(mappedBy = "affectedIssue", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<IssueRelation> issueRelationSet = new HashSet<>();
    // Parent-child bidirectional relationship
    // Life cycle of an 'IssueRelation' entirely depends on its parent 'Issue'

    @NotAudited
    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();
    // Parent-child bidirectional relationship
    // Life cycle of a 'Comment' entirely depends on its parent 'Issue'

    public void addIssueRelation(IssueRelation issueRelationArg) {
        /*
         * Jpa automatically updates 'ISSUERELATION' table in the database
         * when 'issueRelationSet' collection field is modified
         */
        Optional<IssueRelation> existingRelation = issueRelationSet.stream()
                .filter(issueRelation ->
                        issueRelation.getCauseIssue().getId().equals(issueRelationArg.getCauseIssue().getId()))
                .findAny();
        if (existingRelation.isPresent()) {
            existingRelation.get().update(issueRelationArg.getRelationDescription());
        } else {
            issueRelationArg.assignAffectedIssue(this);
            // To avoid mismatched state between entity objects and the database
            issueRelationSet.add(issueRelationArg);
        }
    }


    public void deleteIssueRelation(Long causeIssueId) {
        /*
         * Jpa automatically updates 'ISSUERELATION' table in the database
         * when 'issueRelationSet' collection field is modified
         */
        Optional<IssueRelation> existingRelation
                = issueRelationSet.stream()
                .filter(issueRelation ->
                        issueRelation.getCauseIssue().getId().equals(causeIssueId))
                .findAny();

        if (existingRelation.isPresent()) {
            existingRelation.get().assignAffectedIssue(null);
            // To avoid mismatched state between entity objects and the database
            issueRelationSet.remove(existingRelation.get());
        }

    }

    public void addComment(Comment comment) {
        /*
         * Jpa automatically updates 'COMMENT' table in the database
         * when 'commentList' collection field is modified

         * Adds a 'Comment' to the 'commentList'.
         * It is not necessary to check if a comment with the same id already exists,
         * as Comment's 'id' field is never updated
         * , and all comments are assigned autogenerated keys from the database.
         */
        comment.assignIssue(this);
        // To avoid mismatched state between entity objects and the database
        commentList.add(comment);
    }


    public void deleteComment(Long commentId) {
        /*
         * Jpa automatically updates 'COMMENT' table in the database
         * when 'commentList' collection field is modified
         *
         * Deletes a 'Comment' from the 'commentList'
         */
        Optional<Comment> existingComment
                = commentList.stream()
                .filter(comment -> (comment.getId().equals(commentId)))
                .findAny();

        if (existingComment.isPresent()) {
            existingComment.get().assignIssue(null);
            // To avoid mismatched state between entity objects and the database
            commentList.remove(existingComment.get());
        }
    }


    protected Issue(Project project
            , Set<User> assignees
            , String title
            , String description
            , IssuePriority priority
            , IssueStatus status
            , IssueType type
            , Sprint sprint) {

        this.project = project;
        this.assignees.clear();
        this.assignees.addAll(assignees);
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.type = type;
        assignCurrentSprint(sprint);
        archived = false;
    }

    protected Issue(Long id
            , Project project
            , Set<User> assignees
            , String title
            , String description
            , IssuePriority priority
            , IssueStatus status
            , IssueType type
            , Sprint sprint) {

        this(project, assignees, title, description, priority, status, type, sprint);
        this.id = id;
        archived = false;
    }

    public static Issue createIssue(Long id
            , Project project
            , Set<User> assignees
            , String title
            , String description
            , IssuePriority priority
            , IssueStatus status
            , IssueType type
            , Sprint sprint) {
        /*
         * Ensure that the access modifier of 'Issue' constructors is set to protected
         * so that only this static factory method can be called outside this class to create 'Issue' objects.
         */
        if (assignees == null) {
            assignees = new HashSet<>();
        }
        return new Issue(id, project, assignees, title, description, priority, status, type, sprint);
    }


    public void endIssueWithProject() {
        currentSprint = null;
        archived = true;
    }

    public void forceCompleteIssue() {
        status = IssueStatus.DONE;
        currentSprint = null;
        archived = true;
    }

    public void assignCurrentSprint(Sprint sprint) {
        if (sprint != null && sprint.isArchived()) {
            throw new InvalidSprintArgumentException
                    ("'currentSprint' can't be assigned an archived sprint");
        }
        currentSprint = sprint;
    }

    public void changeStatus(IssueStatus newStatus) {
        this.status = newStatus;
    }

    public Issue detailUpdate(String title
            , String description
            , IssuePriority priority
            , IssueStatus status
            , IssueType type
            , Sprint currentSprint
            , Set<User> assignees) {

        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.type = type;
        this.assignees.clear();
        this.assignees.addAll(assignees);
        assignCurrentSprint(currentSprint);
        return this;
    }

    public Set<String> getAssigneesNames() {
        return assignees.stream()
                .map(User::getUsername)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public Optional<Sprint> getCurrentSprint() {
        if (currentSprint == null) {
            return Optional.empty();
        }
        return Optional.of(currentSprint);
    }

    public String getCurrentSprintIdInString() {
        if (currentSprint == null) {
            return null;
        }
        return currentSprint.getId().toString();
    }


}