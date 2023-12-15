package com.example.security2pro.dto.sprint;

import com.example.security2pro.domain.model.Sprint;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ActiveSprintUpdateDto {

    @JsonProperty("id")
    @NotNull
    private Long id;
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


    public ActiveSprintUpdateDto() {}

    @JsonCreator
    public ActiveSprintUpdateDto(@JsonProperty("id") Long id, @JsonProperty("name")String name, @JsonProperty("description")String description, @JsonProperty("startDate")LocalDateTime startDate, @JsonProperty("endDate") LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    public ActiveSprintUpdateDto(Sprint sprint){
        id = sprint.getId();
        name = sprint.getName();
        startDate = sprint.getStartDate();
        endDate = sprint.getEndDate();
    }

    @Override
    public String toString() {
        return "ActiveSprintUpdateDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
