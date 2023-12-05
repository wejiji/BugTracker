package com.example.security2pro.dto;

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
public class IssueRelationDto {
    @JsonProperty("id")
    private Long id;
//    @JsonProperty("affectedIssueId")
//    @NotNull
//    private Long affectedIssueId;
    @JsonProperty("causeIssueId")
    @NotNull
    private Long causeIssueId;
    @JsonProperty("description")
    @NotBlank
    private String relationDescription;

    @JsonCreator
    public IssueRelationDto( @JsonProperty("id")Long id,  @JsonProperty("causeIssueId")Long causeIssueId,   @JsonProperty("description")String relationDescription) {
        this.id = id;
        this.causeIssueId = causeIssueId;
        this.relationDescription = relationDescription;
    }


    public IssueRelationDto(IssueRelation issueRelation){
        id=issueRelation.getId();
        //affectedIssueId = issueRelation.getAffectedIssue().getId();
        causeIssueId = issueRelation.getCauseIssue().getId();
        relationDescription = issueRelation.getRelationDescription();
    }


    @Override
    public String toString() {
        return "IssueRelationDto{" +
                "id=" + id +
                //", affectedIssueId=" + affectedIssueId +
                ", causeIssueId=" + causeIssueId +
                ", relationDescription='" + relationDescription + '\'' +
                '}';
    }
}
