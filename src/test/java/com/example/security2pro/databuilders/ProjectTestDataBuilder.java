package com.example.security2pro.databuilders;

import com.example.security2pro.domain.model.Project;


public class ProjectTestDataBuilder{
    private Long id = 1L;
    private String name = "Test Project";
    private String description = "Project description";

    public ProjectTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }


    public ProjectTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProjectTestDataBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    // Add similar methods for other fields

    public Project build() {
        return new Project(id, name, description);
    }
}