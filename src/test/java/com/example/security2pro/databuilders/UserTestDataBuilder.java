package com.example.security2pro.databuilders;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserTestDataBuilder {
    private String username ="jj";

    private String password="jj123";

    private String firstName="ji";

    private String lastName="jang";

    private String email="jj@gmail.com";

    private Set<Role> authorities= new HashSet<>(List.of(Role.ROLE_TEAM_MEMBER));

    private boolean enabled = true;

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

    public UserTestDataBuilder withEmail(String Email){
        this.email = email;
        return this;
    }

    public UserTestDataBuilder withAuthorities(Set<Role> authorities){
        this.authorities = authorities;
        return this;
    }

    public UserTestDataBuilder withEnabled(boolean enabled){
        this.enabled = enabled;
        return this;
    }


    public User build(){
        return new User(username,password, firstName,lastName,email,enabled);
    }
}
