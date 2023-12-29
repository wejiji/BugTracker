package com.example.security2pro.controller;

import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.dto.issue.onetomany.ActivityCreateDto;
import com.example.security2pro.dto.issue.onetomany.ActivityDto;

import com.example.security2pro.dto.issue.onetomany.ActivityPageDto;
import com.example.security2pro.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@Transactional
public class ActivityController {

    private final ActivityService activityService;


    @PostMapping("issues/{issueId}/activities")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public ActivityDto createActivity(@Validated @RequestBody ActivityCreateDto activityCreateDto){

        return activityService.createActivity(activityCreateDto);
    }

    @GetMapping("/issues/{issueId}/activities")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public ActivityPageDto getIssueActivities(@PathVariable Long issueId
            , @RequestParam(value="offset", defaultValue = "0") int offset
            , @RequestParam(value="limit", defaultValue = "2") int limit){

        return activityService.findAllByIssueId(issueId,offset,limit);
    }


    @DeleteMapping("issues/{issueId}/activities/{activityId}")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#activityId,'activity','author') or hasRole('ADMIN')")
    public void deleteActivity(@PathVariable Long activityId, @AuthenticationPrincipal SecurityUser principal){

        activityService.deleteActivity(activityId, principal);
    }



}
