package com.example.security2pro.service;

import com.example.security2pro.domain.enums.ActivityType;
import com.example.security2pro.domain.model.Activity;
import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.dto.IssueDto;
import com.example.security2pro.repository.ActivityRepository;
import com.example.security2pro.repository.AuditRepository;
import com.example.security2pro.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@RequiredArgsConstructor
@Component
@Slf4j
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class AuditAspect {

    private final ActivityRepository activityRepository;
    private final AuditRepository auditRepository;
    private final IssueRepository issueRepository;

//    @Pointcut("execution(public * *.*IssueFromDto(..))")
//    public void allIssueHistoryUpdateActivities(){}

    @Pointcut("execution(public * com.example.security2pro.controller.IssueController.updateIssue(..))" +
            "||execution(public * com.example.security2pro.controller.IssueController.createIssue(..))")
    public void allIssueHistoryUpdateActivities(){}

    @AfterReturning(value="allIssueHistoryUpdateActivities()",returning = "issueDto")
    public void updateActivities(JoinPoint joinPoint, IssueDto issueDto) throws Throwable {
        Long issueId = issueDto.getIssueId();

        int historyActivitiesCountBefore = activityRepository.findIssueHistoryCountByIssueId(issueId);

        List<String> updateDescriptions=null;
        updateDescriptions = auditRepository.getHistory(Issue.class, issueId);

        log.info("history Activity count before= " +historyActivitiesCountBefore);
        log.info("updateDescription size " + updateDescriptions.size());

        if(historyActivitiesCountBefore < updateDescriptions.size()){ // only if there was modification
            List<String> updatedHistories= new ArrayList<>(updateDescriptions.subList(historyActivitiesCountBefore+(updateDescriptions.size()-historyActivitiesCountBefore)-1,updateDescriptions.size()));
            // find all the activity that has type - issue History type for this issue. and use the size of the list.

            Issue issue = issueRepository.getReferenceById(issueId);

            List<Activity> activityList=updatedHistories.stream().map(history-> new Activity(null,null, issue, ActivityType.ISSUE_HISTORY,history)).collect(Collectors.toCollection(ArrayList::new));
            activityRepository.saveAll(activityList);
            String result = activityList.stream().map(Activity::getDescription).collect(Collectors.joining("////"));
            log.info(result + "was saved in activities.. ");
        }

    }



}
