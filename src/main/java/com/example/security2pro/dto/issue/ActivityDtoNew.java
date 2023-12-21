package com.example.security2pro.dto.issue;

import com.example.security2pro.domain.enums.ActivityType;
import com.example.security2pro.domain.model.Activity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ActivityDtoNew implements DtoWithIssueId{

    @JsonProperty("id")
    @NotNull
    private Long id;

    @JsonProperty("issueId")
    @NotNull
    private Long issueId;
    @JsonProperty("type")
    @NotNull
    private ActivityType type;
    @JsonProperty("description")
    @NotBlank
    private String description;

    private LocalDateTime createdAt;
    private String createdBy; //username


    public ActivityDtoNew() {
    }

    @JsonCreator
    public ActivityDtoNew(@JsonProperty("id") Long id,@JsonProperty("issueId")Long issueId, @JsonProperty("type") ActivityType type, @JsonProperty("description") String description) {
        this.id = id;
        this.issueId = issueId;
        this.type = type;
        this.description = description;
    }

    public ActivityDtoNew(Activity activity){
        id = activity.getId();
        issueId = activity.getIssue().getId();
        type =activity.getType();
        description = activity.getDescription();
    }


    @Override
    public Long issueIdForAuthorization() {
        return issueId;
    }
}
