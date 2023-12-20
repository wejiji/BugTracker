package com.example.security2pro;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.dto.issue.CreateDtoWithProjectId;
import com.example.security2pro.repository.IssueRepository;
import com.example.security2pro.repository.ProjectMemberRepository;
import com.example.security2pro.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;


import java.io.Serializable;
import java.util.Optional;


@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final ProjectMemberRepository projectMemberRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;


    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || (targetDomainObject == null) || (permission==null)){
            return false;
        }

        User user= ((SecurityUser) authentication.getPrincipal()).getUser();
        if(user==null) return false;

        if(user.getAuthorities().contains(Role.valueOf((String)permission))){
            return true;
        }

        Optional<Long> projectId = Optional.empty();

        if(targetDomainObject instanceof CreateDtoWithProjectId){
            CreateDtoWithProjectId createDtoWithProjectId = (CreateDtoWithProjectId) targetDomainObject;
            projectId = createDtoWithProjectId.getProjectId(); // always not null
        }

        if(projectId.isEmpty()) return false;
        Optional<ProjectMember> projectMember = projectMemberRepository.findByUsernameAndProjectIdWithAuthorities(user.getUsername(),projectId.get());
        return projectMember.map(member -> member.getAuthorities().contains(Role.valueOf((String) permission))).orElse(false);
    }


    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if ((authentication == null) || (targetType == null) || !(permission instanceof String)){
            return false;
        }

        User user= ((SecurityUser) authentication.getPrincipal()).getUser();
        if(user==null) return false;

        if(user.getAuthorities().contains(Role.valueOf((String)permission))){
            return true;
        }

        Optional<Long> projectId = getProjectId(targetType, targetId);

        if(projectId.isEmpty()) return false;
        Optional<ProjectMember> projectMemberOptional= projectMemberRepository.findByUsernameAndProjectIdWithAuthorities(user.getUsername(),projectId.get());
        return projectMemberOptional.map(projectMember -> projectMember.getAuthorities().contains(Role.valueOf((String) permission))).orElse(false);
    }

    private Optional<Long> getProjectId(String targetType, Serializable targetId){
        Optional<Long> projectId = Optional.empty();

        if(targetType.equals("project")){
            return Optional.of((Long)targetId);
        }
        if(targetType.equals("sprint")){
            return getSprintProjectId((Long) targetId);
        }
        if(targetType.equals("issue")){
            return getIssueProjectId((Long) targetId);
        }
        if(targetType.equals("projectMember")){
            return getProjectMemberProjectId((Long) targetId);
        }
        return projectId;
    }

    private Optional<Long> getSprintProjectId(Long sprintId){
        Optional<Sprint> sprintOptional = sprintRepository.findById(sprintId);
        return sprintOptional.map(sprint -> sprint.getProject().getId());
//        if(sprintOptional.isEmpty()){
//            return Optional.empty();
//        }
//        return Optional.of(sprintOptional.get().getProject().getId());
    }
    private Optional<Long> getIssueProjectId(Long issueId){
        Optional<Issue> issueOptional = issueRepository.findById(issueId);
        return issueOptional.map(issue -> issue.getProject().getId());
    }

    private Optional<Long> getProjectMemberProjectId(Long projectMemberId){
        Optional<ProjectMember> projectMemberOptional = projectMemberRepository.findById(projectMemberId);
        return projectMemberOptional.map(projectMember -> projectMember.getProject().getId());
    }

}

// linting
