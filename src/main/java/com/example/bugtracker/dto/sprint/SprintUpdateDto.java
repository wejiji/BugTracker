package com.example.bugtracker.dto.sprint;

import com.example.bugtracker.domain.model.Sprint;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SprintUpdateDto {

    @JsonProperty("id")
    @NotNull
    private Long id;

    @JsonProperty("name")
    @NotBlank
    private String name;

    @JsonProperty("description")
    @NotNull
    private String description;

    @JsonProperty("startDate")
    @NotNull
    private LocalDateTime startDate;

    @JsonProperty("endDate")
    @NotNull
    private LocalDateTime endDate;

    @JsonCreator
    public SprintUpdateDto(@JsonProperty("id") Long id
            , @JsonProperty("name") String name
            , @JsonProperty("description") String description
            , @JsonProperty("startDate") LocalDateTime startDate
            , @JsonProperty("endDate") LocalDateTime endDate) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    public SprintUpdateDto(Sprint sprint) {

        id = sprint.getId();
        name = sprint.getName();
        description = sprint.getDescription();
        startDate = sprint.getStartDate();
        endDate = sprint.getEndDate();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SprintUpdateDto that = (SprintUpdateDto) object;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, startDate, endDate);
    }

}
