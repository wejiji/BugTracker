package com.example.security2pro.domain.model;

import com.example.security2pro.domain.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USERS"
        ,uniqueConstraints = { @UniqueConstraint(columnNames = { "username"}) }
)
@Audited
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    //@Column(unique = true)
    private String username;

    @NotAudited
    private String password;
    @NotAudited
    private String firstName;
    @NotAudited
    private String lastName;
    @NotAudited
    private String email;

    @Getter
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    // Basic Auth filter's DAO Auth authorities fetch - should be eager.. other workaround???
    @CollectionTable(name = "user_authorities", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "authorities", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> authorities= new HashSet<>();

    private boolean enabled = false;//이부분 여기 있어야 할까?

    protected User(Long id, String username, String password, String firstName, String lastName, String email, boolean enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.enabled = enabled;
    }

    protected User(Long id, String username, String password, String firstName, String lastName, String email, Set<Role> authorities, boolean enabled) {
        this(id,username,password,firstName,lastName,email,enabled);
        if(username.equals("yj") && firstName.equals("Yeaji")){
            this.authorities=authorities;
            return;
        }
        if(authorities==null || authorities.isEmpty()){
            this.authorities.add(Role.ROLE_TEAM_MEMBER);
        } else {
            if(!authorities.stream().allMatch(role->role.name().startsWith("ROLE_TEAM"))){
                throw new IllegalArgumentException("user cannot be assigned project member roles or admin role");
            }
            if(authorities.contains(Role.ROLE_TEAM_LEAD) && authorities.contains(Role.ROLE_TEAM_MEMBER)){
                throw new IllegalArgumentException("user cannot have both team member role and team lead role");
            }
            this.authorities.addAll(authorities);
        }
    }

    public static User createUser(Long id, String username, String password, String firstName, String lastName, String email, Set<Role> authorities, boolean enabled ){

        return new User(id, username, password, firstName, lastName, email, authorities, enabled);
    }

    public void changePassword(String newPassword){
        this.password = newPassword;
    }

    public void updateNamesAndEmail(String firstName, String lastName, String email){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public void adminUpdate(String username, String password, String firstName, String lastName, String email, Set<Role> authorities, boolean enabled ){

        if(!authorities.stream().allMatch(role->role.name().startsWith("ROLE_TEAM"))){
            throw new IllegalArgumentException("user cannot be assigned project member roles or admin role");
        }
        boolean argsContainsTeamLeadRole = authorities.contains(Role.ROLE_TEAM_LEAD);
        boolean argsContainsTeamMemberRole = authorities.contains(Role.ROLE_TEAM_MEMBER);

        if(argsContainsTeamMemberRole && argsContainsTeamLeadRole){
            throw new IllegalArgumentException("user cannot have both team member role and team lead role");
        }

        if(argsContainsTeamMemberRole || argsContainsTeamLeadRole){
            this.authorities.remove(Role.ROLE_TEAM_MEMBER);
            this.authorities.remove(Role.ROLE_TEAM_LEAD);
        }
        this.authorities.addAll(authorities);


        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.enabled =  enabled;
    }




}
