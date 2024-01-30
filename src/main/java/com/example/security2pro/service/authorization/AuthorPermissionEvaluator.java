package com.example.security2pro.service.authorization;

import com.example.security2pro.authentication.jwt.ProjectRoles;
import com.example.security2pro.authentication.jwt.UserAndProjectRoleAuthentication;
import com.example.security2pro.domain.model.issue.Comment;
import com.example.security2pro.domain.model.BaseEntity;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.repository.repository_interfaces.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class AuthorPermissionEvaluator implements CustomPermissionEvaluator {

    /*
     * Provides authorization based on whether the 'Principal' of the 'Authentication' is
     * the owner (creator) of the resources with restricted access.
     * The owner must also be a 'ProjectMember' of the 'Project' to which the resource belongs.
     * Therefore, it checks for both the resource's ownership and 'ProjectMemberRole' inclusion in the 'Authentication,'
     *  which should be an implementation of the 'UserAndProjectRoleAuthentication' interface.
     * Currently, only 'JwtAuthentication' implements this interface in the app.
     */

    private final CommentRepository commentRepository;

    private static final String COMMENT = "comment";

    @Override
    public boolean supports(Object object) {
        return false;
    }

    @Override
    public boolean supports(String targetType) {
        return targetType.equals(COMMENT);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if ((authentication == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }

        String username = ((SecurityUser) authentication.getPrincipal()).getUsername();
        if (username == null) {
            return false;
        }

        Optional<Long> projectIdOptional = getProjectIdFromTypeAndId(targetType, targetId);
        if (projectIdOptional.isEmpty()) {
            return false;
        }

        UserAndProjectRoleAuthentication userAndProjectRoleAuthentication =
                (UserAndProjectRoleAuthentication) authentication;

        Set<ProjectRoles> projectRolesSet = userAndProjectRoleAuthentication.getProjectRoles();
        if (projectRolesSet == null || projectRolesSet.isEmpty()) {
            return false;
        }

        Optional<ProjectRoles> matchedRole = userAndProjectRoleAuthentication.getProjectRoles().stream()
                .filter(projectRoles -> projectRoles.getProjectId().equals(projectIdOptional.get()))
                .findAny();
        if (matchedRole.isEmpty()) {
            return false;
        }

        return username.equals(getOriginalAuthor(targetType, targetId));
    }


    private String getOriginalAuthor(String targetType, Serializable targetId) {
        if (targetType.startsWith(COMMENT)) {
            Optional<Comment> commentOptional = commentRepository.findById((Long) targetId);
            return commentOptional.map(BaseEntity::getCreatedBy).orElse(null);
        }
        return null;
    }


    private Optional<Long> getProjectIdFromTypeAndId(String targetType, Serializable targetId) {
        Optional<Long> projectId = Optional.empty();

        if (targetType.equals(COMMENT)) {
            Optional<Comment> activityOptional = commentRepository.findById((Long) targetId);
            return activityOptional.map(activity -> Optional.of(activity.getIssue().getProject().getId())).orElse(null);
        }
        return projectId;
    }
}
