package com.example.security2pro.dto.user;

import com.example.security2pro.domain.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {

    private final Long id;
    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String email;


    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }
}
