package com.example.security2pro.controller;
import com.example.security2pro.dto.issue.IssueSimpleDto;
import com.example.security2pro.dto.sprint.SprintCreateDto;
import com.example.security2pro.dto.sprint.SprintUpdateDto;
import com.example.security2pro.dto.sprinthistory.SprintIssueHistoryDto;
import com.example.security2pro.service.IssueService;
import com.example.security2pro.service.HistoryService;
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

    private final HistoryService historyService;

    //need to check projectId-check if sprint belongs to the project-
    //otherwise, problems with authorization - sprintDTO does not update project id field though
    // other project members have access the sprints of other projects


    @PostMapping("/sprints")
    @PreAuthorize("hasPermission('sprintCreateDto','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public SprintUpdateDto createSprint(@PathVariable Long projectId, @Validated @RequestBody SprintCreateDto sprintCreateDto, BindingResult bindingResult) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}

        return sprintService.createSprintFromDto(projectId, sprintCreateDto);
    }



    @GetMapping("/sprints/{sprintId}")
    @PreAuthorize("hasPermission(#sprintId,'sprint','ROLE_PROJECT_LEAD') or hasPermission(#sprintId,'sprint','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public SprintUpdateDto getSprint(@PathVariable Long sprintId){

        return sprintService.getSprintById(sprintId);
        // Is this for active sprint or history -  can be either archived or active. !
        // returns sprint with its issues.
    }


    @PostMapping("/sprints/{sprintId}")
    @PreAuthorize("hasPermission(#sprintId,'sprint','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public SprintUpdateDto updateSprint(@PathVariable Long sprintId, @Validated @RequestBody SprintUpdateDto sprintUpdateDto, BindingResult bindingResult) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}
        return sprintService.updateSprintFromDto(sprintId, sprintUpdateDto);

    }

    @DeleteMapping("/sprints/{sprintId}")
    @PreAuthorize("hasPermission(#sprintId,'sprint','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void deleteSprint(@PathVariable Long sprintId){

        sprintService.deleteSprint(sprintId);
    }

    @PostMapping("/sprints/{sprintId}/end")
    @PreAuthorize("hasPermission(#sprintId,'sprint','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void handleEndingSprintIssues(
            @RequestParam boolean forceEndIssues, @PathVariable Long sprintId) {

        historyService.endSprintAndSprintIssues(sprintId, forceEndIssues);
    }


   @GetMapping("/sprints/{sprintId}/issues") //needs to send existing active issues List (status != done and not archived)
   @PreAuthorize("hasPermission(#sprintId,'sprint','ROLE_PROJECT_LEAD') or hasPermission(#sprintId,'sprint','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
   public Set<IssueSimpleDto> activeSprintIssues(@PathVariable Long sprintId){

        return issueService.getActiveIssuesBySprintId(sprintId);

    }

    @GetMapping("/archived-sprints/{sprintId}/issues")
    @PreAuthorize("hasPermission(#sprintId,'sprint','ROLE_PROJECT_LEAD') or hasPermission(#sprintId,'sprint','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public Set<SprintIssueHistoryDto> sprintIssueHistory(@PathVariable Long sprintId){

        return sprintService.getSprintIssueHistory(sprintId);
    }



}
