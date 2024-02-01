package com.example.security2pro.dto.user;

import com.example.security2pro.domain.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserSimpleUpdateDto {

    @NotNull
    private final Long id;

    @NotBlank
    @Size(min=1, max=30)
    private final String firstName;

    @NotBlank
    @Size(min=1, max=30)
    private final String lastName;

    @NotBlank
    @Email
    @Size(max=70)
    private final String email;


    public UserSimpleUpdateDto(User user) {

        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }

    public UserSimpleUpdateDto(Long id
            , String firstName
            , String lastName
            , String email) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
