package com.example.bugtracker.dto.user;

import com.example.bugtracker.domain.model.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResponseDto {

    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;


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
