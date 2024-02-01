package com.example.bugtracker.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {
    /*
    * no JPA bidirectional relationships for this entity.
    * All the entities that have many-to-one relationship with 'Project'
    * will have a JPA unidirectional relationship defined in their class
    * , being the owning side.
    */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="project_id")
    private Long id;
    private String name;
    private String description;

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
        /*
         * Ensure that the access modifier of 'Project' constructors is set to protected
         * so that only this static factory method can be called outside this class to create 'Project' objects.
         */
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
