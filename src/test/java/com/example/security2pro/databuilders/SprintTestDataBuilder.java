package com.example.security2pro.databuilders;

import com.example.security2pro.domain.model.Project;
import com.example.security2pro.domain.model.Sprint;

import java.time.LocalDateTime;

public class SprintTestDataBuilder {

    private Long id = 18L;
    private Project project;
    private String name = "sprintname";
    private String description = "sprintdescription";
    private LocalDateTime startDate = LocalDateTime.now().minusDays(1);
    private LocalDateTime endDate = startDate.plusDays(14);

    private boolean archived =false;


    public SprintTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public SprintTestDataBuilder withProject(Project project) {
        this.project = project;
        return this;
    }

    public SprintTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SprintTestDataBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public SprintTestDataBuilder withStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public SprintTestDataBuilder withEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public SprintTestDataBuilder withArchived(boolean archived){
        this.archived = archived;
        return this;
    }

    public Sprint build() {
        Sprint sprint =  Sprint.createSprint(id,project, name, description, startDate, endDate);
        if(archived){
            sprint.completeSprint(endDate);
        }
        return sprint;
    }


}