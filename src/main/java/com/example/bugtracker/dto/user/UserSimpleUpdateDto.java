package com.example.bugtracker.dto.user;

import com.example.bugtracker.domain.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSimpleUpdateDto {

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


    public UserSimpleUpdateDto(User user) {

        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }

    public UserSimpleUpdateDto(
             String firstName
            , String lastName
            , String email) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
