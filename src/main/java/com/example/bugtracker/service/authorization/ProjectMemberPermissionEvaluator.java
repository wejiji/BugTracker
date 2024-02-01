package com.example.bugtracker.service.authorization;

import com.example.bugtracker.authentication.jwt.ProjectRoles;
import com.example.bugtracker.authentication.jwt.UserAndProjectRoleAuthentication;
import com.example.bugtracker.domain.enums.ProjectMemberRole;
import com.example.bugtracker.dto.issue.authorization.CreateDtoWithProjectId;

import com.example.bugtracker.repository.repository_interfaces.IssueRepository;
import com.example.bugtracker.repository.repository_interfaces.ProjectMemberRepository;
import com.example.bugtracker.repository.repository_interfaces.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;


@RequiredArgsConstructor
@Component
public class ProjectMemberPermissionEvaluator implements CustomPermissionEvaluator {

    /*
     * Authorizes access to resources only for users who are members of the project to which the resource belongs.
     * This evaluator checks the 'ProjectMemberRole' of the passed 'Authentication,'
     *  which should be an implementation class of the 'UserAndProjectRoleAuthentication' interface.
     * Currently, only 'JwtAuthentication' implements this interface in the app.
     */

    private final ProjectMemberRepository projectMemberRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;

    public boolean supports(Object object) {
        return object instanceof CreateDtoWithProjectId;
    }

    public boolean supports(String targetType) {
        return targetType.equals("issue")
               || targetType.equals("sprint")
               || targetType.equals("project")
               || targetType.equals("projectMember");
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || (targetDomainObject == null) || (permission == null)) {
            return false;
        }

        Optional<Long> projectIdOptional = getProjectIdFromDto(targetDomainObject);
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
                .filter(projectRoles -> projectRoles.getProjectId().equals(projectIdOptional.get())
                                        && projectRoles.getRoles().contains(ProjectMemberRole.valueOf((String) permission)))
                .findAny();

        return matchedRole.isPresent();
    }


    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if ((authentication == null) || (targetType == null) || !(permission instanceof String)) {
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
                .filter(projectRoles -> projectRoles.getProjectId().equals(projectIdOptional.get())
                                        && projectRoles.getRoles().contains(ProjectMemberRole.valueOf((String) permission)))
                .findAny();

        return matchedRole.isPresent();
    }

    private Optional<Long> getProjectIdFromTypeAndId(String targetType, Serializable targetId) {
        Optional<Long> projectId = Optional.empty();

        if (targetType.equals("project")) {
            return Optional.of((Long) targetId);
        }
        if (targetType.equals("sprint")) {
            return sprintRepository.findById((Long) targetId).map(sprint -> sprint.getProject().getId());
        }
        if (targetType.equals("issue")) {
            return issueRepository.findById((Long) targetId).map(issue -> issue.getProject().getId());
        }
        if (targetType.equals("projectMember")) {
            return projectMemberRepository.findById((Long) targetId).map(projectMember -> projectMember.getProject().getId());
        }
        return projectId;
    }


    private Optional<Long> getProjectIdFromDto(Object targetDomainObject) {
        if (targetDomainObject instanceof CreateDtoWithProjectId createDtoWithProjectId) {
            return createDtoWithProjectId.getProjectId(); // always not null
        }
        return Optional.empty();
    }

}
