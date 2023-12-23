package com.example.security2pro.controller;

import com.example.security2pro.dto.issue.*;
import com.example.security2pro.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
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
    public IssueUpdateResponseDto getIssueDetails(@PathVariable Long issueId){

        return issueService.getIssueWithDetails(issueId);
    }


    @GetMapping("/issues")
    @PreAuthorize("#username==authentication.principal.username or hasRole('ADMIN')")
    // only the user itself can look
    public Set<IssueSimpleDto> userIssues(@RequestParam String username){
        return issueService.getUserIssues(username);
    }


    @DeleteMapping("/issues/{issueId}")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public void deleteIssue(@PathVariable Long issueId){

        issueService.deleteByIdsInBulk(new HashSet<>(List.of(issueId)));
    }


    @GetMapping("/issues/{issueId}/history")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public List<IssueHistoryDto> getIssueHistory(@PathVariable Long issueId){

        return issueHistoryService.getIssueHistories(issueId);
    }


//===========================================================

    @PostMapping("/simple-issues")
    @PreAuthorize("hasPermission('IssueCreateDto','ROLE_PROJECT_LEAD') or hasPermission('IssueCreateDto','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public SimpleIssueUpdateDto createSimpleIssue(@Validated @RequestBody SimpleIssueCreateDto simpleIssueCreateDto
            , BindingResult bindingResult) throws BindException{
        // the below is for validation logic. If type mismatch or Other deserialization exception happens,
        // It will throw an error and this controller is not called......
        //Go to rest controller advice to take care of this.
        //also it looks like 403 is caused by this error which triggers authentication entrypoint !...
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }
        return issueService.createIssueFromSimpleDto(simpleIssueCreateDto);
    }

    @PostMapping("/simple-issues/{issueId}")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public SimpleIssueUpdateDto updateSimpleIssue(@PathVariable Long issueId , @Validated @RequestBody SimpleIssueUpdateDto simpleIssueUpdateDto,
                                              BindingResult bindingResult) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}

        return issueService.updateIssueFromSimpleDto(simpleIssueUpdateDto);
    }

//    @GetMapping("/simple-issues/{issueId}")
//    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
//    public SimpleIssueUpdateDto getSimpleIssue(@PathVariable Long issueId){
//
//        return issueService.getIssueSimple(issueId);
//    }



    //=========================================
    @PostMapping("/issues")
    @PreAuthorize("hasPermission('IssueCreateDto','ROLE_PROJECT_LEAD') or hasPermission('IssueCreateDto','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public IssueUpdateResponseDto createIssue(@Validated @RequestBody IssueCreateDto issueCreateDto
            , BindingResult bindingResult) throws BindException{
        // the below is for validation logic. If type mismatch or Other deserialization exception happens,
        // It will throw an error and this controller is not called......
        //Go to rest controller advice to take care of this.
        //also it looks like 403 is caused by this error which triggers authentication entrypoint !...
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }
        return issueService.createIssueDetailFromDto(issueCreateDto);
    }

    @PostMapping("/issues/{issueId}")
    @PreAuthorize("hasPermission(#issueId,'issue','ROLE_PROJECT_LEAD') or hasPermission(#issueId,'issue','ROLE_PROJECT_MEMBER') or hasRole('ADMIN')")
    public IssueUpdateResponseDto updateIssue(@PathVariable Long issueId , @Validated @RequestBody IssueUpdateDto issueUpdateDto,
                                              BindingResult bindingResult) throws BindException {

        if(bindingResult.hasErrors()){throw new BindException(bindingResult);}

        return issueService.updateIssueDetailFromDto(issueUpdateDto);
    }
//=====================================================================
}
