package com.example.security2pro.controller;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.dto.projectmember.ProjectMemberCreateDto;
import com.example.security2pro.dto.projectmember.ProjectMemberReturnDto;
import com.example.security2pro.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@RestController
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

//    @PostMapping("/project-members/create")
//    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
//    public ProjectMemberDto addProjectMember(@PathVariable Long projectId, @Validated @RequestBody ProjectMemberCreateDto projectMemberCreateDto, BindingResult bindingResult) throws BindException {
//
//        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}
//
//        return projectService.addProjectMember(projectId, projectMemberCreateDto);
//    }

    @PostMapping("/project-members/create")
    @PreAuthorize("hasPermission('projectMemberCreateDto','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public ProjectMemberReturnDto createProjectMember(@Validated @RequestBody ProjectMemberCreateDto projectMemberCreateDto, BindingResult bindingResult) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}

        return projectMemberService.createProjectMember(projectMemberCreateDto);
    }


    @PostMapping("/project-members/{projectMemberId}/update-role")
    @PreAuthorize("hasPermission(#projectMemberId,'projectMember','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void updateProjectMemberAuthority(@PathVariable Long projectMemberId, @RequestBody Set<Role> roleSet) {

        projectMemberService.updateRole(projectMemberId,roleSet);
    }

    @GetMapping("/project-members/{projectMemberId}")
    @PreAuthorize("hasPermission(#projectMemberId,'projectMember','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public ProjectMemberReturnDto getProjectMemberWithAuthority(@PathVariable Long projectMemberId) {

        return projectMemberService.getReferenceById(projectMemberId);
    }

    @PostMapping("/project-members/{projectMemberId}/delete")
    @PreAuthorize("hasPermission(#projectMemberId,'projectMember','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void deleteProjectMember(@PathVariable Long projectMemberId) {

        projectMemberService.deleteById(projectMemberId);
    }




}
