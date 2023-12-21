package com.example.security2pro.controller;

import com.example.security2pro.dto.issue.IssueRelationDto;
import com.example.security2pro.service.IssueRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class IssueRelationController {

    private final IssueRelationService issueRelationService;


    @PostMapping("/related-issues/create")
    @PreAuthorize("hasPermission('IssueRelationDto','ROLE_PROJECT_LEAD') or hasPermission('IssueRelationDto','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public IssueRelationDto createIssueRelation(@Validated @RequestBody IssueRelationDto issueRelationDto){

        return issueRelationService.createIssueRelation(issueRelationDto);
    }

    @GetMapping("/issues/{issueId}/related-issues")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public Set<IssueRelationDto> getIssueRelations(@PathVariable Long issueId){

        return issueRelationService.findAllByAffectedIssueId(issueId);
    }

    @PostMapping("/related-issues/delete")
    @PreAuthorize("hasPermission('IssueRelationDto','ROLE_PROJECT_LEAD') or hasPermission('IssueRelationDto','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public void deleteIssueRelation(IssueRelationDto issueRelationDto){

        issueRelationService.deleteIssueRelation(issueRelationDto);
    }



}
