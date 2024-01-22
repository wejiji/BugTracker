package com.example.security2pro.dto.issue.onetomany;

import com.example.security2pro.domain.model.issue.IssueRelation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueRelationDto {

    @JsonProperty("causeIssueId")
    @NotNull
    private final Long causeIssueId;
    @JsonProperty("description")
    @NotBlank
    private final String relationDescription;

    @JsonCreator
    public IssueRelationDto( @JsonProperty("causeIssueId")Long causeIssueId,   @JsonProperty("description")String relationDescription) {
        this.causeIssueId = causeIssueId;
        this.relationDescription = relationDescription;
    }


    public IssueRelationDto(IssueRelation issueRelation){
        causeIssueId = issueRelation.getCauseIssue().getId();
        relationDescription = issueRelation.getRelationDescription();
    }


}
