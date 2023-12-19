package com.example.security2pro.dto.issue;

import com.example.security2pro.domain.model.IssueRelation;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class IssueRelationCreateDto {

    @JsonProperty("causeIssueId")
    @NotNull
    private Long causeIssueId;
    @JsonProperty("description")
    @NotBlank
    private String relationDescription;

    @JsonCreator
    public IssueRelationCreateDto(@JsonProperty("causeIssueId")Long causeIssueId, @JsonProperty("description")String relationDescription) {
        this.causeIssueId = causeIssueId;
        this.relationDescription = relationDescription;
    }

    public IssueRelationCreateDto(IssueRelation issueRelation){
        causeIssueId = issueRelation.getCauseIssue().getId();
        relationDescription = issueRelation.getRelationDescription();
    }

}
