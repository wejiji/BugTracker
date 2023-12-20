package com.example.security2pro.domain.model;

import com.example.security2pro.dto.project.ProjectCreateDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="project_id")
    private Long id;

    private String name;

    private String description;

    public Project(String name, String description){
        this.name = name ;
        this.description = description;
        archived = false;
    }


    public static Project createProject(ProjectCreateDto projectCreateDto){

        String name = projectCreateDto.getName();
        String description = projectCreateDto.getDescription();
        if(description==null) description="";
        return new Project(name,description);

    }

    public void endProject(){
        archived = true;
    }

    public void updateProject(String name, String description){
        this.name= name;
        this.description = description;
    }


}
