package com.example.security2pro.controller;

import com.example.security2pro.dto.issue.IssueSimpleDto;
import com.example.security2pro.dto.project.ProjectCreateDto;
import com.example.security2pro.dto.project.ProjectDto;
import com.example.security2pro.dto.project.ProjectSimpleUpdateDto;
import com.example.security2pro.dto.projectmember.ProjectMemberReturnDto;
import com.example.security2pro.dto.sprint.SprintUpdateDto;
import com.example.security2pro.service.HistoryService;
import com.example.security2pro.service.IssueService;
import com.example.security2pro.service.ProjectService;
import com.example.security2pro.service.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
@RestController
public class ProjectController {

    private final ProjectService projectService;

    private final IssueService issueService;

    private final SprintService sprintService;

    private final HistoryService historyService;

    @GetMapping("/projects")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEAM_LEAD')")
    public String check() {
        return "hello ";
    }

    @PostMapping("/projects")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEAM_LEAD')")
    public ProjectSimpleUpdateDto createProject(@Validated @RequestBody ProjectCreateDto projectCreateDto,
                                          BindingResult bindingResult,
                                          @CurrentSecurityContext(expression = "authentication")
                                             Authentication authentication ) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}

        String username = authentication.getName();
        return projectService.startProject(projectCreateDto, username);
    }

    @GetMapping("/projects/{projectId}")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public ProjectDto projectSprintsAndIssues(@PathVariable Long projectId) {

        return projectService.getProjectDetails(projectId);
    }

    @PostMapping("/projects/{projectId}")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public ProjectSimpleUpdateDto updateProjectSimple(@PathVariable Long projectId, @Validated ProjectSimpleUpdateDto projectSimpleUpdateDto) {

        return projectService.updateProject(projectId, projectSimpleUpdateDto);
    }

    @PostMapping("/projects/{projectId}/end")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void endProject(@PathVariable Long projectId, @RequestParam boolean forceEndIssues) {


        historyService.endProject(projectId, forceEndIssues);
    }


    @DeleteMapping("/projects/{projectId}")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void deleteProject(@PathVariable Long projectId) {

        projectService.deleteProject(projectId);
    }


    @GetMapping("/projects/{projectId}/project-members")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public List<ProjectMemberReturnDto> projectMembers(@PathVariable Long projectId){

        return projectService.findAllMemberByProjectIdWithAuthorities(projectId)
                .stream()
                .map(ProjectMemberReturnDto::new)
                .toList();
    }


    @GetMapping("/projects/{projectId}/active-sprints")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER')  or hasRole('ADMIN')")
    public Set<SprintUpdateDto> getActiveSprints(@PathVariable Long projectId){

        return sprintService.getActiveSprints(projectId);
    }

    @GetMapping("/projects/{projectId}/archived-sprints")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public Set<SprintUpdateDto> getArchivedSprints(@PathVariable Long projectId){

        return sprintService.getArchivedSprints(projectId);
    }


   @GetMapping("/projects/{projectId}/active-issues")
   @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
   public Set<IssueSimpleDto> activeIssues(@PathVariable Long projectId){

        return issueService.getActiveIssues(projectId);
    }




}
