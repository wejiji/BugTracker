package com.example.bugtracker.smalltest.service;

import com.example.bugtracker.databuilders.UserTestDataBuilder;
import com.example.bugtracker.fake.authentication.PasswordEncoderFake;
import com.example.bugtracker.service.UserService;
import com.example.bugtracker.service.UserServiceImpl;
import com.example.bugtracker.fake.authentication.SecurityContextHolderStrategyFake;
import com.example.bugtracker.domain.enums.UserRole;
import com.example.bugtracker.domain.model.User;
import com.example.bugtracker.dto.user.*;
import com.example.bugtracker.fake.repository.UserRepositoryFake;
import com.example.bugtracker.repository.repository_interfaces.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class UserServiceTest {
    /*
    * Tests for 'UserService' using mocks with
    * the Jpa repository replaced by a fake repository
    *
    * Additional test cases are required to cover scenarios
     * where 'UserService' should not intercept and propagate exceptions.
    */

    private final UserRepository userRepository = new UserRepositoryFake();
    private final PasswordEncoder passwordEncoder = new PasswordEncoderFake(); //does nothing
    private final UserService userService =
            new UserServiceImpl(
                    userRepository
                    , passwordEncoder
                    , new SecurityContextHolderStrategyFake());

    @Test
    void loadUserByUsername_throwsException_givenNonExistentUser() {
        /*
         * Verifies that an exception is thrown
         * when a non-existent username is passed.
         */

        String usernameNotExist = "notExist";
        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(usernameNotExist));
    }

    @Test
    void loadUserByUsername_fetchesAndReturnsUserDetail() {
        /*
         * Verifies that the 'loadUserByUsername' method correctly fetches a 'User'
         * and then returns a 'SecurityUser' constructed from the fetched 'User' object.
         * Note that 'UserRole's of the user's 'authorities' field will be converted to 'SimpleGrantedAuthority'.
         */

        User userCreated = new UserTestDataBuilder()
                .withUsername("testUsername")
                .withPassword("testPassword")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .withEnabled(true)
                .build();
        userCreated = userRepository.save(userCreated);

        //Execution
        UserDetails userDetails = userService.loadUserByUsername("testUsername");

        assertEquals("testUsername", userDetails.getUsername());
        // Authority elements have to be 'SimpleGrantedAuthority' object because the method returns 'UserDetails' instance
        assertEquals(new HashSet<>(Set.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER"))), userDetails.getAuthorities());
        assertEquals("testPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void register_throwsException_givenDuplicateUsername() {
        /*
         * Verifies that the 'register' method throws an exception
         * when attempting to register a user with a duplicate username.
         */

        //Setup
        User user = new UserTestDataBuilder().withUsername("userNameAlreadyExist").build();
        userRepository.save(user);

        UserRegistrationDto userRegistrationDto
                = new UserRegistrationDto(
                "userNameAlreadyExist"
                , "testPassword"
                , "testFirstName"
                , "testLastName"
                , "test@gmail.com");

        //Execution & Assertions
        assertThrows(DuplicateKeyException.class,
                () -> userService.register(userRegistrationDto));
    }

    @Test
    void register_createsAndReturnsUserRegistrationDto_givenNewUser() {
        /*
         * Tests a successful user registration case using the 'register' method
         * where the given user's username does not already exist in the repository.
         *
         * Also verifies the returning 'UserRegistrationDto' is correctly populated
         * */

        //Setup
        UserRegistrationDto userRegistrationDto
                = new UserRegistrationDto("testUsername"
                , "testPassword"
                , "testFirstName"
                , "testLastName"
                , "test@gmail.com");

        assertThat(userRepository.findAll()).isEmpty();

        //Execution
        UserResponseDto userResponseDto = userService.register(userRegistrationDto);

        //Assertions
        assertEquals(1, userRepository.findAll().size());
        User userFound = userRepository.findUserByUsername("testUsername").get();

        assertEquals("testUsername", userFound.getUsername());
        //assertEquals("testPassword",userFound.getPassword());
        assertEquals("testFirstName", userFound.getFirstName());
        assertEquals("testLastName", userFound.getLastName());
        assertEquals("test@gmail.com", userFound.getEmail());
        assertEquals(Set.of(UserRole.ROLE_TEAM_MEMBER), userFound.getAuthorities());
        assertFalse(userFound.isEnabled());

        assertEquals(userFound.getId(), userResponseDto.getId());
        assertEquals(userFound.getUsername(), userResponseDto.getUsername());
        assertEquals(userFound.getFirstName(), userResponseDto.getFirstName());
        assertEquals(userFound.getLastName(), userResponseDto.getLastName());
        assertEquals(userFound.getEmail(), userResponseDto.getEmail());
    }


    @Test
    void UserResponseDto_givenUser() {
        /*
         * Verifies that the constructor of 'UserResponseDto' correctly assigns fields
         * from the provided 'User' argument.
         */

        //Setup
        User user = new UserTestDataBuilder()
                .withId(10L)
                .withUsername("testUsername")
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .build();

        //Execution
        UserResponseDto userResponseDto = new UserResponseDto(user);

        //Assertions
        assertEquals(10L, userResponseDto.getId());
        assertEquals("testUsername", userResponseDto.getUsername());
        assertEquals("testFirstName", userResponseDto.getFirstName());
        assertEquals("testLastName", userResponseDto.getLastName());

    }

    @Test
    void UserSimpleUpdateDto_givenUser() {
        /*
         * Verifies that the constructor of 'UserSimpleUpdateDto' correctly assigns fields
         * from the provided 'User' argument.
         */

        //Setup
        User user = new UserTestDataBuilder()
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withEmail("test@gmail.com")
                .build();

        //Execution
        UserSimpleUpdateDto userSimpleUpdateDto = new UserSimpleUpdateDto(user);

        //Assertions
        assertEquals("testFirstName", userSimpleUpdateDto.getFirstName());
        assertEquals("testLastName", userSimpleUpdateDto.getLastName());
        assertEquals("test@gmail.com", userSimpleUpdateDto.getEmail());
    }

    @Test
    void UserSimpleUpdateDto_givenFieldsValues() {
        /*
         * Verifies that the constructor of 'UserSimpleUpdateDto' correctly assigns fields
         * from the provided arguments.
         */

        //Setup
        User user = new UserTestDataBuilder()
                .withId(10L)
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withEmail("test@gmail.com")
                .build();

        //Execution
        UserSimpleUpdateDto userSimpleUpdateDto
                = new UserSimpleUpdateDto(
                 "testFirstName"
                , "testLastName"
                , "test@gmail.com");

        //Assertions
        assertEquals("testFirstName", userSimpleUpdateDto.getFirstName());
        assertEquals("testLastName", userSimpleUpdateDto.getLastName());
        assertEquals("test@gmail.com", userSimpleUpdateDto.getEmail());
    }


    @Test
    void updateUserNamesAndEmail_updatesFirstNameAndLastNameAndEmail_andThenReturnsUserResponseDto() {
        /*
         * Ensures that the 'updateUserNamesAndEmail' method correctly updates
         * the 'firstName','lastName' and 'email' fields.
         *
         * Also verifies that 'UserResponseDto' values are correctly populated.
         */

        //Setup
        User userBeforeUpdate = new UserTestDataBuilder()
                .withId(10L)
                .withUsername("testUsername")
                .withFirstName("originalFirstName")
                .withLastName("originalLastName")
                .withEmail("originalEmail@gmail.com")
                .withEnabled(true)
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();
        userBeforeUpdate = userRepository.save(userBeforeUpdate);

        UserSimpleUpdateDto userSimpleUpdateDto = new UserSimpleUpdateDto(
                 "updatedFirstName"
                , "updatedLastName"
                , "updatedEmail@gmail.com");

        //Execution
        UserResponseDto userResponseDtoReturned
                = userService.updateUserNamesAndEmail("testUsername",userSimpleUpdateDto);

        //Assertions
        assertEquals(10L, userResponseDtoReturned.getId());
        assertEquals("updatedFirstName", userResponseDtoReturned.getFirstName());
        assertEquals("updatedLastName", userResponseDtoReturned.getLastName());
        assertEquals("updatedEmail@gmail.com", userResponseDtoReturned.getEmail());

        User expectedUser = new UserTestDataBuilder()
                .withId(10L)
                .withUsername("testUsername")
                .withFirstName("updatedFirstName")
                .withLastName("updatedLastName")
                .withEmail("updatedEmail@gmail.com")
                .withEnabled(userBeforeUpdate.isEnabled())
                .withAuthorities(userBeforeUpdate.getAuthorities())
                .build();
        User userFound = userRepository.findById(10L).get();

        assertThat(expectedUser)
                .usingRecursiveComparison()
                .isEqualTo(userFound);
    }


    @Test
    void updateUser_updatesUser() {
        /*
         * Tests the 'updateUser' method to ensure it correctly modifies a user's details.
         */

        //Setup
        User userBeforeUpdate = new UserTestDataBuilder()
                .withId(10L)
                .withUsername("originalUsername")
                .withPassword("originalPassword")
                .withFirstName("originalFirstName")
                .withLastName("originalLastName")
                .withEmail("originalEmail@gmail.com")
                .withEnabled(true)
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .build();
        userRepository.save(userBeforeUpdate);

        UserAdminUpdateDto userAdminUpdateDto = new UserAdminUpdateDto(
                 "updatedPassword"
                , "updatedFirstName"
                , "updatedLastName"
                , "updatedEmail@gmail.com"
                , Set.of(UserRole.ROLE_TEAM_LEAD)
                , false
        );

        //Execution
        userService.updateUser("originalUsername", userAdminUpdateDto);


        User expectedUser = new UserTestDataBuilder()
                .withId(10L)
                .withUsername("originalUsername")
                .withPassword("updatedPassword")
                .withFirstName("updatedFirstName")
                .withLastName("updatedLastName")
                .withEmail("updatedEmail@gmail.com")
                .withEnabled(false)
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_LEAD))
                .build();
        User userFound = userRepository.findById(10L).get();

        assertThat(expectedUser)
                .usingRecursiveComparison()
                .isEqualTo(userFound);
    }

    @Test
    void deleteUser_deletesUser_givenUsername() {
        /*
         * Tests that the 'deleteUser' method in 'userService'
         * correctly removes a user from the repository.
         */

        //Setup
        User user = new UserTestDataBuilder().withUsername("userToBeDeleted").build();
        userRepository.save(user);
        assertEquals(1, userRepository.findAll().size());

        //Execution
        userService.deleteUser("userToBeDeleted");

        //Assertions
        assertThat(userRepository.findAll()).isEmpty();
        assertThat(userRepository.findUserByUsername("userToBeDeleted")).isEmpty();
    }


}
