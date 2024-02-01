package com.example.bugtracker.databuilders;


import com.example.bugtracker.domain.model.Project;

public class ProjectTestDataBuilder{
    private Long id = 1L;
    private String name = "Test Project";
    private String description = "Project description";

    private boolean archived = false;

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

    public ProjectTestDataBuilder withArchived(boolean archived) {
        this.archived = archived;
        return this;
    }

    public Project build() {
        Project project = Project.createProject(id,name,description);
        if(archived){
            project.endProject();
        }
        return project;
    }
}