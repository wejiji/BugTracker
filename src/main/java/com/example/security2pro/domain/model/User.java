package com.example.security2pro.domain.model;

import com.example.security2pro.domain.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.HashSet;
import java.util.Set;

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
        this.authorities = authorities;
    }

    public static User createUser(Long id, String username, String password, String firstName, String lastName, String email, Set<Role> authorities, boolean enabled ){
        if(authorities==null || authorities.isEmpty()){
            authorities = Set.of(Role.valueOf("ROLE_TEAM_MEMBER"));
        } else {
            if(!authorities.stream().allMatch(role->role.name().startsWith("ROLE_TEAM"))){
                throw new IllegalArgumentException("user cannot be assigned project member roles");
            }
        }
        return new User(id, username, password, firstName, lastName, email, authorities, enabled);
    }

    public void changePassword(String newPassword){
        this.password = newPassword;
    }
    public void giveRole(Set<Role> roles){
        this.authorities = roles;
    }
    public void updateNamesAndEmail(String firstName, String lastName, String email){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public void adminUpdate(String username, String password, String firstName, String lastName, String email, Set<Role> authorities, boolean enabled ){

        if(authorities==null || authorities.isEmpty()){
            authorities = Set.of(Role.valueOf("ROLE_TEAM_MEMBER"));
        } else {
            if(!authorities.stream().allMatch(role->role.name().startsWith("ROLE_TEAM"))){
                throw new IllegalArgumentException("user cannot be assigned project member roles");
            }
            this.authorities = authorities;
        }

        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.enabled =  enabled;
    }


}
