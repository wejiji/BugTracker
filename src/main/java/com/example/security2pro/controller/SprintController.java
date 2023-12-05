package com.example.security2pro.controller;
import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.dto.ActiveSprintDto;
import com.example.security2pro.dto.ProjectDto;
import com.example.security2pro.dto.SprintUpdateDto;
import com.example.security2pro.service.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RequiredArgsConstructor
@RestController
public class SprintController {

    private final SprintService sprintService;


    //need to check projectId-check if sprint belongs to the project-
    //otherwise, problems with authorization - sprintDTO does not update project id field though
    // other project members have access the sprints of other projects


//    @GetMapping("/projects/{projectId}/sprints/create")
//    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
//    public List<IssueDto> getIssuesWithinProjectToCreateSprint(@PathVariable Long projectId){
//
//        return sprintService.findExistingIssuesToUpdateSprint(projectId);
//    }

    @GetMapping("/projects/{projectId}/sprints/create")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public ProjectDto getProjectToCreateSprint(@PathVariable Long projectId){

        return sprintService.getProjectToCreateSprint(projectId);
    }

    @GetMapping("/projects/{projectId}/sprints/{sprintId}/update")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public SprintUpdateDto getProjectToUpdateSprint(@PathVariable Long projectId, @PathVariable Long sprintId) throws BindException {

        return sprintService.getProjectToUpdateSprint(projectId,sprintId);
        //issues can only be moved to or out of sprint. issues are not updated
    }

    @PostMapping("/projects/{projectId}/sprints/create")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public ActiveSprintDto createSprint(@PathVariable Long projectId, @Validated @RequestBody ActiveSprintDto activeSprintDto, BindingResult bindingResult) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}

        return sprintService.createSprint(projectId, activeSprintDto);
        //issues can only be moved to sprint. issues are not updated
    }



    @PostMapping("/projects/{projectId}/sprints/{sprintId}/update")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public ActiveSprintDto updateSprint(@PathVariable Long projectId, @Validated @RequestBody ActiveSprintDto activeSprintDto, BindingResult bindingResult) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}
        return sprintService.updateSprint(projectId, activeSprintDto);
        //issues can only be moved to or out of sprint. issues are not updated
    }


    @GetMapping("/projects/{projectId}/sprints/{sprintId}")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public ActiveSprintDto getSprint(@PathVariable Long projectId, @PathVariable Long sprintId){

        return sprintService.getSprintById(sprintId);
        // Is this for active sprint or history -  can be either archived or active. !
        // returns sprint with its issues.
    }

    @PostMapping("/projects/{projectId}/sprints/{sprintId}/delete")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void deleteSprint(@PathVariable Long projectId, @PathVariable Long sprintId){

        sprintService.deleteSprint(projectId, sprintId);
        //error will be thrown if the sprint already ended
    }

    @GetMapping("/projects/{projectId}/sprints/{sprintId}/end")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public ActiveSprintDto getEndingSprintIssues(@PathVariable Long projectId, @PathVariable Long sprintId){

         return sprintService.getActiveSprintAndIssuesToEnd(projectId, sprintId);
        //error will be thrown if the sprint already ended
    }

    @PostMapping("/projects/{projectId}/sprints/{sprintId}/end")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void handleEndingSprintIssues(@PathVariable Long projectId, @PathVariable Long sprintId, @Validated @RequestBody ActiveSprintDto activeSprintDto, BindingResult bindingResult) throws BindException {

        // any other modifications than just {changing issues' status to Done and the sprint's status to archived} are not allowed
        //( only status will change if the issue is ending with the sprint in case its status is not 'DONE'.)

        // the active issues with status 'DONE' within the sprint will become archived!!!!!!
        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}

        //check status and if it is "DONE" then set complete to true when creating SprintIssueHistories in Service
        sprintService.endSprint(projectId,sprintId, activeSprintDto);
    }





}
