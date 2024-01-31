package com.example.security2pro.controller;

import com.example.security2pro.dto.issue.*;
import com.example.security2pro.dto.issue.onetomany.IssueHistoryDto;
import com.example.security2pro.dto.issue.onetomany.IssueRelationDto;
import com.example.security2pro.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;
    private final IssueHistoryService issueHistoryService;

    @GetMapping("/issues/{issueId}")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public IssueUpdateDto getIssue(@PathVariable Long issueId){

        return issueService.getIssueSimple(issueId);
    }


    @GetMapping("/issues")
    @PreAuthorize("#username==authentication.principal.username or hasRole('ADMIN')")
    public Set<IssueSimpleDto> userIssues(@RequestParam String username){
        return issueService.getUserIssues(username);
    }


    @DeleteMapping("/issues/{issueId}")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public void deleteIssue(@PathVariable Long issueId){

        issueService.deleteById(issueId);
    }


    @GetMapping("/issues/{issueId}/history")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public List<IssueHistoryDto> getIssueHistory(@PathVariable Long issueId){

        return issueHistoryService.getIssueHistories(issueId);
    }


    @PostMapping("/issues")
    @PreAuthorize("hasPermission('IssueCreateDto','ROLE_PROJECT_LEAD') or hasPermission('IssueCreateDto','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public IssueUpdateDto createSimpleIssue(@Validated @RequestBody IssueCreateDto issueCreateDto
            , BindingResult bindingResult) throws BindException{

        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }
        return issueService.createIssueFromSimpleDto(issueCreateDto);
    }

    @PostMapping("/issues/{issueId}")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public IssueUpdateDto updateSimpleIssue(@PathVariable Long issueId , @Validated @RequestBody IssueUpdateDto issueUpdateDto,
                                            BindingResult bindingResult) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}

        return issueService.updateIssueFromSimpleDto(issueUpdateDto);
    }



    @GetMapping("/issues/{affectedIssueId}/related-issues")
    @PreAuthorize("hasPermission(#affectedIssueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#affectedIssueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public Set<IssueRelationDto> getIssueRelations(@PathVariable Long affectedIssueId){

        return issueService.findAllByAffectedIssueId(affectedIssueId);
    }

    @PostMapping("/issues/{affectedIssueId}/related-issues")
    @PreAuthorize("hasPermission(#affectedIssueId,'issue','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public IssueRelationDto createOrUpdateIssueRelation(@PathVariable Long affectedIssueId, @Validated @RequestBody IssueRelationDto issueRelationDto){

        return issueService.createOrUpdateIssueRelation(affectedIssueId ,issueRelationDto);
    }


    @DeleteMapping("/issues/{affectedIssueId}/related-issues/{causeIssueId}")
    @PreAuthorize("hasPermission(#affectedIssueId,'issue','ROLE_PROJECT_LEAD') or hasRole('ADMIN')")
    public void deleteIssueRelation(@PathVariable Long affectedIssueId, @PathVariable Long causeIssueId){

        issueService.deleteIssueRelation(affectedIssueId, causeIssueId);
    }


}
