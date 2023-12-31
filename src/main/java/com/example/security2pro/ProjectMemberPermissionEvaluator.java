package com.example.security2pro;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.dto.issue.authorization.CreateDtoWithProjectId;

import com.example.security2pro.repository.repository_interfaces.ActivityRepository;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectMemberRepository;
import com.example.security2pro.repository.repository_interfaces.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;


import java.io.Serializable;
import java.util.Optional;


@RequiredArgsConstructor
public class ProjectMemberPermissionEvaluator implements PermissionEvaluator {

    private final ProjectMemberRepository projectMemberRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;

    private final ActivityRepository activityRepository;


    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || (targetDomainObject == null) || (permission==null)){
            return false;
        }
        User user= ((SecurityUser) authentication.getPrincipal()).getUser();
        if(user==null) {
            return false;
        }
//        if(user.getAuthorities().contains(Role.valueOf((String)permission))){
//            return true;
//        }
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

        if((permission).equals("author")){
            return user.getUsername().equals(getOriginalAuthor(targetType,targetId));
        }
        if(((String) permission).startsWith("ROLE_PROJECT")){
            return projectMemberOptional.get().getAuthorities().contains(Role.valueOf((String) permission));
        }
        return false;
       // return projectMemberOptional.map(projectMember -> projectMember.getAuthorities().contains(Role.valueOf((String) permission))).orElse(false);
    }


    private String getOriginalAuthor(String targetType, Serializable targetId){
        if(targetType.startsWith("activity")){
            Optional<Activity> activityOptional = activityRepository.findById((Long)targetId);
            return activityOptional.map(BaseEntity::getCreatedBy).orElse(null);
        }
        return null;
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
//    private Optional<Long> getProjectIdFromTypeAndId(String targetType, Serializable targetId){
//        Optional<Long> projectId = Optional.empty();
//
//        if(targetType.equals("project")){
//            return Optional.of((Long)targetId);
//        }
//        if(targetType.equals("sprint")){
//            return getSprintProjectId((Long) targetId);
//        }
//        if(targetType.equals("issue")){
//            return getIssueProjectId((Long) targetId);
//        }
//        if(targetType.equals("projectMember")){
//            return getProjectMemberProjectId((Long) targetId);
//        }
//
//        return projectId;
//    }
//    private Optional<Long> getSprintProjectId(Long sprintId){
////        Optional<Sprint> sprintOptional = sprintRepository.findById(sprintId);
////        return sprintOptional.map(sprint -> sprint.getProject().getId());
//        return sprintRepository.findById(sprintId).map(sprint -> sprint.getProject().getId());
//    }
//    private Optional<Long> getIssueProjectId(Long issueId){
//        Optional<Issue> issueOptional = issueRepository.findById(issueId);
//        return issueOptional.map(issue -> issue.getProject().getId());
//    }
//
//    private Optional<Long> getProjectMemberProjectId(Long projectMemberId){
//        Optional<ProjectMember> projectMemberOptional = projectMemberRepository.findById(projectMemberId);
//        return projectMemberOptional.map(projectMember -> projectMember.getProject().getId());
//    }

}

// linting
