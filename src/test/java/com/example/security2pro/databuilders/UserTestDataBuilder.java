package com.example.security2pro.databuilders;

import com.example.security2pro.domain.enums.UserRole;
import com.example.security2pro.domain.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserTestDataBuilder {

    private Long id = 1L;

    private String username ="testUsername";

    private String password="testUserPassword";

    private String firstName="testFirstName";

    private String lastName="testLastName";

    private String email="testUser@gmail.com";

    private Set<UserRole> authorities= new HashSet<>(List.of(UserRole.ROLE_TEAM_LEAD));

    private boolean enabled = true;

    public UserTestDataBuilder withId(Long id){
        this.id = id;
        return this;
    }

    public UserTestDataBuilder withUsername(String username){
        this.username = username;
        return this;
    }

    public UserTestDataBuilder withPassword(String password){
        this.password = password;
        return this;
    }

    public UserTestDataBuilder withFirstName(String firstName){
        this.firstName =firstName;
        return this;
    }

    public UserTestDataBuilder withLastName(String lastName){
        this.lastName=lastName;
        return this;
    }

    public UserTestDataBuilder withEmail(String email){
        this.email = email;
        return this;
    }

    public UserTestDataBuilder withAuthorities(Set<UserRole> authorities){
        this.authorities = authorities;
        return this;
    }

    public UserTestDataBuilder withEnabled(boolean enabled){
        this.enabled = enabled;
        return this;
    }


    public User build(){
        return User.createUser(id, username,password, firstName,lastName,email, authorities, enabled);
    }
}
