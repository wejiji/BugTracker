package com.example.security2pro.domain.model;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.dto.ProjectCreationForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;


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


    public static Project createProject(ProjectCreationForm projectCreationForm){

        String name = projectCreationForm.getName();
        String description = projectCreationForm.getDescription();
        if(description==null) description="";
        return new Project(name,description);

    }

    public void endProject(){
        archived = true;
    }


}
