package com.example.security2pro.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sprint extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sprint_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //다대일 양방향
    @JoinColumn(name="project_id")
    private Project project;

    private String name;

    private String description;

    @Temporal(value = TemporalType.TIMESTAMP)
    private LocalDateTime startDate;

    @Temporal(value = TemporalType.TIMESTAMP)
    private LocalDateTime endDate;


    public Sprint(Project project, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.project = project;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        archived = false;
    }

    public Sprint(Long id, Project project, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this(project,name,description,startDate,endDate);
        this.id = id;
    }

    public Sprint(Long id, Sprint sprint){
        this(sprint.getProject(),sprint.getName(),sprint.getDescription(),sprint.getStartDate(),sprint.getEndDate());
        this.id = id;
    }

    public void completeSprint(){
        if(endDate.isAfter(LocalDateTime.now())){
            endDate = LocalDateTime.now();
        }
        archived = true;
    }

    public void updateFields(String name, String description, LocalDateTime startDate, LocalDateTime endDate){

        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }


}
