package com.example.security2pro.service;

import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.dto.ActiveSprintDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;

@Aspect
@Component
@RequiredArgsConstructor
@Transactional
public class ScheduleAspect {

    private final ScheduledTaskExecutor scheduledTaskExecutor;

    private final ObjectProvider<EndSprintTask> endTaskProvider;

    private final IssueService issueService;

    private final SprintService sprintService;

    @Pointcut("execution(public * com.example.security2pro.service.SprintService.updateSprint(..))")
    public void sprintUpdates(){}

    @Pointcut("execution(public * com.example.security2pro.service.SprintService.createSprint(..))")
    public void sprintCreates(){}

    @Pointcut("execution(public * com.example.security2pro.service.SprintService.deleteSprint(..))")
    public void sprintDeletes(){}


    @AfterReturning(value ="sprintUpdates()",returning = "activeSprintDtoResult")
    public void scheduleUpdatedSprintTerminations(JoinPoint joinPoint, ActiveSprintDto activeSprintDtoResult){

        ActiveSprintDto activeSprintDtoArg =(ActiveSprintDto) Arrays.stream(joinPoint.getArgs()).filter(arg->arg.getClass().isAssignableFrom(ActiveSprintDto.class)).findAny().get();
        // afterreturning assumes the arg existed in db



        //archived ones will be filtered in service
        if(!activeSprintDtoResult.getEndDate().equals(activeSprintDtoArg.getEndDate())){
            scheduledTaskExecutor.cancelscheduledSprint(activeSprintDtoResult.getId());

            scheduledTaskExecutor.scheduleSprintEnd(activeSprintDtoResult.getId(), activeSprintDtoResult.getEndDate(), endTaskProvider.getObject(activeSprintDtoResult.getId(), sprintService, issueService));
        }
    }

    @AfterReturning(value ="sprintCreates()",returning = "activeSprintDtoResult")
    public void scheduleCreatedSprintTerminations(JoinPoint joinPoint, ActiveSprintDto activeSprintDtoResult){
        ActiveSprintDto activeSprintDtoArg =(ActiveSprintDto) Arrays.stream(joinPoint.getArgs()).filter(arg->arg.getClass().isAssignableFrom(ActiveSprintDto.class)).findAny().get();
        // afterreturning assumes the arg existed in db

        scheduledTaskExecutor.scheduleSprintEnd(activeSprintDtoResult.getId(),activeSprintDtoResult.getEndDate(), endTaskProvider.getObject(activeSprintDtoResult.getId(), sprintService, issueService));

    }

    @AfterReturning(value = "sprintDeletes()")
    public void cancelOnSprintDelete(JoinPoint joinPoint){
        Long sprintId =(Long)Arrays.stream(joinPoint.getArgs()).filter(arg->arg.getClass().isAssignableFrom(Long.class)).findAny().get();
        // afterreturning assumes the arg existed in db

        scheduledTaskExecutor.cancelscheduledSprint(sprintId);
    }

}
