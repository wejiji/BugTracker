package com.example.security2pro.controller;

import com.example.security2pro.domain.enums.UserRole;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.dto.user.*;
import com.example.security2pro.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;



    @GetMapping("/create-default-user")
    public void createUser(){
        String encoded= passwordEncoder.encode("1235");
        User user = User.createUser(null,"yj",encoded,"Yeaji","Choi","",new HashSet<>(List.of(UserRole.valueOf("ROLE_ADMIN"))),true);
        try{
            userService.loadUserByUsername(user.getUsername());
        }catch(UsernameNotFoundException e){
            return;
        }
        userService.createUser(user);
    }



    @PostMapping("/api/register/users")
    public UserResponseDto register(@Validated @RequestBody UserRegistrationDto userRegistrationDto,
                                    BindingResult bindingResult) throws BindException {

        //userRegistration 객체가 만들어지긴 하는데 validation 안맞을경우
        //스프링에 의해 BindException 던져진것 받아서 처리하도록 함..?
        // (RegistrationErrorDto 는 현재 안쓰이는데 그렇게 쓰이도록 할수도 있음 - bindingErrorConverter에 코드있음 )
        if(bindingResult.hasErrors())throw new BindException(bindingResult);

        return userService.register(userRegistrationDto);
    }

    @PostMapping("users/change-password/{username}")
    @PreAuthorize("authentication.principal.username == #username")
    public ResponseEntity<String> changePassword(@PathVariable String username, @RequestBody ChangePasswordDto changePasswordDto){
        // if not successful, exceptions will be thrown
        userService.changePassword(username, changePasswordDto);

        return new ResponseEntity<>("password has been changed successfully", HttpStatus.OK);
    }

    @DeleteMapping("/users/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String username){
        userService.deleteUser(username);

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
