package com.example.security2pro.databuilders;

import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.domain.model.Project;
import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.domain.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class IssueTestDataBuilder {

    private Long id = 1L;
    private Project project;
    private String name = "issueName";
    private String description = "issueDescription";
    private IssuePriority priority = IssuePriority.HIGHEST;
    private IssueStatus status = IssueStatus.IN_REVIEW;
    private IssueType type = IssueType.BUG;

    private Set<User> assignees = new HashSet<>();

    private Sprint currentSprint;

    private boolean archived=false;

    public IssueTestDataBuilder withProject(Project project) {
        this.project = project;
        return this;
    }

    public IssueTestDataBuilder withName(String name) {
        this.name = name;
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
        return new Issue( id, project, assignees, name, description, priority, status, type, currentSprint);
    }
}