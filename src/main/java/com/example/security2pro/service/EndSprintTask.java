package com.example.security2pro.service;

import com.example.security2pro.domain.model.Sprint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional
@Component
@RequiredArgsConstructor
@Slf4j
public class EndSprintTask implements Runnable {

    private final Long sprintId;

    private final SprintService sprintService;

    private final IssueService issueService;


    @Override
    public void run() {
        log.info("sprint with id"+ sprintId +" is ending now by the system at its scheduled end Date");
        Sprint sprint = sprintService.getReferenceById(sprintId);
        sprint.completeSprint();
        sprintService.handleEndingSprintIssues(
                sprint
                ,issueService.findIssuesByCurrentSprintId(sprint.getId())
                , Collections.emptyMap());

    }
}