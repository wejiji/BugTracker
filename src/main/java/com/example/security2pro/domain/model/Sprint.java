package com.example.security2pro.domain.model;

import com.example.security2pro.exception.directmessageconcretes.InvalidSprintDateException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sprint extends BaseEntity {
    /*
     * no JPA bidirectional relationships for this entity.
     * All the entities that have many-to-one relationship with 'Sprint'
     * will have a JPA unidirectional relationship defined in their class
     * , being the owning side.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sprint_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // JPA unidirectional relationship
    @JoinColumn(name = "project_id")
    private Project project;

    private String name;

    private String description;

    @Temporal(value = TemporalType.TIMESTAMP)
    private LocalDateTime startDate;

    @Temporal(value = TemporalType.TIMESTAMP)
    private LocalDateTime endDate;

    public static Sprint createSprint(Long id, Project project, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        /*
         * Ensure that the access modifier of 'Sprint' constructors is set to protected
         * so that only this static factory method can be called outside this class to create 'Sprint' objects.
         */

        if (startDate.isAfter(endDate)) {
            throw new InvalidSprintDateException("start date cannot be after end date");
        }

        return new Sprint(id, project, name, description, startDate, endDate);
    }

    public static Sprint createDefaultSprint(Project project, LocalDateTime startDate) {
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
        this(project, name, description, startDate, endDate);
        this.id = id;
    }

    protected Sprint(Long id, Project project, String name, String description, LocalDateTime startDate, LocalDateTime endDate, boolean archived) {
        this(project, name, description, startDate, endDate);
        this.id = id;
        this.archived = archived;
    }

    protected Sprint(Long id, Sprint sprint) {
        this(sprint.getProject(), sprint.getName(), sprint.getDescription(), sprint.getStartDate(), sprint.getEndDate());
        this.id = id;
    }

    public void completeSprint(LocalDateTime now) {
        if (endDate.isAfter(now)) {
            if (startDate.isAfter(now)) {
                endDate = startDate;
            } else {
                endDate = now;
            }
        }
        archived = true;
    }

    public Sprint update(String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidSprintDateException("start date cannot be after end date");
        }
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        return this;
    }


}
