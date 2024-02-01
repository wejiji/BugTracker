package com.example.bugtracker.dto.user;

import com.example.bugtracker.domain.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {

    private final Long id;

    private final String username;

    private final String firstName;

    private final String lastName;

    private final String email;


    public UserResponseDto(User user) {

        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }

    public UserResponseDto(Long id
            , String username
            , String firstName
            , String lastName
            , String email) {

        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
