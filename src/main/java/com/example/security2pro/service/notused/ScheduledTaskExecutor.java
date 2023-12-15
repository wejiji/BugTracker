//package com.example.security2pro.service;
//
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.stereotype.Component;
//
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ScheduledFuture;
//
//@Getter
//@RequiredArgsConstructor
//@Component
//public class ScheduledTaskExecutor {
//
//    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
//
//    private Map<Long, ScheduledFuture> endingTasks = new HashMap<>();
//
//    public void scheduleSprintEnd(Long id, LocalDateTime endDate, EndSprintTask endSprintTask){
//        Instant instant =  endDate.toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now()));
//
//        ScheduledFuture scheduledEnd = threadPoolTaskScheduler.schedule(endSprintTask, instant);
//        endingTasks.put(id, scheduledEnd);
//    }
//
//    public void cancelscheduledSprint(Long sprintId){
//        endingTasks.get(sprintId).cancel(false); // there is no reason to cancel if it is already happening
//    }
//
//
//}