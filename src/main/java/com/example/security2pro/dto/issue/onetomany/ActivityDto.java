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

public class ActivityDto {
    @JsonProperty("id")
    @NotNull
    private final Long id;
    @JsonProperty("type")
    @NotNull
    private final ActivityType type;
    @JsonProperty("description")
    @NotBlank
    private final String description;

    @JsonCreator
    public ActivityDto(@JsonProperty("id")Long id, @JsonProperty("type") ActivityType type, @JsonProperty("description") String description) {
        this.id = id;
        this.type = type;
        this.description = description;
    }

    public ActivityDto(Activity activity){
        id = activity.getId();
        type =activity.getType();
        description = activity.getDescription();
    }



}
