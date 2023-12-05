package com.example.security2pro.controller;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.dto.UserRegistrationDto;
import com.example.security2pro.dto.UserResponseDto;
import com.example.security2pro.service.JpaUserService;
import com.example.security2pro.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserService userService;



    @GetMapping("/create-default-user")
    public void createUser(){
        String encoded= passwordEncoder.encode("1235");
        User user = new User("yj",encoded,"Yeaji","Choi","",new HashSet<>(Arrays.asList(Role.valueOf("ROLE_ADMIN"),Role.valueOf("ROLE_TEAM_LEAD"))),true);
        if(userService.userExists(user.getUsername())){
            return;
        }
        userService.createUser(user);
    }


    @PostMapping("/api/register/users")
    public UserResponseDto register( // registrationDto를 리턴해도 될까??
                                         @Validated @RequestBody UserRegistrationDto userRegistrationDto,
                                         BindingResult bindingResult) throws BindException {

        //userRegistration 객체자체가 못만들어질 경우?(type mismatch)??
        //모두 string 이라서 그럴일은 없을것 같음;;;;


        //userRegistration 객체가 만들어지긴 하는데 validation 안맞을경우
        //스프링에 의해 BindException 던져진것 받아서 처리하도록 함..?
        // (RegistrationErrorDto 는 현재 안쓰이는데 그렇게 쓰이도록 할수도 있음 - bindingErrorConverter에 코드있음 )
        if(bindingResult.hasErrors())throw new BindException(bindingResult);


        else {
            if(userService.userExists(userRegistrationDto.getUsername())){
                bindingResult.rejectValue("username","duplicate.username","The username already exists");
                throw new BindException(bindingResult);
            }
            //이메일 중복은 체크하지는 않는다. 어드민만이 enable로 돌릴수 있다.

            String username = userRegistrationDto.getUsername();
            String password= passwordEncoder.encode(userRegistrationDto.getPassword());
            String firstName = userRegistrationDto.getFirstName();
            String lastName = userRegistrationDto.getLastName();
            String email = userRegistrationDto.getEmail();


            User user = new User(username,password,firstName,lastName,email,false);
            System.out.println("hoohoo ");
            //여기서 바로 매니저 부르는것이 맞을까?? 로그인 아니고 만드는것 뿐이니까? 로그인은 필터체인 통과. 이건 아님.
            userService.createUser(user);

            //아래는 테스트용
            //return new UserRegistrationDto(username,null, firstName, lastName, email);
            return new UserResponseDto(user);
        }
    }


//    @GetMapping
//    public List<UserResponseDto> list(){
//         return userService.findAll().stream().map(UserResponseDto::new).collect(Collectors.toList());
//    }
//




}
