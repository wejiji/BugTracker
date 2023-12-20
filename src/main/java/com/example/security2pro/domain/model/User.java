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


    public User(String username, String password, String firstName, String lastName, String email, boolean enabled) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.enabled = enabled;
        authorities = Set.of(Role.valueOf("ROLE_TEAM_MEMBER"));
    }

    public User(String username, String password, String firstName, String lastName, String email, Set<Role> authorities, boolean enabled) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.authorities = authorities;
        this.enabled = enabled;
    }

    public void changePassword(String newPassword){
        this.password = newPassword;
    }
    public void giveRole(Set<Role> roles){
        this.authorities = roles;
    }

}
