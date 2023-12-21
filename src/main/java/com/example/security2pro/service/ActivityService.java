package com.example.security2pro.service;

import com.example.security2pro.domain.enums.Role;
import com.example.security2pro.domain.model.Activity;
import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.dto.issue.ActivityCreateDto;
import com.example.security2pro.dto.issue.ActivityDtoNew;
import com.example.security2pro.repository.ActivityRepository;
import com.example.security2pro.repository.IssueRepository;
import com.example.security2pro.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    private final IssueRepository issueRepository;

    private final ProjectMemberRepository projectMemberRepository;

    public ActivityDtoNew createActivity(ActivityCreateDto activityCreateDto){

        Issue issue = issueRepository.getReferenceById(activityCreateDto.getIssueId());
        Activity savedActivity = activityRepository.save(new Activity(null, issue ,activityCreateDto.getType(),activityCreateDto.getDescription()));
        return new ActivityDtoNew(savedActivity);
    }


    public Set<ActivityDtoNew> findAllByIssueId(Long issueId){

        return activityRepository.findAllByIssueId(issueId).stream().map(ActivityDtoNew::new).collect(Collectors.toCollection(HashSet::new));
    }


    // no preauth .... try to refactor!
    public void deleteActivity(Long activityId, SecurityUser user){

        Optional<Activity> activityOptional= activityRepository.findById(activityId);
        if(activityOptional.isEmpty()) throw new IllegalArgumentException("activity not found");

        //if user is not project lead or admin..
        if(!user.getUser().getAuthorities().contains(Role.ROLE_ADMIN)){
            Optional<ProjectMember> projectMemberOptional = projectMemberRepository.findByUsernameAndProjectIdWithAuthorities(user.getUsername(),activityOptional.get().getIssue().getProject().getId());
            if(projectMemberOptional.isEmpty()){
                throw new AccessDeniedException("only the author/ project-lead/ admin can delete an activity");
            }
            if(!projectMemberOptional.get().getAuthorities().contains(Role.ROLE_PROJECT_LEAD)){
                if(activityOptional.get().getCreatedBy().equals(user.getUsername())){
                    activityRepository.deleteById(activityId);
                } else {
                    throw new AccessDeniedException("only the author/ project-lead/ admin can delete an activity");
                }
            }
        } else {
            activityRepository.deleteById(activityId);
        }




    }





}
