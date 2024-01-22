package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    /*
     * 'User' instances are sometimes constructed by using UserTestData builder in this test class.
     * When UserTestData is instantiated,
     * each field is initialized with default value when no argument is passed for the field.
     *
     * Each test of this class will test at most one method of User class
     * every test of this class is a small test
     */

    @Test
    void createUser_createsAndReturnsUser() {
        // Success case: Valid 'UserRole' arguments passed for the 'authorities' field.

        //Setup & Execution
        User user = User.createUser(1L, "testUsername"
                , "testPassword"
                , "testFirstName"
                , "testLastName"
                , "test@gmail.com"
                , Set.of(UserRole.ROLE_TEAM_MEMBER)
                , true
        );

        //Assertions
        assertEquals("testUsername", user.getUsername());
        assertEquals("testPassword", user.getPassword());
        assertEquals("testFirstName", user.getFirstName());
        assertEquals("testLastName", user.getLastName());
        assertEquals(Set.of(UserRole.ROLE_TEAM_MEMBER), user.getAuthorities());
        assertEquals("test@gmail.com", user.getEmail());
        assertTrue(user.isEnabled());
    }

    @Test
    void createUser_throwsException_givenBothTeamMemberAndLeadRoles() {
        /*
         * Ensures that a 'User' cannot be assigned both 'ROLE_TEAM_MEMBER' and 'ROLE_TEAM_LEAD'.
         * This test verifies the expected 'IllegalArgumentException' is thrown
         * when attempting to include both roles in the 'authorities' field argument set.
         */

        Set<UserRole> userRoles = Set.of(UserRole.ROLE_TEAM_MEMBER, UserRole.ROLE_TEAM_LEAD);

        assertThrows(IllegalArgumentException.class, () -> User.createUser(1L, "testUsername"
                , "testPassword"
                , "testFirstName"
                , "testLastName"
                , "test@gmail.com"
                , userRoles
                , true
        ));

    }


    @Test
    void createUser_throwsException_givenAdminRole() {
        /*
         * This test ensures that the 'admin' role can only be assigned in the database.
         * It checks if an 'IllegalArgumentException' is thrown
         * when 'ROLE_ADMIN' is passed as an argument for the 'authorities' field.
         */

        Set<UserRole> userRoles = Set.of(UserRole.ROLE_ADMIN);
        assertThrows(IllegalArgumentException.class, () -> User.createUser(1L, "testUsername"
                , "testPassword"
                , "testFirstName"
                , "testLastName"
                , "test@gmail.com"
                , userRoles
                , true
        ));
    }

    @Test
    void changePassword_updatesPassword() {
        // Verifies that the 'changePassword' method correctly updates the 'password' field.

        //Setup
        User user = new UserTestDataBuilder()
                .withPassword("testPassword")
                .build();

        //Execution
        user.changePassword("newPassword");

        //Assertions
        assertEquals("newPassword", user.getPassword());
    }

    @Test
    void updateNamesAndEmail_updatesFirstNameAndLastNameAndEmail() {
        /*
         * Ensures that the 'updateNamesAndEmail' method correctly updates
         * the 'firstName','lastName' and 'email' fields.
         */

        //Setup
        User user = new UserTestDataBuilder()
                .withFirstName("originalFirstName")
                .withLastName("originalLastName")
                .withEmail("originalEmail@gmail.com")
                .build();

        //Execution
        user.updateNamesAndEmail(
                "updatedFirstName"
                , "updatedLastName"
                , "updatedEmail@gmail.com");

        //Assertions
        User expectedUser = new UserTestDataBuilder()
                .withFirstName("updatedFirstName")
                .withLastName("updatedLastName")
                .withEmail("updatedEmail@gmail.com")
                .build();
        assertThat(user).usingRecursiveComparison().isEqualTo(expectedUser);
        // also checks that other fields are not affected by this update
    }

    void adminUpdate_throwsException_givenAdminRole() {
        /*
         * The 'admin' role should only be assigned in the database.
         * This test verifies that an 'IllegalArgumentException' is thrown
         * when attempting to pass 'ROLE_ADMIN' as an argument for the 'authorities' field.
         */

        //Setup
        User user = new UserTestDataBuilder()
                .withUsername("originalUsername")
                .withPassword("originalPassword")
                .withFirstName("originalFirstName")
                .withLastName("originalLastName")
                .withEmail("originalEmail@gmail.com")
                .withAuthorities(Set.of(UserRole.ROLE_ADMIN))
                .withEnabled(false)
                .build();

        //Execution & Assertions
        Set<UserRole> userRoles = Set.of(UserRole.ROLE_ADMIN);
        assertThrows(IllegalArgumentException.class,
                () -> user.adminUpdate(
                        "updatedUsername"
                        , "updatedPassword"
                        , "updatedFirstName"
                        , "updatedLastName"
                        , "updatedEmail@gmail.com"
                        , userRoles
                        , true
                ));
    }


    void adminUpdate_throwsException_givenBothTeamMemberAndLeadRole() {
        /*
         * 'User' cannot be assigned both 'ROLE_TEAM_MEMBER' and 'ROLE_TEAM_LEAD'.
         * This test verifies that an 'IllegalArgumentException' is thrown
         * when attempting to include both roles in the arguments for the 'authorities' field.
         */

        //Setup
        User user = new UserTestDataBuilder()
                .withUsername("originalUsername")
                .withPassword("originalPassword")
                .withFirstName("originalFirstName")
                .withLastName("originalLastName")
                .withEmail("originalEmail@gmail.com")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .withEnabled(false)
                .build();

        //Execution & Assertions

        Set<UserRole> userRoleSet = Set.of(UserRole.ROLE_TEAM_MEMBER, UserRole.ROLE_TEAM_LEAD);
        assertThrows(IllegalArgumentException.class,
                () -> user.adminUpdate(
                        "updatedUsername"
                        , "updatedPassword"
                        , "updatedFirstName"
                        , "updatedLastName"
                        , "updatedEmail@gmail.com"
                        , userRoleSet
                        , true
                ));
    }


    @Test
    void adminUpdate_updatesUser() {
        /*
         * Success case : The 'adminUpdate' method can be only called
         * by the user with 'Admin' role.
         * This test verifies that the method correctly updates a 'User'.
         */


        //Setup
        User user = new UserTestDataBuilder()
                .withUsername("originalUsername")
                .withPassword("originalPassword")
                .withFirstName("originalFirstName")
                .withLastName("originalLastName")
                .withEmail("originalEmail@gmail.com")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_MEMBER))
                .withEnabled(false)
                .build();

        //Execution
        user.adminUpdate(
                "updatedUsername"
                , "updatedPassword"
                , "updatedFirstName"
                , "updatedLastName"
                , "updatedEmail@gmail.com"
                , Set.of(UserRole.ROLE_TEAM_LEAD)
                , true
        );

        //Assertions
        assertEquals("updatedUsername", user.getUsername());
        assertEquals("updatedPassword", user.getPassword());
        assertEquals("updatedFirstName", user.getFirstName());
        assertEquals("updatedLastName", user.getLastName());
        assertEquals("updatedEmail@gmail.com", user.getEmail());
        assertEquals(Set.of(UserRole.ROLE_TEAM_LEAD), user.getAuthorities());
        assertTrue(user.isEnabled());

    }


}
