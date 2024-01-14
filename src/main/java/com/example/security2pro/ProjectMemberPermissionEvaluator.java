package com.example.security2pro;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.dto.issue.authorization.CreateDtoWithProjectId;

import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectMemberRepository;
import com.example.security2pro.repository.repository_interfaces.SprintRepository;
import com.example.security2pro.service.CustomPermissionEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


import java.io.Serializable;
import java.util.Optional;


@RequiredArgsConstructor
@Component
public class ProjectMemberPermissionEvaluator implements CustomPermissionEvaluator {

    private final ProjectMemberRepository projectMemberRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;


    public boolean supports(Object object){
        return object instanceof CreateDtoWithProjectId;
    }


    public boolean supports(String targetType){
        return targetType.equals("issue")||targetType.equals("sprint")||targetType.equals("project")||targetType.equals("projectMember");
    }


    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || (targetDomainObject == null) || (permission==null)){
            return false;
        }
        User user= ((SecurityUser) authentication.getPrincipal()).getUser();
        if(user==null) {
            return false;
        }

        Optional<Long> projectId = getProjectIdFromDto(targetDomainObject);
        if(projectId.isEmpty()) {return false;}

        Optional<ProjectMember> projectMember = projectMemberRepository.findByUsernameAndProjectIdWithAuthorities(user.getUsername(),projectId.get());
        return projectMember.map(member -> member.getAuthorities().contains(Role.valueOf((String) permission))).orElse(false);
    }


    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if ((authentication == null) || (targetType == null) || !(permission instanceof String)){return false;}

        User user= ((SecurityUser) authentication.getPrincipal()).getUser();
        if(user==null) {return false;}
//        if(user.getAuthorities().contains(Role.valueOf((String)permission))){
//            return true;
//        }
        Optional<Long> projectId = getProjectIdFromTypeAndId(targetType, targetId);
        if(projectId.isEmpty()) {return false;}

        Optional<ProjectMember> projectMemberOptional= projectMemberRepository.findByUsernameAndProjectIdWithAuthorities(user.getUsername(),projectId.get());
        if(projectMemberOptional.isEmpty()) {return false;}

        if(((String) permission).startsWith("ROLE_PROJECT")){
            return projectMemberOptional.get().getAuthorities().contains(Role.valueOf((String) permission));
        }
        return false;
   }



    private Optional<Long> getProjectIdFromTypeAndId(String targetType, Serializable targetId){
        Optional<Long> projectId = Optional.empty();

        if(targetType.equals("project")){
            return Optional.of((Long)targetId);
        }
        if(targetType.equals("sprint")){
            return sprintRepository.findById((Long)targetId).map(sprint -> sprint.getProject().getId());
        }
        if(targetType.equals("issue")){
            return issueRepository.findById((Long)targetId).map(issue -> issue.getProject().getId());
        }
        if(targetType.equals("projectMember")){
            return projectMemberRepository.findById((Long)targetId).map(projectMember -> projectMember.getProject().getId());
        }
        return projectId;
    }


    private Optional<Long> getProjectIdFromDto(Object targetDomainObject){
        if(targetDomainObject instanceof CreateDtoWithProjectId createDtoWithProjectId){
            return createDtoWithProjectId.getProjectId(); // always not null
        }

//        if(targetDomainObject instanceof DtoWithIssueId dtoIssueWithId){
//            Optional<Issue> foundIssue= issueRepository.findById(dtoIssueWithId.issueIdForAuthorization());
//            return foundIssue.map(issue -> issue.getProject().getId());
//        }
        return Optional.empty();
    }

//    private <T> Optional<Long> getProjectId(Long id, JpaRepository<T,Long> jpaRepository){
//        Optional<T> tOptional = jpaRepository.findById(id);
//        tOptional.map(t-> t.getProject().getId());
//    }

}

// linting
