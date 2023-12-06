package com.example.security2pro.dto;

import com.example.security2pro.domain.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {

    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;


    public UserResponseDto() {
    }

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }
}
