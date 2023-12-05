package com.example.security2pro.controller;

import com.example.security2pro.domain.model.*;

import com.example.security2pro.dto.*;
import com.example.security2pro.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @GetMapping("users/{userId}/active-issues")
    public Set<IssueDto> userIssues(@PathVariable Long userId){

        return issueService.getUserIssues(userId);
    }


    @GetMapping("projects/{projectId}/issues/create") //needs to send existing active issues List (status != done and not archived)
    public ProjectDto existingActiveIssues(@PathVariable Long projectId){

        return issueService.getProjectDataToCreateIssue(projectId);
        //This page will not show the relationship between sprints and issues -!!!
    }


    @PostMapping("projects/{projectId}/issues/create")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public IssueDto createIssue(@PathVariable Long projectId, @Validated @RequestBody IssueDto issueDto
                                       , BindingResult bindingResult) throws BindException, InvocationTargetException, IllegalAccessException {
        // the below is for validation logic. If type mismatch or Other deserialization exception happens,
        // It will throw an error and this controller is not called......
        //Go to rest controller advice to take care of this.
        //also it looks like 403 is caused by this error which triggers authenticationentrypoint !...
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }
        return issueService.createIssueFromDto(projectId, issueDto);
    }


    @GetMapping("projects/{projectId}/issues/{issueId}/update")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public IssueUpdateDto getIssueToUpdate(@PathVariable Long projectId, @PathVariable Long issueId) throws BindException {


        return issueService.getIssueAndProjectDataToUpdate(projectId,issueId);
    }

    @PostMapping("projects/{projectId}/issues/{issueId}/update")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public IssueDto updateIssue(@PathVariable Long projectId, @Validated @RequestBody IssueDto issueDto,
                                BindingResult bindingResult) throws BindException, InvocationTargetException, IllegalAccessException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}
        return issueService.updateIssueFromDto(projectId,issueDto);
    }


    @GetMapping("projects/{projectId}/issues/{issueId}")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public IssueDto getIssueDetails(@PathVariable Long projectId, @PathVariable Long issueId) throws InvocationTargetException, IllegalAccessException {

        return issueService.getIssueWithDetails(issueId);
    }


    @PostMapping("projects/{projectId}/issues/{issueId}/delete")
    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public void deleteIssue(@PathVariable Long projectId, @PathVariable Long issueId){
        Issue foundIssue = issueService.getReferenceById(issueId);

        issueService.deleteById(issueId);
    }




}
