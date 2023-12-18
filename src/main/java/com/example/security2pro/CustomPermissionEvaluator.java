package com.example.security2pro;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.domain.model.auth.SecurityUser;
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

    private final
    ProjectMemberRepository projectMemberRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;


    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
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

        if(targetType.equals("project")){
            Optional<ProjectMember> projectMember = projectMemberRepository.findByUserIdAndProjectIdWithAuthorities(user.getId(),(Long)targetId);
            if(projectMember.isEmpty()){
                return false;
            }
            if(!projectMember.get().getProject().getId().equals(targetId)){
                return false;
            }
            return projectMember.get().getAuthorities().contains(Role.valueOf((String) permission));
        }


        if(targetType.equals("sprint")){
            Optional<Sprint> sprintOptional = sprintRepository.findById((Long)targetId);
            if(sprintOptional.isEmpty()){
                return false;
            }
            Optional<ProjectMember> projectMemberOptional= projectMemberRepository.findByUserIdAndProjectIdWithAuthorities(user.getId(),sprintOptional.get().getProject().getId());
            return projectMemberOptional.map(projectMember -> projectMember.getAuthorities().contains(Role.valueOf((String) permission))).orElse(false);
        }


        if(targetType.equals("issue")){
            Optional<Issue> issueOptional = issueRepository.findById((Long) targetId);
            if(issueOptional.isEmpty()){
                return false;
            }
            Optional<ProjectMember> projectMemberOptional= projectMemberRepository.findByUserIdAndProjectIdWithAuthorities(user.getId(),issueOptional.get().getProject().getId());
            return projectMemberOptional.map(projectMember -> projectMember.getAuthorities().contains(Role.valueOf((String) permission))).orElse(false);
        }

        return false;
    }
}

// linting
