package com.example.security2pro.dto.issue;

import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.model.Issue;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class IssueSimpleDto {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("priority")
    private IssuePriority priority;
    @JsonProperty("status")
    private IssueStatus status;

    public IssueSimpleDto() {}

    @JsonCreator
    public IssueSimpleDto( @JsonProperty("id")Long id, @JsonProperty("title")String title, @JsonProperty("priority") IssuePriority priority,    @JsonProperty("status")IssueStatus status) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status;
    }

    public IssueSimpleDto(Issue issue){
        id = issue.getId();
        title = issue.getTitle();
        priority = issue.getPriority();
        status = issue.getStatus();
    }

    @Override
    public String toString() {
        return "IssueSimpleDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                '}';
    }
}
