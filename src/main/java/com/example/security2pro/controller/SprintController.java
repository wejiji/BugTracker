package com.example.security2pro.controller;
import com.example.security2pro.dto.issue.IssueSimpleDto;
import com.example.security2pro.dto.sprint.ActiveSprintCreateDto;
import com.example.security2pro.dto.sprint.ActiveSprintUpdateDto;
import com.example.security2pro.dto.sprinthistory.SprintIssueHistoryDto;
import com.example.security2pro.service.IssueService;
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

    private final IssueService issueService;




    //need to check projectId-check if sprint belongs to the project-
    //otherwise, problems with authorization - sprintDTO does not update project id field though
    // other project members have access the sprints of other projects


    @PostMapping("/projects/{projectId}/sprints/create")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public ActiveSprintUpdateDto createSprint(@PathVariable Long projectId, @Validated @RequestBody ActiveSprintCreateDto activeSprintCreateDto, BindingResult bindingResult) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}

        return sprintService.createSprint(projectId, activeSprintCreateDto);
        //issues can only be moved to sprint. issues are not updated
    }



    @GetMapping("/sprints/{sprintId}")
    @PreAuthorize("hasPermission(#sprintId,'sprint','ROLE_PROJECT_LEAD') or hasPermission(#sprintId,'sprint','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public ActiveSprintUpdateDto getSprint(@PathVariable Long sprintId){

        return sprintService.getSprintById(sprintId);
        // Is this for active sprint or history -  can be either archived or active. !
        // returns sprint with its issues.
    }


    @PostMapping("/sprints/{sprintId}/update")
    @PreAuthorize("hasPermission(#sprintId,'sprint','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public ActiveSprintUpdateDto updateSprint(@PathVariable Long sprintId, @Validated @RequestBody ActiveSprintUpdateDto activeSprintUpdateDto, BindingResult bindingResult) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}
        return sprintService.updateSprint(sprintId, activeSprintUpdateDto);
        //issues can only be moved to or out of sprint. issues are not updated
    }

    @PostMapping("/sprints/{sprintId}/delete")
    @PreAuthorize("hasPermission(#sprintId,'sprint','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void deleteSprint(@PathVariable Long sprintId){

        sprintService.deleteSprint(sprintId);
    }

    @PostMapping("/sprints/{sprintId}/end")
    @PreAuthorize("hasPermission(#sprintId,'sprint','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void handleEndingSprintIssues(
            @RequestParam boolean forceEndIssues, @PathVariable Long sprintId) {

        sprintService.endSprint(sprintId);

        issueService.handleEndingSprintIssues(sprintId, forceEndIssues);
    }


    @PreAuthorize("hasPermission(#sprintId,'sprint','ROLE_PROJECT_LEAD') or hasPermission(#sprintId,'sprint','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    @GetMapping("/sprints/{sprintId}/active-issues") //needs to send existing active issues List (status != done and not archived)
    public Set<IssueSimpleDto> activeSprintIssues(@PathVariable Long sprintId){

        return issueService.getActiveIssuesBySprintId(sprintId);
        //This page will not show the relationship between sprints and issues -!!!
    }

    @PreAuthorize("hasPermission(#sprintId,'sprint','ROLE_PROJECT_LEAD') or hasPermission(#sprintId,'sprint','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    @GetMapping("/archived-sprints/{sprintId}/issues")
    public Set<SprintIssueHistoryDto> sprintIssueHistory(@PathVariable Long sprintId){
        // use sprintIssueHistoryRepository  --
        return sprintService.getSprintIssueHistory(sprintId);
    }



}
