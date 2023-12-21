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
public class IssueRelationDto implements DtoWithIssueId{
    @JsonProperty("affectedIssueId")
    @NotNull
    private Long affectedIssueId;
    @JsonProperty("causeIssueId")
    @NotNull
    private Long causeIssueId;
    @JsonProperty("description")
    @NotBlank
    private String relationDescription;

    @JsonCreator
    public IssueRelationDto( @JsonProperty("affectedIssueId")Long affectedIssueId,  @JsonProperty("causeIssueId")Long causeIssueId,   @JsonProperty("description")String relationDescription) {
        this.affectedIssueId = affectedIssueId;
        this.causeIssueId = causeIssueId;
        this.relationDescription = relationDescription;
    }


    public IssueRelationDto(IssueRelation issueRelation){
        affectedIssueId =issueRelation.getAffectedIssue().getId();
        causeIssueId = issueRelation.getCauseIssue().getId();
        relationDescription = issueRelation.getRelationDescription();
    }


    @Override
    public Long issueIdForAuthorization() {
        return affectedIssueId;
    }
}
