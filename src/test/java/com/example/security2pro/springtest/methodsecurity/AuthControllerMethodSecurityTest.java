package com.example.security2pro.springtest.methodsecurity;

import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.enums.UserRole;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.repository.repository_interfaces.TokenRepository;
import com.example.security2pro.repository.repository_interfaces.UserRepository;
import com.example.security2pro.springtest.methodsecurity.securitycontextsetter.WithMockCustomUserWithJwt;
import com.example.security2pro.springtest.methodsecurity.securitycontextsetter.WithMockCustomUserWithRefreshToken;
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
class AuthControllerMethodSecurityTest {


    /**
     * Authorization & AuthController & Basic Authentication tests.
     * Note that authentication by spring security filters is bypassed
     * when @WithSecurityContext is used.
     * Therefore, tests annotated with '@WithMockCustomUserWithJwt' and '@WithMockCustomUserWithRefreshToken'
     * bypasses authentication process, verifies only the controller under test.
     *
     */
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;
    @Autowired
    TokenRepository tokenRepository;
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
    void basicAuthSuccess_issuesRefreshAndAccess() throws Exception {
        String encoded = passwordEncoder.encode("teamLeadPassword");
        User user = new UserTestDataBuilder()
                .withUsername("teamLead")
                .withPassword(encoded)
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_LEAD))
                .build();
        user = userRepository.save(user);

        mvc.perform(MockMvcRequestBuilders.post("/api/login")
                .with(httpBasic("teamLead","teamLeadPassword")))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(cookie()) //refresh expected
//                .andExpect(content()) //access expected
        ;
    }

    @Test
    @WithMockCustomUserWithRefreshToken(username = "teamLead")
    void preAuthorization_denyAccess_givenWrongAuthenticationType() {
        //RefreshAuthentication is not used for authorization
        assertThrows(ServletException.class,
                ()->mvc.perform(MockMvcRequestBuilders.get("/test-preauth/"+projectIdForAuthorization)));
    }

    @Test
    @WithMockCustomUserWithRefreshToken(username = "teamLead")
    void preAuthorization_wrongRefresh() throws Exception {

        //The reason createRefreshToken is called..
        //It thinks it needs to send out cookie (Basic auth case) since there is no
        //proper cookie that was expected..
        // controller logic is that when expected cookie is not found,
        // it considers Basic was used.
        // This concludes that WithMockCustom.... bypasses authentication!!!!
        // But this case can be used as how it will issue refresh?..

        //expect only access token and not refresh !

        User user = new UserTestDataBuilder()
                .withUsername("teamLead")
                .withAuthorities(Set.of(UserRole.ROLE_TEAM_LEAD))
                .build();
        user = userRepository.save(user);

//        RefreshTokenData refreshTokenData
//                = new RefreshTokenData(
//                1L
//                ,user
//                , Date.from(Instant.now().plus(1, ChronoUnit.DAYS))
//                ,"testRefreshToken");
//        tokenRepository.createNewToken(refreshTokenData);

        Cookie cookie = new Cookie("refresh_token","refreshTokenStringValue");
        mvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk());
    }




}
