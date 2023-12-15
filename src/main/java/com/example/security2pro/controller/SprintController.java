package com.example.security2pro.controller;
import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.dto.sprint.ActiveSprintUpdateDto;
import com.example.security2pro.dto.project.ProjectDto;
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


    @PostMapping("/projects/{projectId}/sprints/create")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public ActiveSprintUpdateDto createSprint(@PathVariable Long projectId, @Validated @RequestBody ActiveSprintUpdateDto activeSprintUpdateDto, BindingResult bindingResult) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}

        return sprintService.createSprint(projectId, activeSprintUpdateDto);
        //issues can only be moved to sprint. issues are not updated
    }

    @GetMapping("/projects/{projectId}/sprints/{sprintId}")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public ActiveSprintUpdateDto getSprint(@PathVariable Long projectId, @PathVariable Long sprintId){

        return sprintService.getSprintById(sprintId);
        // Is this for active sprint or history -  can be either archived or active. !
        // returns sprint with its issues.
    }

    @GetMapping("/projects/{projectId}/active-sprints")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public Set<ActiveSprintUpdateDto> getActiveSprints(@PathVariable Long projectId){

        return sprintService.getActiveSprints(projectId);
    }

    @GetMapping("/projects/{projectId}/archived-sprints")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public Set<ActiveSprintUpdateDto> getArchivedSprints(@PathVariable Long projectId){

        return sprintService.getArchivedSprints(projectId);
    }

    @PostMapping("/projects/{projectId}/sprints/{sprintId}/update")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public ActiveSprintUpdateDto updateSprint(@PathVariable Long projectId, @Validated @RequestBody ActiveSprintUpdateDto activeSprintUpdateDto, BindingResult bindingResult) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}
        return sprintService.updateSprint(projectId, activeSprintUpdateDto);
        //issues can only be moved to or out of sprint. issues are not updated
    }

    @PostMapping("/projects/{projectId}/sprints/{sprintId}/end")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void endSprint(@PathVariable Long projectId, @PathVariable Long sprintId, @Validated @RequestBody ActiveSprintUpdateDto activeSprintUpdateDto, BindingResult bindingResult) throws BindException {
        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}

        sprintService.endSprint(projectId,sprintId);
    }

    @PostMapping("/projects/{projectId}/sprints/{sprintId}/delete")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void deleteSprint(@PathVariable Long projectId, @PathVariable Long sprintId){

        sprintService.deleteSprint(projectId, sprintId);
    }



}
