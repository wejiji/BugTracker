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


    public static Sprint createSprint(Long id, Project project, String name, String description, LocalDateTime startDate, LocalDateTime endDate){
        if(startDate.isAfter(endDate)){
            throw new IllegalArgumentException("start date cannot be after end date");
        }

        return new Sprint(id,project,name,description,startDate,endDate);
    }

    public static Sprint createDefaultSprint(Project project, LocalDateTime startDate){
        return new Sprint(null, project, "untitled", "", startDate, startDate.plusDays(14));
    }

    protected Sprint(Project project, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.project = project;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        archived = false;
    }

    protected Sprint(Long id, Project project, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this(project,name,description,startDate,endDate);
        this.id = id;
    }


    protected Sprint(Long id, Project project, String name, String description, LocalDateTime startDate, LocalDateTime endDate, boolean archived) {
        this(project,name,description,startDate,endDate);
        this.id = id;
        this.archived = archived;
    }

    protected Sprint(Long id, Sprint sprint){
        this(sprint.getProject(),sprint.getName(),sprint.getDescription(),sprint.getStartDate(),sprint.getEndDate());
        this.id = id;
    }


    public void completeSprint(LocalDateTime now){
        // needs to be careful with start date
        if(endDate.isAfter(now)){
            endDate = now;
        }
        archived = true;
    }

    public Sprint updateFields(String name, String description, LocalDateTime startDate, LocalDateTime endDate){
        if(startDate.isAfter(endDate)){
            throw new IllegalArgumentException("start date cannot be after end date");
        }
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        return this;
    }




}
