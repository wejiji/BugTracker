package com.example.security2pro.domain.model;

import com.example.security2pro.domain.enums.UserRole;
import com.example.security2pro.exception.directmessageconcretes.UserInvalidRoleArgumentException;
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
        // Both "username" and "id" columns are indexed for efficient retrieval.
)
@Audited
public class User {

    /*
     * no JPA bidirectional relationships for this entity.
     * All the entities that have a relationship with 'User'
     * will have a unidirectional relationship defined in their class
     * , being the owning side.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;


    private String username;

    @NotAudited
    private String password;

    @NotAudited
    private String firstName;

    @NotAudited
    private String lastName;

    @NotAudited
    private String email;

    @NotAudited
    @Getter
    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "user_authorities"
            , joinColumns = @JoinColumn(name ="username"
            , referencedColumnName = "username",nullable = false))
    @Column(name = "authorities", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> authorities= new HashSet<>();

    private boolean enabled = false;

    protected User(Long id, String username, String password, String firstName, String lastName, String email, boolean enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.enabled = enabled;
    }

    protected User(Long id, String username, String password, String firstName, String lastName, String email, Set<UserRole> authorities, boolean enabled) {
        this(id,username,password,firstName,lastName,email,enabled);

        //Below needs to be removed!!!
        if(username.equals("yj") && firstName.equals("Yeaji")){
            this.authorities=authorities;
            return;
        }
        if(authorities==null || authorities.isEmpty()){
            this.authorities.add(UserRole.ROLE_TEAM_MEMBER);
        } else {
            if(!authorities.stream().allMatch(role->role.name().startsWith("ROLE_TEAM"))){
                throw new UserInvalidRoleArgumentException(
                        "user cannot be assigned project member roles or admin role");
            }
            if(authorities.contains(UserRole.ROLE_TEAM_LEAD) && authorities.contains(UserRole.ROLE_TEAM_MEMBER)){
                throw new UserInvalidRoleArgumentException(
                        "user cannot have both team member role and team lead role");
            }
            this.authorities.addAll(authorities);
        }
    }

    public static User createUser(Long id, String username, String password, String firstName, String lastName, String email, Set<UserRole> authorities, boolean enabled ){
        /*
         * Ensure that the access modifier of 'User' constructors is set to protected
         * so that only this static factory method can be called outside this class to create 'User' objects.
         */
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

    public void adminUpdate(String password, String firstName, String lastName, String email, Set<UserRole> authorities, boolean enabled ){

        if(!authorities.stream().allMatch(role->role.name().startsWith("ROLE_TEAM"))){
            throw new UserInvalidRoleArgumentException(
                    "user cannot be assigned project member roles or admin role");
        }
        boolean argsContainsTeamLeadRole = authorities.contains(UserRole.ROLE_TEAM_LEAD);
        boolean argsContainsTeamMemberRole = authorities.contains(UserRole.ROLE_TEAM_MEMBER);

        if(argsContainsTeamMemberRole && argsContainsTeamLeadRole){
            throw new UserInvalidRoleArgumentException(
                    "user cannot have both team member role and team lead role");
        }

        if(argsContainsTeamMemberRole || argsContainsTeamLeadRole){
            this.authorities.remove(UserRole.ROLE_TEAM_MEMBER);
            this.authorities.remove(UserRole.ROLE_TEAM_LEAD);
        }
        this.authorities.addAll(authorities);

        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.enabled =  enabled;
    }




}
