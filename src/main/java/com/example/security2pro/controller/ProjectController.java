package com.example.security2pro.controller;


import com.example.security2pro.domain.model.*;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.dto.project.ProjectCreateDto;
import com.example.security2pro.dto.project.ProjectDto;
import com.example.security2pro.dto.projectmember.ProjectMemberCreateDto;
import com.example.security2pro.dto.projectmember.ProjectMemberDto;
import com.example.security2pro.service.IssueService;
import com.example.security2pro.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
public class ProjectController {

    private final ProjectService projectService;

    private final IssueService issueService;

    //authentication and authorization needs to be tested further


    // authorization tested on this end point (/create)
    //tested
    @PostMapping("/projects/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEAM_LEAD')")
    public ProjectCreateDto createProject(@Validated @RequestBody ProjectCreateDto projectCreateDto,
                                          BindingResult bindingResult,
                                          @CurrentSecurityContext(expression = "authentication")
                                             Authentication authentication ) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}

        User user= ((SecurityUser)authentication.getPrincipal()).getUser();
        Project newProject = projectService.startProject(projectCreateDto, user);
        return new ProjectCreateDto(newProject);
    }


    //tested
    @GetMapping("/projects/{projectId}")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public ProjectDto projectSprintsAndIssues(@PathVariable Long projectId) {

        return projectService.getProjectDetails(projectId);
    }


    //tested
    @GetMapping("/projects/{projectId}/project-members")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public List<ProjectMemberDto> projectMembers(@PathVariable Long projectId){

        return projectService.findAllMemberByProjectIdWithUser(projectId).stream().map(ProjectMemberDto::new).collect(Collectors.toList());
    }

    //tested
    @PostMapping("/projects/{projectId}/project-members/add")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public ProjectMemberDto addProjectMember(@PathVariable Long projectId, @Validated @RequestBody ProjectMemberCreateDto projectMemberCreateDto, BindingResult bindingResult) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}

        return projectService.addProjectMember(projectId, projectMemberCreateDto);

    }

    @PostMapping("/projects/{projectId}/end")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void endProject(Long projectId, @RequestParam boolean forceEndIssues) {

        issueService.endProject(projectId, forceEndIssues);
    }


//    @PostMapping("/projects/{projectId}/delete")
//    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
//    public void deleteProject(Long projectId) {
//
//        sprintService.deleteProject(projectId);
//    }


}
