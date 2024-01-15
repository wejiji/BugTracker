package com.example.security2pro.domain.model;

import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.Role;
import org.junit.jupiter.api.Test;

import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;

public class UserTest {


    @Test
    public void createUser_success(){

        User user = User.createUser(1L,"testUsername"
                ,"testPassword"
                ,"testFirstName"
                ,"testLastName"
                ,"test@gmail.com"
                ,Set.of(Role.ROLE_TEAM_MEMBER)
                ,true
        );

        assertEquals("testUsername",user.getUsername());
        assertEquals("testPassword",user.getPassword());
        assertEquals("testFirstName",user.getFirstName());
        assertEquals("testLastName",user.getLastName());
        assertEquals(Set.of(Role.ROLE_TEAM_MEMBER) ,user.getAuthorities());
        assertEquals("test@gmail.com",user.getEmail());
        assertTrue(user.isEnabled());
    }

    @Test
    public void createUser_throwsExceptionGivenBothTeamMemberAndLeadRoles(){
        assertThrows( IllegalArgumentException.class ,()->User.createUser(1L,"testUsername"
                ,"testPassword"
                ,"testFirstName"
                ,"testLastName"
                ,"test@gmail.com"
                ,Set.of(Role.ROLE_PROJECT_MEMBER, Role.ROLE_TEAM_LEAD)
                ,true
        ));

    }

    @Test
    public void createUser_throwsExceptionGivenInvalidProjectMemberRole(){

        assertThrows( IllegalArgumentException.class ,()->User.createUser(1L,"testUsername"
                ,"testPassword"
                ,"testFirstName"
                ,"testLastName"
                ,"test@gmail.com"
                ,Set.of(Role.ROLE_PROJECT_MEMBER)
                ,true
        ));

    }

    @Test
    public void createUser_throwsExceptionGivenAdminRole(){

        assertThrows( IllegalArgumentException.class ,()->User.createUser(1L,"testUsername"
                ,"testPassword"
                ,"testFirstName"
                ,"testLastName"
                ,"test@gmail.com"
                ,Set.of(Role.ROLE_ADMIN)
                ,true
        ));

    }

    @Test
    public void changePassword(){

        User user = new UserTestDataBuilder()
                .withPassword("testPassword")
                .build();

        user.changePassword("newPassword");

        assertEquals("newPassword",user.getPassword());
    }

    @Test
    public void updateNamesAndEmail(){

        User user = new UserTestDataBuilder()
                .withFirstName("originalFirstName")
                .withLastName("originalLastName")
                .withEmail("originalEmail@gmail.com")
                .build();

        user.updateNamesAndEmail(
                "updatedFirstName"
                ,"updatedLastName"
                ,"updatedEmail@gmail.com");

        assertEquals("updatedFirstName",user.getFirstName());
        assertEquals("updatedLastName", user.getLastName());
        assertEquals("updatedEmail@gmail.com", user.getEmail());

    }

    public void adminUpdate_throwsExceptionGivenAdminRole(){

        User user = new UserTestDataBuilder()
                .withUsername("originalUsername")
                .withPassword("originalPassword")
                .withFirstName("originalFirstName")
                .withLastName("originalLastName")
                .withEmail("originalEmail@gmail.com")
                .withAuthorities(Set.of(Role.ROLE_ADMIN))
                .withEnabled(false)
                .build();

        assertThrows(IllegalArgumentException.class,
                ()->user.adminUpdate(
                        "updatedUsername"
                        ,"updatedPassword"
                        ,"updatedFirstName"
                        ,"updatedLastName"
                        ,"updatedEmail@gmail.com"
                        ,Set.of(Role.ROLE_ADMIN)
                        ,true
                ));


    }

    @Test
    public void adminUpdate_throwsExceptionGivenProjectMemberRole(){

        User user = new UserTestDataBuilder()
                .withUsername("originalUsername")
                .withPassword("originalPassword")
                .withFirstName("originalFirstName")
                .withLastName("originalLastName")
                .withEmail("originalEmail@gmail.com")
                .withAuthorities(Set.of(Role.ROLE_TEAM_MEMBER))
                .withEnabled(false)
                .build();

        assertThrows(IllegalArgumentException.class,
                ()->user.adminUpdate(
                        "updatedUsername"
                        ,"updatedPassword"
                        ,"updatedFirstName"
                        ,"updatedLastName"
                        ,"updatedEmail@gmail.com"
                        ,Set.of(Role.ROLE_PROJECT_MEMBER)
                        ,true
                ));

    }

    public void adminUpdate_throwsExceptionGivenBothProjectMemberAndLeadRole(){

        User user = new UserTestDataBuilder()
                .withUsername("originalUsername")
                .withPassword("originalPassword")
                .withFirstName("originalFirstName")
                .withLastName("originalLastName")
                .withEmail("originalEmail@gmail.com")
                .withAuthorities(Set.of(Role.ROLE_TEAM_MEMBER))
                .withEnabled(false)
                .build();

        assertThrows(IllegalArgumentException.class,
                ()->user.adminUpdate(
                        "updatedUsername"
                        ,"updatedPassword"
                        ,"updatedFirstName"
                        ,"updatedLastName"
                        ,"updatedEmail@gmail.com"
                        ,Set.of(Role.ROLE_PROJECT_MEMBER,Role.ROLE_TEAM_LEAD)
                        ,true
                ));

    }


    @Test
    public void adminUpdate_success(){

        User user = new UserTestDataBuilder()
                .withUsername("originalUsername")
                .withPassword("originalPassword")
                .withFirstName("originalFirstName")
                .withLastName("originalLastName")
                .withEmail("originalEmail@gmail.com")
                .withAuthorities(Set.of(Role.ROLE_TEAM_MEMBER))
                .withEnabled(false)
                .build();

        user.adminUpdate(
                        "updatedUsername"
                        ,"updatedPassword"
                        ,"updatedFirstName"
                        ,"updatedLastName"
                        ,"updatedEmail@gmail.com"
                        ,Set.of(Role.ROLE_TEAM_LEAD)
                        ,true
        );

        assertEquals("updatedUsername",user.getUsername());
        assertEquals("updatedPassword",user.getPassword());
        assertEquals("updatedFirstName",user.getFirstName());
        assertEquals("updatedLastName",user.getLastName());
        assertEquals("updatedEmail@gmail.com",user.getEmail());
        assertEquals(Set.of(Role.ROLE_TEAM_LEAD),user.getAuthorities());
        assertTrue(user.isEnabled());

    }









}
