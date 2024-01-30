package com.example.security2pro.smalltest.domain.model;

import com.example.security2pro.domain.enums.UserRole;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRoleTest {
    @Test
    void startsWith(){
        //tests if all the roles of ProjectMemberRole start with 'ROLE_TEAM' except 'ROLE_ADMIN'

        assertTrue(Arrays
                .stream(UserRole.values())
                .filter(role->!role.name().equals("ROLE_ADMIN"))
                .allMatch(role-> role.name().startsWith("ROLE_TEAM")));
    }
}
