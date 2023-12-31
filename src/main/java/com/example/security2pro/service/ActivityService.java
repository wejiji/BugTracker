package com.example.security2pro.service;

import com.example.security2pro.domain.model.Activity;
import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.domain.model.auth.SecurityUser;
import com.example.security2pro.dto.issue.onetomany.ActivityCreateDto;
import com.example.security2pro.dto.issue.onetomany.ActivityDto;
import com.example.security2pro.dto.issue.onetomany.ActivityPageDto;
import com.example.security2pro.repository.repository_interfaces.ActivityRepository;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return activityRepository.findAllByIssueId(issueId, offset, limit);

//        PageRequest pageRequest = PageRequest.of(offset,limit);
//        return new ActivityPageDto(activityRepository.findAllByIssueId(issueId, pageRequest).map(ActivityDto::new));
    }


    public void deleteActivity(Long activityId, SecurityUser user){
        activityRepository.deleteById(activityId);
    }





}
