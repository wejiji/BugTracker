package com.example.bugtracker.authentication.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProjectRolesConverter {

    // String representation
    // 19:ROLE_PROJECT_LEAD && 1:ROLE_PROJECT_MEMBER,ROLE_PROJECT_MEMBER && 24:ROLE_PROJECT_MEMBER

    public String convertToString(Set<ProjectRoles> projectRolesSet){

        if(projectRolesSet==null|| projectRolesSet.isEmpty()){return "";}

        return projectRolesSet.stream().map(ProjectRoles::inString).collect(Collectors.joining("&&"));
    }

    public Set<ProjectRoles> convertToRoles(String projectRolesInString){
        if(!StringUtils.hasText(projectRolesInString)){return Collections.emptySet();}

        log.info("converting projectRolesInString: "+ projectRolesInString);
        projectRolesInString = projectRolesInString.trim();

        Set<ProjectRoles> projectRolesSet = new HashSet<>();

        Pattern.compile("&&").splitAsStream(projectRolesInString)
                .forEach(roleString->{
                    String[] roleStringArray = roleString.split(":");
                    ProjectRoles projectRoles = new ProjectRoles(roleStringArray[0], roleStringArray[1]);
                    projectRolesSet.add(projectRoles);
                });

        return projectRolesSet;
    }


}
