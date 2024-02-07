package com.example.bugtracker.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangePasswordDto {
    @NotBlank
    @Size(min = 8, max = 20)
    String oldPassword;

    @NotBlank
    @Size(min = 8, max = 20)
    String newPassword;

    public ChangePasswordDto(String oldPassword, String newPassword) {

        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }


}
