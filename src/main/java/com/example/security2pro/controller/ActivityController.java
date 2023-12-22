package com.example.security2pro.controller;

import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.dto.issue.ActivityCreateDto;
import com.example.security2pro.dto.issue.ActivityDto;
import com.example.security2pro.dto.issue.ActivityDtoNew;
import com.example.security2pro.dto.issue.IssueRelationDto;
import com.example.security2pro.service.ActivityService;
import com.example.security2pro.service.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@Transactional
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping("/activities/create")
    @PreAuthorize("hasPermission('activityCreateDto','ROLE_PROJECT_LEAD') or hasPermission('activityCreateDto','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public ActivityDtoNew createActivity(@Validated @RequestBody ActivityCreateDto activityCreateDto){

        return activityService.createActivity(activityCreateDto);
    }

    @GetMapping("/issues/{issueId}/activities")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public Set<ActivityDtoNew> getIssueActivities(@PathVariable Long issueId){

        return activityService.findAllByIssueId(issueId);
    }

    @PostMapping("/activities/{activityId}/delete")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#activityId,'activity','author') or hasRole('ADMIN')")
    public void deleteActivity(@PathVariable Long activityId, @AuthenticationPrincipal SecurityUser principal){

        activityService.deleteActivity(activityId, principal);
    }



}
