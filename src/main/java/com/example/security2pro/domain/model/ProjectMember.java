package com.example.security2pro.domain.model;

import com.example.security2pro.domain.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="project_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="username", referencedColumnName = "username")
    private User user;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "project_member_authorities", joinColumns = @JoinColumn(name = "project_member_id"))
    @Column(name = "authorities", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> authorities = new HashSet<>();


    public ProjectMember(Project project, User user,Set<Role> authorities){
        this.project = project;
        this.user = user;
        this.authorities = authorities;
    }


    public static ProjectMember createProjectMember(Project project, User user, Set<Role> authorities){

        if(authorities==null) {
            return new ProjectMember(project, user, Set.of(Role.ROLE_PROJECT_MEMBER));
        } else {
            return new ProjectMember(project, user, authorities);
        }
    }


    public Set<Role> updateRole(Set<Role> roleSet){
        if(roleSet.stream().allMatch(role -> role.name().startsWith("ROLE_PROJECT_"))){
            this.authorities.clear();
            this.authorities.addAll(roleSet);
            System.out.println(roleSet);
            System.out.println(authorities);
            return authorities;
        }
        return new HashSet<>();
    }

//    public Role updateRole(Role roleSet){
//        if(!roleSet.name().startsWith("ROLE_PROJECT_")){
//            return null;
//        }
//        return roleSet;
//    }



}
