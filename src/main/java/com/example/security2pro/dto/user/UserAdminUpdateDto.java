package com.example.security2pro.dto.user;

import com.example.security2pro.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter //Dto이므로 열었음
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAdminUpdateDto {

    @NotBlank
    @Size(max =15)
    private String username;
    @NotBlank
    @Size(min=8, max=20)
    private String password;
    @NotBlank
    @Size(min=1, max=30)
    private String firstName;
    @NotBlank
    @Size(min=1, max=30)
    private String lastName;
    @NotBlank
    @Email
    @Size(max=70)
    private String email;

    private Set<UserRole> roles; // 이부분 확신이 서지 않는다

    boolean enabled;
    public UserAdminUpdateDto (String username, String password, String firstName, String lastName, String email, boolean enabled) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.enabled = enabled;
    }

    public UserAdminUpdateDto (String username, String password, String firstName, String lastName, String email, Set<UserRole> roles, boolean enabled) {
        this(username,password,firstName,lastName,email, enabled);
        this.roles= roles;
    }

}
