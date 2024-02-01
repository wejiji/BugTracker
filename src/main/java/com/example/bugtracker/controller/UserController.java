package com.example.bugtracker.controller;

import com.example.bugtracker.dto.user.*;
import com.example.bugtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
public class UserController {

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;


    @PostMapping("/users")
    public UserResponseDto register(@Validated @RequestBody UserRegistrationDto userRegistrationDto,
                                    BindingResult bindingResult) throws BindException {

         if(bindingResult.hasErrors())throw new BindException(bindingResult);

        return userService.register(userRegistrationDto);
    }

    @PostMapping("users/change-password/{username}")
    @PreAuthorize("authentication.principal.username == #username")
    public ResponseEntity<String> changePassword(@PathVariable String username, @RequestBody ChangePasswordDto changePasswordDto){
        userService.changePassword(username, changePasswordDto);
        // If not successful, an exception will be thrown.
        return new ResponseEntity<>("password has been changed successfully", HttpStatus.OK);
    }

    @DeleteMapping("/users/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String username){
        userService.deleteUser(username);
        // An exception will be thrown if a non-existent id is passed.
        return new ResponseEntity<>("the user with username "+ username +" has been deleted successfully", HttpStatus.OK);
    }

    @PostMapping("/users/update/{username}")
    @PreAuthorize("authentication.principal.username == #username or hasRole('ADMIN')")
    public UserResponseDto updateUserNamesAndEmail(@PathVariable String username, @Validated UserSimpleUpdateDto userSimpleUpdateDto){
        return userService.updateUserNamesAndEmail(userSimpleUpdateDto);
    }

    @PostMapping("/users/admin-update/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUserByAdmin(@PathVariable String username, @Validated UserAdminUpdateDto userAdminUpdateDto){

        userService.updateUser(username, userAdminUpdateDto);

        return new ResponseEntity<>("update successful",HttpStatus.OK);
    }







}