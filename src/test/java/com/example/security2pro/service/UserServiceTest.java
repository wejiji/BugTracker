package com.example.security2pro.service;

import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.dto.user.*;
import com.example.security2pro.repository.UserRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


public class UserServiceTest {

    private final UserRepository userRepository = new UserRepositoryFake();

    private final PasswordEncoder passwordEncoder = new PasswordEncoderFake(); //does nothing


    private final UserService userService = new JpaUserService(userRepository,passwordEncoder, new SecurityContextHolderStrategyFake());


    @Test
    public void loadUserByUsername_throwsException(){
        String usernameNotExist ="notExist";

        assertThrows(UsernameNotFoundException.class, ()->userService.loadUserByUsername(usernameNotExist));
    }

    @Test
    public void loadUserByUsername_success(){
        User userCreated = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withPassword("testPassword")
                .withAuthorities(Set.of(Role.ROLE_TEAM_MEMBER))
                .withEnabled(true)
                .build();
        userCreated = userRepository.save(userCreated);

        //Execution
        UserDetails userDetails = userService.loadUserByUsername("testUsername");

        assertEquals("testUsername", userDetails.getUsername());
        // authority element has to be SimpleGrantedAuthority because the method returns 'SecurityUser' instance
        assertEquals(new ArrayList<>(Set.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER"))),userDetails.getAuthorities());
        assertEquals("testPassword",userDetails.getPassword());
        assertEquals(true,userDetails.isEnabled());
    }

    @Test
    public void register_throwsExceptionWhenGivenUsernameExist(){

        User user = new UserTestDataBuilder().withUsername("userNameAlreadyExist").build();
        userRepository.save(user);

        UserRegistrationDto userRegistrationDto
                = new UserRegistrationDto("userNameAlreadyExist"
                ,"testPassword"
                ,"testFirstName"
                ,"testLastName"
                ,"test@gmail.com");

        assertThrows(DuplicateKeyException.class,()-> userService.register(userRegistrationDto));
    }

    @Test
    public void register_success(){

        UserRegistrationDto userRegistrationDto
                = new UserRegistrationDto("testUsername"
                ,"testPassword"
                ,"testFirstName"
                ,"testLastName"
                ,"test@gmail.com");

        assertThat(userRepository.findAll()).isEmpty();

        //Execution
        UserResponseDto userResponseDto = userService.register(userRegistrationDto);

        //Assertions
        assertEquals(1,userRepository.findAll().size());
        User userFound= userRepository.findUserByUsername("testUsername").get();

        assertEquals("testUsername" ,userFound.getUsername());
        //assertEquals("testPassword",userFound.getPassword());
        assertEquals("testFirstName",userFound.getFirstName());
        assertEquals("testLastName",userFound.getLastName());
        assertEquals("test@gmail.com",userFound.getEmail());
        assertEquals(Set.of(Role.ROLE_TEAM_MEMBER),userFound.getAuthorities());
        assertFalse(userFound.isEnabled());

        assertEquals(userFound.getId(), userResponseDto.getId());
        assertEquals(userFound.getUsername(), userResponseDto.getUsername());
        assertEquals(userFound.getFirstName(), userResponseDto.getFirstName());
        assertEquals(userFound.getLastName(), userResponseDto.getLastName());
        assertEquals(userFound.getEmail(), userResponseDto.getEmail());
    }


    @Test
    public void testUserResponseDto(){
        User user = new UserTestDataBuilder()
                .withId(10L)
                .withUsername("testUsername")
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .build();

        UserResponseDto userResponseDto = new UserResponseDto(user);

        assertEquals(10L, userResponseDto.getId());
        assertEquals("testUsername", userResponseDto.getUsername());
        assertEquals("testFirstName", userResponseDto.getFirstName());
        assertEquals("testLastName", userResponseDto.getLastName());

    }

    @Test
    public void testUserSimpleUpdateDtoFromUser(){
        User user = new UserTestDataBuilder()
                .withId(10L)
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withEmail("test@gmail.com")
                .build();

        UserSimpleUpdateDto userSimpleUpdateDto = new UserSimpleUpdateDto(user);
        assertEquals(10L, userSimpleUpdateDto.getId());
        assertEquals("testFirstName", userSimpleUpdateDto.getFirstName());
        assertEquals("testLastName", userSimpleUpdateDto.getLastName());
        assertEquals("test@gmail.com", userSimpleUpdateDto.getEmail());
    }

    @Test
    public void testUserSimpleUpdateDtoFromParams(){
        User user = new UserTestDataBuilder()
                .withId(10L)
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withEmail("test@gmail.com")
                .build();

        UserSimpleUpdateDto userSimpleUpdateDto = new UserSimpleUpdateDto(10L,"testFirstName","testLastName","test@gmail.com");
        assertEquals(10L, userSimpleUpdateDto.getId());
        assertEquals("testFirstName", userSimpleUpdateDto.getFirstName());
        assertEquals("testLastName", userSimpleUpdateDto.getLastName());
        assertEquals("test@gmail.com", userSimpleUpdateDto.getEmail());
    }


    @Test
    public void updateUserNamesAndEmail(){
        User userBeforeUpdate = new UserTestDataBuilder()
                .withId(10L)
                .withUsername("originalUsername")
                .withFirstName("originalFirstName")
                .withLastName("originalLastName")
                .withEmail("originalEmail@gmail.com")
                .withEnabled(true)
                .withAuthorities(Set.of(Role.ROLE_TEAM_MEMBER))
                .build();
        userBeforeUpdate = userRepository.save(userBeforeUpdate);

        UserSimpleUpdateDto userSimpleUpdateDto = new UserSimpleUpdateDto(
                10L
                ,"updatedFirstName"
                ,"updatedLastName"
                ,"updatedEmail@gmail.com");

        //Execution
        UserResponseDto userResponseDtoReturned = userService.updateUserNamesAndEmail(userSimpleUpdateDto);

        assertEquals(10L, userResponseDtoReturned.getId());
        assertEquals("originalUsername", userResponseDtoReturned.getUsername());
        assertEquals("updatedFirstName", userResponseDtoReturned.getFirstName());
        assertEquals("updatedLastName", userResponseDtoReturned.getLastName());
        assertEquals("updatedEmail@gmail.com", userResponseDtoReturned.getEmail());

        User expectedUser = new UserTestDataBuilder()
                .withId(10L)
                .withUsername("originalUsername")
                .withFirstName("updatedFirstName")
                .withLastName("updatedLastName")
                .withEmail("updatedEmail@gmail.com")
                .withEnabled(userBeforeUpdate.isEnabled())
                .withAuthorities(userBeforeUpdate.getAuthorities())
                .build();
        User userFound = userRepository.findById(10L).get();

        assertThat(expectedUser).usingRecursiveComparison().isEqualTo(userFound);
    }


    @Test
    public void updateUser(){
        User userBeforeUpdate = new UserTestDataBuilder()
                .withId(10L)
                .withUsername("originalUsername")
                .withPassword("originalPassword")
                .withFirstName("originalFirstName")
                .withLastName("originalLastName")
                .withEmail("originalEmail@gmail.com")
                .withEnabled(true)
                .withAuthorities(Set.of(Role.ROLE_TEAM_MEMBER))
                .build();
        userBeforeUpdate = userRepository.save(userBeforeUpdate);

        UserAdminUpdateDto userAdminUpdateDto = new UserAdminUpdateDto(
                "updatedUsername"
                ,"updatedPassword"
                ,"updatedFirstName"
                ,"updatedLastName"
                ,"updatedEmail@gmail.com"
                ,Set.of(Role.ROLE_TEAM_LEAD)
                ,false
        );

        //Execution
        userService.updateUser("originalUsername",userAdminUpdateDto);


        User expectedUser = new UserTestDataBuilder()
                .withId(10L)
                .withUsername("updatedUsername")
                .withPassword("updatedPassword")
                .withFirstName("updatedFirstName")
                .withLastName("updatedLastName")
                .withEmail("updatedEmail@gmail.com")
                .withEnabled(false)
                .withAuthorities(Set.of(Role.ROLE_TEAM_LEAD))
                .build();
        User userFound = userRepository.findById(10L).get();

        assertThat(expectedUser).usingRecursiveComparison().isEqualTo(userFound);

    }

    @Test
    public void deleteUser(){

        User user = new UserTestDataBuilder().withUsername("userToBeDeleted").build();
        userRepository.save(user);

        assertEquals(1,userRepository.findAll().size());
        //Execution
        userService.deleteUser("userToBeDeleted");

        assertThat(userRepository.findAll()).isEmpty();
        assertThat(userRepository.findUserByUsername("userToBeDeleted")).isEmpty();
    }


    @Test
    public void deleteUser_throwsExceptionWhenUsernameNotExist(){

        assertThat(userRepository.findUserByUsername("usernameNotExist")).isEmpty();
        assertThrows(IllegalArgumentException.class, ()-> userService.deleteUser("usernameNotExist"));
    }








}
