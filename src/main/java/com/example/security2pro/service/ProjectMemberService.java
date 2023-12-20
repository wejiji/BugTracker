package com.example.security2pro.service;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.Project;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.dto.projectmember.ProjectMemberCreateDto;
import com.example.security2pro.dto.projectmember.ProjectMemberReturnDto;
import com.example.security2pro.repository.ProjectMemberRepository;
import com.example.security2pro.repository.UserRepository;
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

    private final ProjectService projectService;

    public ProjectMemberReturnDto createProjectMember(ProjectMemberCreateDto projectMemberCreateDto){
        Project project = projectService.getReferenceById(projectMemberCreateDto.getProjectId().get());

        Optional<User> userOptional = userRepository.findUserByUsername(projectMemberCreateDto.getUsername());
        if(userOptional.isEmpty()){
            throw new IllegalArgumentException("username not found within the project");
        }
        if(projectMemberRepository.findByUsernameAndProjectId(userOptional.get().getUsername(),project.getId()).isPresent()){
            throw new IllegalArgumentException("project member with the same user exists within the project with id"+projectMemberCreateDto.getProjectId());
        }
        ProjectMember projectMember =ProjectMember.createProjectMember(project,userOptional.get(), projectMemberCreateDto.getAuthorities());

        projectMemberRepository.save(projectMember);

        return new ProjectMemberReturnDto(projectMember);
    }

    public void updateRole(Long projectMemberId , Set<Role> roleSet){
        ProjectMember projectMember = projectMemberRepository.findByIdWithAuthorities(projectMemberId).get();
        Set<Role> updatedRole =projectMember.updateRole(roleSet);
        if(updatedRole.isEmpty()){
            throw new IllegalArgumentException("invalid project role");
        }
    }

    public ProjectMemberReturnDto getReferenceById(Long projectMemberId){
        ProjectMember projectMember = projectMemberRepository.getReferenceById(projectMemberId);
        return new ProjectMemberReturnDto(projectMember);
    }

    public void deleteById(Long projectMemberId){
        projectMemberRepository.deleteById(projectMemberId);
    }


}
