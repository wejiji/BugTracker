package com.example.bugtracker.service;

import com.example.bugtracker.domain.enums.ProjectMemberRole;
import com.example.bugtracker.exception.directmessageconcretes.InvalidUserArgumentException;
import com.example.bugtracker.exception.directmessageconcretes.NotExistException;
import com.example.bugtracker.exception.directmessageconcretes.ProjectMemberInvalidRoleArgumentException;
import com.example.bugtracker.domain.model.Project;
import com.example.bugtracker.domain.model.ProjectMember;
import com.example.bugtracker.domain.model.User;
import com.example.bugtracker.dto.projectmember.ProjectMemberCreateDto;
import com.example.bugtracker.dto.projectmember.ProjectMemberReturnDto;
import com.example.bugtracker.repository.repository_interfaces.ProjectMemberRepository;
import com.example.bugtracker.repository.repository_interfaces.ProjectRepository;
import com.example.bugtracker.repository.repository_interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.Set;


@RequiredArgsConstructor
@Service
@Transactional
public class ProjectMemberService {

    /*
     * While calling the 'save' method is unnecessary when the entity is already in the cache,
     * as dirty checking can automatically update modified fields, it is called nevertheless for explicitness.
     */

    private final ProjectMemberRepository projectMemberRepository;

    private final UserRepository userRepository;

    private final ProjectRepository projectRepository;

    /**
     * Creates and saves a new 'ProjectMember'.
     * Returns a 'ProjectMemberReturnDto' with the auto-generated id.
     *
     * @param projectMemberCreateDto The data required for creating a project member.
     *                               The id of the project is expected to be verified beforehand.
     * @return A 'ProjectMemberReturnDto' representing the newly created project member.
     * @throws NotExistException If the specified username is not found within the project.
     * @throws InvalidUserArgumentException If a project member with the same user already exists within the project.
     * @throws ProjectMemberInvalidRoleArgumentException If an invalid project role is provided.
     */
    public ProjectMemberReturnDto createProjectMember(ProjectMemberCreateDto projectMemberCreateDto){
        Project project = projectRepository
                .getReferenceById(projectMemberCreateDto.getProjectId().get());

        Optional<User> userOptional = userRepository
                .findUserByUsername(projectMemberCreateDto.getUsername());

        if(userOptional.isEmpty()){
            throw new NotExistException("username not found within the project");
        }
        if(projectMemberRepository.findByUsernameAndProjectId(
                userOptional.get().getUsername(),project.getId())
                .isPresent()){
            throw new InvalidUserArgumentException(
                    "project member with the same user exists "
                    + "within the project with id"+projectMemberCreateDto.getProjectId());
        }

        Set<ProjectMemberRole> authorities = projectMemberCreateDto.getAuthorities();
        if(!authorities.stream().allMatch(role -> role.name().startsWith("ROLE_PROJECT"))){
            throw new ProjectMemberInvalidRoleArgumentException("invalid project role");
        }

        ProjectMember projectMember
                =ProjectMember.createProjectMember(
                        null,project,userOptional.get(), authorities);
        projectMember = projectMemberRepository.save(projectMember);
        return new ProjectMemberReturnDto(projectMember);
    }

    /**
     * Updates the roles of a project member identified by the provided id.
     *
     * @param projectMemberId The id of the project member to be updated.
     *                        Expected to be verified for existence within the project beforehand.
     * @param roleSet The set of new roles to be assigned to the project member.
     * @throws ProjectMemberInvalidRoleArgumentException If the provided role set is empty or invalid.
     */
    public void updateRole(Long projectMemberId , Set<ProjectMemberRole> roleSet){

        if(roleSet.isEmpty())throw new ProjectMemberInvalidRoleArgumentException("invalid role");
        ProjectMember projectMember = projectMemberRepository.findByIdWithAuthorities(projectMemberId).get();
        projectMember.updateRole(roleSet);
        projectMemberRepository.save(projectMember);
    }

    public ProjectMemberReturnDto getReferenceById(Long projectMemberId){
        // The 'projectMemberId' is expected to be verified for existence within the project beforehand.
        ProjectMember projectMember = projectMemberRepository.getReferenceById(projectMemberId);
        return new ProjectMemberReturnDto(projectMember);
    }

    public void deleteById(Long projectMemberId){
        projectMemberRepository.deleteById(projectMemberId);
    }


}
