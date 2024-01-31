package com.example.security2pro.integrationtest.methodsecurity;

import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.UserRole;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.repository.repository_interfaces.UserRepository;
import com.example.security2pro.integrationtest.methodsecurity.securitycontextsetter.WithMockCustomUserWithJwt;
import com.example.security2pro.integrationtest.methodsecurity.securitycontextsetter.WithMockCustomUserWithRefreshToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static com.example.security2pro.smalltest.authorization.ProjectMemberPermissionEvaluatorTest.projectIdForAuthorization;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TestMethodSecurityController.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@AutoConfigureTestDatabase
class MethodSecurityTest {

    /**
     * Authorization tests.
     * Note that authentication by spring security filters is bypassed when @WithSecurityContext is used
     * , meaning tests annotated with '@WithMockCustomUserWithJwt' and '@WithMockCustomUserWithRefreshToken'
     * bypass the authentication process, verifies only pre-authorization.
     */
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder= new BCryptPasswordEncoder();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockCustomUserWithJwt(username = "projectMember")
    void preAuthorization_denyAccess_givenUnauthorizedRole() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/test-preauth/"+ projectIdForAuthorization))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUserWithJwt(username = "projectLead")
    void preAuthorization_allowAccess_givenAuthorizedProjectRole() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/test-preauth/"+projectIdForAuthorization))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUserWithJwt(username="admin")
    void preAuthorization_allowAccess_givenAuthorizedUserRole() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/test-preauth/"+projectIdForAuthorization))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUserWithRefreshToken(username = "teamLead")
    void preAuthorization_denyAccess_givenWrongAuthenticationType() {
        //RefreshAuthentication is not used for authorization.
        assertThrows(ServletException.class,
                ()->mvc.perform(MockMvcRequestBuilders.get("/test-preauth/"+projectIdForAuthorization)));
    }

    @Test
    @WithMockCustomUserWithRefreshToken(username = "teamLead")
    void preAuthorization_allowAccess_givenAuthorizedUserRoleInRefreshAuthentication() throws Exception {

        Cookie refreshToken = new Cookie("refresh_token","refreshTokenStringValue");

        mvc.perform(MockMvcRequestBuilders.get("/test-preauth-user-role-test/"+projectIdForAuthorization)
                        .cookie(refreshToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("refresh_token"));
    }

    @Test
    @WithMockCustomUserWithRefreshToken(username = "teamLead")
    void preAuthorization_allowAccess_givenAuthorizedUserRoleInUsernamePasswordTokenAuthentication() throws Exception {
        String encoded = passwordEncoder.encode("teamLeadPassword");
        User user = new UserTestDataBuilder()
                .withUsername("teamLead")
                .withPassword(encoded)
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_LEAD))
                .build();
        user = userRepository.save(user);

        mvc.perform(MockMvcRequestBuilders.get("/test-preauth-user-role-test/"+projectIdForAuthorization)
                        .with(httpBasic("teamLead","teamLeadPassword")))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
