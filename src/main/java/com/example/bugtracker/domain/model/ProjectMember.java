package com.example.bugtracker.domain.model;

import com.example.bugtracker.domain.enums.ProjectMemberRole;
import com.example.bugtracker.exception.directmessageconcretes.ProjectMemberInvalidRoleArgumentException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMember {

    /* Resources are accessible only to users who are members of the project
     * to which the resource belongs.
     * The 'authorities' field values of this class are used for authorization of project resources.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ElementCollection(targetClass = ProjectMemberRole.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "project_member_authorities", joinColumns = @JoinColumn(name = "project_member_id"))
    @Column(name = "authorities", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<ProjectMemberRole> authorities = new HashSet<>();

    protected ProjectMember(
            Long id
            , Project project
            , User user
            , Set<ProjectMemberRole> authorities) {

        this.id = id;
        this.project = project;
        this.user = user;
        this.authorities.addAll(authorities);
    }


    public static ProjectMember createProjectMember(
            Long id
            , Project project
            , User user
            , Set<ProjectMemberRole> authorities) {
        /*
         * Ensure that the access modifier of 'ProjectMember' constructors is set to protected
         * so that only this static factory method can be called outside this class to create 'ProjectMember' objects.
         */

        if (authorities == null || authorities.isEmpty()) {
            return new ProjectMember(id, project, user, Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER));
        }
        if (!authorities.stream()
                .allMatch(role -> role.name().startsWith("ROLE_PROJECT_"))) {
            throw new ProjectMemberInvalidRoleArgumentException("invalid role");
        }
        if (authorities.containsAll(
                Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER, ProjectMemberRole.ROLE_PROJECT_LEAD))) {
            throw new ProjectMemberInvalidRoleArgumentException(
                    "user cannot have both project member role as well as lead role");
        }

        return new ProjectMember(id, project, user, authorities);

    }


    public void updateRole(Set<ProjectMemberRole> authorities) {
        if (authorities.isEmpty()) {
            throw new ProjectMemberInvalidRoleArgumentException("no roles were passed");
        }
        if (!authorities.stream().allMatch(auth -> auth.name().startsWith("ROLE_PROJECT_"))) {
            throw new ProjectMemberInvalidRoleArgumentException("invalid role");
        }
        if (authorities.containsAll(
                Set.of(ProjectMemberRole.ROLE_PROJECT_MEMBER, ProjectMemberRole.ROLE_PROJECT_LEAD))) {
            throw new ProjectMemberInvalidRoleArgumentException(
                    "user cannot have both project member role as well as lead role");
        }
        this.authorities.clear();
        this.authorities.addAll(authorities);

    }


}
