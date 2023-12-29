package com.example.security2pro.domain.model;


import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag=true)
public class Issue extends BaseEntity {


    @Id @Column(name="issue_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY) //프로젝트간 이슈를 옮길수도 있다. 유연성 필요해서 비식별로 해야함
    @JoinColumn(name="project_id")
    private Project project;


    @ManyToMany // 다대다 unidirectional.  이슈가 유저보다 더 많을테니 이렇게 했음.
    @JoinTable(name="issue_user"
            , joinColumns = @JoinColumn(name="issue_id"),
            inverseJoinColumns = @JoinColumn(name="username",referencedColumnName = "username"))
    private Set<User> assignees = new HashSet<>();


    private String title;

    @Lob
    private String description;

//    @Temporal(value = TemporalType.TIMESTAMP)
//    @Column(name= "complete_date")
//    private LocalDateTime completeDate;
//    // 끝나기전엔 dueDate
//    // 끝난 이후에는 endDate 로 쓰는것이 맞을듯


    @Enumerated(EnumType.STRING)
    private IssuePriority priority;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    @Enumerated(EnumType.STRING)
    private IssueType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private Sprint currentSprint;


    public Issue( Project project, Set<User> assignees, String title, String description,IssuePriority priority, IssueStatus status, IssueType type, Sprint sprint) {
        this.project = project;
        this.assignees.clear();
        this.assignees.addAll(assignees);
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.type = type;
        this.currentSprint = sprint;
        archived = false;
    }

    public Issue(Long id, Project project, Set<User> assignees, String title, String description, IssuePriority priority, IssueStatus status, IssueType type, Sprint sprint) {
        this(project,assignees,title,description,priority,status,type,sprint);
        this.id = id;
        archived = false;
    }

    public Issue(Long id, Project project, Set<User> assignees, String title, String description, IssuePriority priority, IssueStatus status, IssueType type, Sprint sprint, boolean archived) {
        this(project,assignees,title,description,priority,status,type,sprint);
        this.id = id;
        archived = archived;
    }

    public static Issue createIssue(Project project, Set<User> assignees, String title, String description, IssuePriority priority, IssueStatus status, IssueType type, Sprint sprint){
        if(assignees==null){
            assignees= new HashSet<>();
        }
        return new Issue(project,assignees, title, description, priority, status, type, sprint);
    }


    public void endIssueWithProject(){
        archived = true;
    }

    public void assignCurrentSprint(Sprint sprint){
        currentSprint = sprint;
    }

    public void forceCompleteIssue(){
        status = IssueStatus.DONE;
        currentSprint = null;
        archived = true;
    }

    public void changeStatus(IssueStatus newStatus){
        this.status = newStatus;
    }

    public void simpleUpdate(String title, IssuePriority priority, IssueStatus issueStatus, Sprint currentSprint){
        this.title = title;
        this.priority = priority;
        this.status = issueStatus;
        this.currentSprint = currentSprint;
    }

    public Issue detailUpdate(String title, String description,IssuePriority priority, IssueStatus status, IssueType type, Sprint currentSprint ,Set<User> assignees){
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.type = type;
        if(assignees==null){
            assignees= new HashSet<>();
        } else {
            this.assignees.clear();
            this.assignees.addAll(assignees);
        }
        this.currentSprint = currentSprint;
        return this;
    }


    public Set<String> getAssigneesNames(){
        //should make query to db for join fetch.??
        return assignees.stream().map(User::getUsername).collect(Collectors.toCollection(HashSet::new));
    }

    public Optional<Sprint> getCurrentSprint(){
        if(currentSprint==null){
            return Optional.empty();
        }
        return Optional.of(currentSprint);
    }

    public String getCurrentSprintIdInString(){
        if(currentSprint==null){
            return null;
        }
        return currentSprint.getId().toString();
    }


}
