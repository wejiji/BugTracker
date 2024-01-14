package com.example.security2pro.service;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.Project;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.dto.projectmember.ProjectMemberCreateDto;
import com.example.security2pro.dto.projectmember.ProjectMemberReturnDto;
import com.example.security2pro.repository.jpa_repository.ProjectMemberJpaRepository;
import com.example.security2pro.repository.jpa_repository.UserJpaRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectMemberRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectRepository;
import com.example.security2pro.repository.repository_interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.Set;


@RequiredArgsConstructor
@Service
@Transactional
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    private final UserRepository userRepository;

    private final ProjectRepository projectRepository;

    public ProjectMemberReturnDto createProjectMember(ProjectMemberCreateDto projectMemberCreateDto){
        Project project = projectRepository.getReferenceById(projectMemberCreateDto.getProjectId().get());

        Optional<User> userOptional = userRepository.findUserByUsername(projectMemberCreateDto.getUsername());
        if(userOptional.isEmpty()){
            throw new IllegalArgumentException("username not found within the project");
        }
        if(projectMemberRepository.findByUsernameAndProjectId(userOptional.get().getUsername(),project.getId()).isPresent()){
            throw new IllegalArgumentException("project member with the same user exists within the project with id"+projectMemberCreateDto.getProjectId());
        }

        Set<Role> authorities = projectMemberCreateDto.getAuthorities();
        if(!authorities.stream().allMatch(role -> role.name().startsWith("ROLE_PROJECT"))){
            throw new IllegalArgumentException("invalid project role");
        }

        ProjectMember projectMember =ProjectMember.createProjectMember(null,project,userOptional.get(), authorities);

        projectMember = projectMemberRepository.save(projectMember);

        return new ProjectMemberReturnDto(projectMember);
    }

    public void updateRole(Long projectMemberId , Set<Role> roleSet){
        if(roleSet.isEmpty())throw new IllegalArgumentException("invalid role");
        ProjectMember projectMember = projectMemberRepository.findByIdWithAuthorities(projectMemberId).get();
        projectMember.updateRole(roleSet);
    }

    public ProjectMemberReturnDto getReferenceById(Long projectMemberId){
        ProjectMember projectMember = projectMemberRepository.getReferenceById(projectMemberId);
        return new ProjectMemberReturnDto(projectMember);
    }

    public void deleteById(Long projectMemberId){
        if(projectMemberRepository.findById(projectMemberId).isEmpty()){
         throw new IllegalArgumentException("project member with id"+ projectMemberId +" does not exist");
        }
        projectMemberRepository.deleteById(projectMemberId);
    }


}
