package com.example.bugtracker.databuilders;

import com.example.bugtracker.domain.enums.IssuePriority;
import com.example.bugtracker.domain.enums.IssueStatus;
import com.example.bugtracker.domain.enums.IssueType;
import com.example.bugtracker.domain.model.issue.Issue;
import com.example.bugtracker.domain.model.Project;
import com.example.bugtracker.domain.model.Sprint;
import com.example.bugtracker.domain.model.User;

import java.util.HashSet;
import java.util.Set;

public class IssueTestDataBuilder {

    private Long id = 1L;
    private Project project;
    private String title = "issueTitle";
    private String description = "issueDescription";
    private IssuePriority priority = IssuePriority.HIGHEST;
    private IssueStatus status = IssueStatus.IN_REVIEW;
    private IssueType type = IssueType.BUG;

    private boolean archived = false;

    private Set<User> assignees = new HashSet<>();

    private Sprint currentSprint;

    public IssueTestDataBuilder withProject(Project project) {
        this.project = project;
        return this;
    }

    public IssueTestDataBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public IssueTestDataBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public IssueTestDataBuilder withPriority(IssuePriority priority) {
        this.priority = priority;
        return this;
    }

    public IssueTestDataBuilder withStatus(IssueStatus status) {
        this.status = status;
        return this;
    }

    public IssueTestDataBuilder withType(IssueType type) {
        this.type = type;
        return this;
    }

    public IssueTestDataBuilder withSprint(Sprint currentSprint) {
        this.currentSprint = currentSprint;
        return this;
    }


    public IssueTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public IssueTestDataBuilder withAssignees(Set<User> assignees) {
        this.assignees = assignees;
        return this;
    }

    public IssueTestDataBuilder withArchived(boolean archived){
        this.archived = archived;
        return this;
    }


    public Issue build() {
        Issue issue =  Issue.createIssue( id, project, assignees, title, description, priority, status, type, currentSprint);
        if(archived){
            issue.endIssueWithProject();
        }
        return issue;
    }
}