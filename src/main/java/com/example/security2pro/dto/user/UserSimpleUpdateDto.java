package com.example.security2pro.dto.user;

import com.example.security2pro.domain.model.User;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserSimpleUpdateDto {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;


    public UserSimpleUpdateDto(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }

    public UserSimpleUpdateDto(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
