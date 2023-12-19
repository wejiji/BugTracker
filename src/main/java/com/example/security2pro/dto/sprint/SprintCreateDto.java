package com.example.security2pro.dto.sprint;

import com.example.security2pro.dto.issue.CreateDtoWithProjectId;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SprintCreateDto implements CreateDtoWithProjectId {

    @JsonProperty("projectId")
    @NotNull
    private Long projectId;

    @JsonProperty("name")
    @NotBlank
    private String name;
    @JsonProperty("description")
    private String description;

    @JsonProperty("startDate")
    @NotNull
    private LocalDateTime startDate;
    @JsonProperty("endDate")
    @NotNull
    private LocalDateTime endDate;


    public SprintCreateDto() {}

    @JsonCreator
    public SprintCreateDto(@JsonProperty("projectId") Long projectId, @JsonProperty("name")String name, @JsonProperty("description")String description, @JsonProperty("startDate")LocalDateTime startDate, @JsonProperty("endDate") LocalDateTime endDate) {
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    public Optional<Long> getProjectId() {
        return Optional.of(projectId);
    }

    @Override
    public String toString() {
        return "ActiveSprintCreateDto{" +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}