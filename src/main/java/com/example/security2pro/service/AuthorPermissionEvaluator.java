package com.example.security2pro.service;

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

        User user= ((SecurityUser) authentication.getPrincipal()).getUser();
        if(user==null) {return false;}

        Optional<Long> projectId = getProjectIdFromTypeAndId(targetType, targetId);
        if(projectId.isEmpty()) {return false;}

        Optional<ProjectMember> projectMemberOptional= projectMemberRepository.findByUsernameAndProjectIdWithAuthorities(user.getUsername(),projectId.get());
        if(projectMemberOptional.isEmpty()) {return false;}

        return user.getUsername().equals(getOriginalAuthor(targetType,targetId));
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
