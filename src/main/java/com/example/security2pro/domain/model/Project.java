package com.example.security2pro.domain.model;

import com.example.security2pro.dto.project.ProjectCreateDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="project_id")
    private Long id;

    private String name;

    private String description;

    protected Project(){}

    protected Project(Long id, String name, String description){
        this(name,description);
        this.id = id;
    }

    protected Project(String name, String description){
        this.name = name ;
        this.description = description;
        archived = false;
    }

    public static Project createProject(Long id, String name, String description){
        if(description==null) description="";
        return new Project(id,name,description);

    }

    public void endProject(){
        archived = true;
    }

    public void updateProject(String name, String description){
        this.name= name;
        if(description==null){
            this.description = "";
        } else {
            this.description = description;
        }
    }


}
