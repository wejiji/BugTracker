package com.example.security2pro.service;

import com.example.security2pro.domain.model.Activity;
import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.dto.issue.onetomany.ActivityCreateDto;
import com.example.security2pro.dto.issue.onetomany.ActivityDto;
import com.example.security2pro.dto.issue.onetomany.ActivityPageDto;
import com.example.security2pro.repository.ActivityRepository;
import com.example.security2pro.repository.IssueRepository;
import com.example.security2pro.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    private final IssueRepository issueRepository;


    public ActivityDto createActivity(ActivityCreateDto activityCreateDto){

        Issue issue = issueRepository.getReferenceById(activityCreateDto.getIssueId());
        Activity savedActivity = activityRepository.save(new Activity(null, issue ,activityCreateDto.getType(),activityCreateDto.getDescription()));
        return new ActivityDto(savedActivity);
    }


    public ActivityPageDto findAllByIssueId(Long issueId, int offset, int limit){

        PageRequest pageRequest = PageRequest.of(offset,limit);
        return new ActivityPageDto(activityRepository.findAllByIssueId(issueId, pageRequest).map(ActivityDto::new));
    }


    public void deleteActivity(Long activityId, SecurityUser user){

//        Optional<Activity> activityOptional= activityRepository.findById(activityId);
//        if(activityOptional.isEmpty()) throw new IllegalArgumentException("activity not found");
//
//        //if user is not project lead or admin..
//        if(!user.getUser().getAuthorities().contains(Role.ROLE_ADMIN)){
//            Optional<ProjectMember> projectMemberOptional = projectMemberRepository.findByUsernameAndProjectIdWithAuthorities(user.getUsername(),activityOptional.get().getIssue().getProject().getId());
//            if(projectMemberOptional.isEmpty()){
//                throw new AccessDeniedException("only the author/ project-lead/ admin can delete an activity");
//            }
//            if(!projectMemberOptional.get().getAuthorities().contains(Role.ROLE_PROJECT_LEAD)){
//                if(activityOptional.get().getCreatedBy().equals(user.getUsername())){
//                    activityRepository.deleteById(activityId);
//                } else {
//                    throw new AccessDeniedException("only the author/ project-lead/ admin can delete an activity");
//                }
//            }
//        } else {
//            activityRepository.deleteById(activityId);
//        }

        activityRepository.deleteById(activityId);
    }





}
