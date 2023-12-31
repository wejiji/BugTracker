package com.example.security2pro.dto.issue.onetomany;

import com.example.security2pro.domain.enums.ActivityType;
import com.example.security2pro.domain.model.Activity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityCreateDto {

    @JsonProperty("issueId")
    @NotNull
    private final Long issueId;
    @JsonProperty("type")
    @NotNull
    private final ActivityType type;
    @JsonProperty("description")
    @NotBlank
    private final String description;


    @JsonCreator
    public ActivityCreateDto(@JsonProperty("issueId")Long issueId, @JsonProperty("type") ActivityType type, @JsonProperty("description") String description) {
        this.issueId = issueId;
        this.type = type;
        this.description = description;
    }

    public ActivityCreateDto(Activity activity){
        issueId = activity.getIssue().getId();
        type =activity.getType();
        description = activity.getDescription();
    }

}
