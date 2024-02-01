package com.example.bugtracker.controller;
import com.example.bugtracker.dto.issue.IssueSimpleDto;
import com.example.bugtracker.dto.sprint.SprintCreateDto;
import com.example.bugtracker.dto.sprint.SprintUpdateDto;
import com.example.bugtracker.dto.sprinthistory.SprintIssueHistoryDto;
import com.example.bugtracker.service.IssueService;
import com.example.bugtracker.service.HistoryService;
import com.example.bugtracker.service.SprintService;
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
