package com.example.security2pro.service;

import com.example.security2pro.authentication.newjwt.ProjectRoles;
import com.example.security2pro.authentication.newjwt.UserAndProjectRoleAuthentication;
import com.example.security2pro.domain.enums.refactoring.ProjectMemberRole;
import com.example.security2pro.domain.model.Comment;
import com.example.security2pro.domain.model.BaseEntity;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.repository.repository_interfaces.CommentRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class AuthorPermissionEvaluator implements CustomPermissionEvaluator{

    private final CommentRepository commentRepository;

    private final ProjectMemberRepository projectMemberRepository;

    @Override
    public boolean supports(Object object) {
        return false;
    }

    @Override
    public boolean supports(String targetType) {
        return targetType.equals("comment");
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if ((authentication == null) || (targetType == null) || !(permission instanceof String)){return false;}

        String username= ((SecurityUser) authentication.getPrincipal()).getUsername();
        if(username==null) {return false;}

        Optional<Long> projectIdOptional = getProjectIdFromTypeAndId(targetType, targetId);
        if(projectIdOptional.isEmpty()) {return false;}

        UserAndProjectRoleAuthentication userAndProjectRoleAuthentication =
                (UserAndProjectRoleAuthentication) authentication;

        Set<ProjectRoles> projectRolesSet = userAndProjectRoleAuthentication.getProjectRoles();
        if(projectRolesSet==null || projectRolesSet.isEmpty()){
            return false;
        }

        Optional<ProjectRoles> matchedRole= userAndProjectRoleAuthentication.getProjectRoles().stream()
                .filter(projectRoles -> projectRoles.getProjectId().equals(projectIdOptional.get()))
                .findAny();
        if(matchedRole.isEmpty()) {
            return false;
        }

        return username.equals(getOriginalAuthor(targetType,targetId));
    }


    private String getOriginalAuthor(String targetType, Serializable targetId){
        if(targetType.startsWith("comment")){
            Optional<Comment> commentOptional = commentRepository.findById((Long)targetId);
            return commentOptional.map(BaseEntity::getCreatedBy).orElse(null);
        }
        return null;
    }


    private Optional<Long> getProjectIdFromTypeAndId(String targetType, Serializable targetId){
        Optional<Long> projectId = Optional.empty();

        if(targetType.equals("comment")){
            Optional<Comment> activityOptional = commentRepository.findById((Long)targetId);
            return activityOptional.map(activity -> Optional.of(activity.getIssue().getProject().getId())).orElse(null);
        }
        return projectId;
    }
}
