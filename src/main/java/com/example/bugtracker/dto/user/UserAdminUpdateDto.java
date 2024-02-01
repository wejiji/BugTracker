package com.example.bugtracker.dto.user;

import com.example.bugtracker.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserAdminUpdateDto {

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    @NotBlank
    @Size(min = 1, max = 30)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 30)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 70)
    private String email;

    @NotNull
    private Set<UserRole> roles;

    boolean enabled;

    public UserAdminUpdateDto(String password
            , String firstName
            , String lastName
            , String email
            , boolean enabled) {

        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.enabled = enabled;
    }

    public UserAdminUpdateDto(String password
            , String firstName
            , String lastName
            , String email
            , Set<UserRole> roles
            , boolean enabled) {

        this(password, firstName, lastName, email, enabled);
        this.roles = roles;
    }

}
