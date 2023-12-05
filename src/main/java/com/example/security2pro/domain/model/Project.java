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


    //일대일 단방향
//    @OneToOne(fetch = FetchType.LAZY)
//    private User projectLead;
    //=================================================
    //유저로 가는것이 필요한가?..조회나 객체그래프탐색만 가능하다는게 무슨뜻인지 와닿지 않는다..
    //양방향 매핑들.. 다 프로젝트가 주인아님
//    @ManyToMany(mappedBy = "projects")
//    private Set<User> users;
    //COMMENTED - to make it unidirectional


//    //NOT SURE IF THE BELOW IS NECESSARY
//    //SEARCH FROM ISSUE TABLE SHOULD BE ENOUGH
//    // ex> select * from issue table where project_id = ?
//    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
//    private Set<Issue> issueList = new HashSet<>();
    //SEARCH FROM ACTIVITY TABLE SHOULD BE ENOUGH
//    // select * from activities table where project_id = ?
//    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
//    private Set<Activity> activities = new HashSet<>();



}
