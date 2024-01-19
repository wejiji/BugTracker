package com.example.security2pro.authentication;


import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.service.auth0.JwtTokenManager;
import org.springframework.security.core.Authentication;
import java.util.HashMap;
import java.util.Map;

import static com.example.security2pro.authorization.ProjectMemberPermissionEvaluatorTest.projectId;


public class JwtTokenManagerImplFake implements JwtTokenManager {
    //this is used for JwtAuthenticationProviderTest only for now

//    private Clock clock;
//    private String signingKey;
//
//    private TokenRepository tokenRepository;
//    private int refreshMaxAgeInDays ;
//    private int accessMaxAgeInMins;

//    public TokenManagerImplFake(TokenRepository tokenRepository, String signingKey, Clock clock, int refreshMaxAgeInDays, int accessMaxAgeInMins){
//        this.tokenRepository = tokenRepository;
//        this.signingKey = signingKey;
//        this.clock = clock;
//        this.refreshMaxAgeInDays = refreshMaxAgeInDays;
//        this.accessMaxAgeInMins = accessMaxAgeInMins;
//
//    }


    @Override
    public String createAccessToken(Authentication authentication) {

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String username = securityUser.getUsername();
        if(username.equals("admin") || username.equals("yj")){
            return "jwtStringForAdmin";
        }

        if(username.equals("projectMember")){
            return "jwtStringForProjectMember";
        }

        if(username.equals("projectLead")){
            return "jwtStringForProjectLead";
        }
        return null;

    }

    @Override
    public Map<String,String> verifyAccessToken(String jwt) {

        Map<String,String> verifiedClaimsMap = new HashMap<>();

        if(jwt.equals("jwtStringForAdmin")){
            verifiedClaimsMap.put("subject","admin");
            verifiedClaimsMap.put("userRoles","ROLE_ADMIN");
            verifiedClaimsMap.put("projectRoles","");
            System.out.println("just verified admin ");
        }

        if(jwt.equals("jwtStringForProjectMember")){
            verifiedClaimsMap.put("subject","projectMember");
            verifiedClaimsMap.put("userRoles","ROLE_TEAM_MEMBER");
            verifiedClaimsMap.put("projectRoles","["+projectId+":ROLE_PROJECT_MEMBER]");
            System.out.println("just verified member ");
        }

        if(jwt.equals("jwtStringForProjectLead")){
            verifiedClaimsMap.put("subject","projectLead");
            verifiedClaimsMap.put("userRoles","ROLE_TEAM_MEMBER");
            verifiedClaimsMap.put("projectRoles","["+projectId+":ROLE_PROJECT_LEAD]");
            System.out.println("just verified lead ");
        }


        return verifiedClaimsMap;
    }

}
