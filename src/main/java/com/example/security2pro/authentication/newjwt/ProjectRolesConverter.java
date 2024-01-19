package com.example.security2pro.authentication.newjwt;

import com.example.security2pro.domain.enums.refactoring.ProjectMemberRole;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ProjectRolesConverter {

    //example string
    // 19:ROLE_PROJECT_LEAD && 1:ROLE_PROJECT_MEMBER,ROLE_PROJECT_MEMBER && 24:ROLE_PROJECT_MEMBER

    public String convertToString(Set<ProjectRoles> projectRolesSet){

        if(projectRolesSet==null|| projectRolesSet.isEmpty()){return "";}

        Map<Long, List<ProjectRoles>> rolesPerProject = projectRolesSet.stream().collect(Collectors.groupingBy(ProjectRoles::getProjectId));

        return projectRolesSet.stream().map(ProjectRoles::toString).collect(Collectors.joining("&&"));
    }

    public Set<ProjectRoles> convertToRoles(String projectRolesInString){
        if(!StringUtils.hasText(projectRolesInString)){return Collections.emptySet();}

        System.out.println("here is the projectRolesInString:"+ projectRolesInString);
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
