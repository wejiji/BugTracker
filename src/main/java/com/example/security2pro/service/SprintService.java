package com.example.security2pro.service;

import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.sprint.SprintCreateDto;
import com.example.security2pro.dto.sprint.SprintUpdateDto;
import com.example.security2pro.dto.sprinthistory.SprintIssueHistoryDto;
import com.example.security2pro.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SprintService {

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;

    private final ProjectRepository projectRepository;

    private final SprintIssueHistoryRepository sprintIssueHistoryRepository;

    public SprintUpdateDto createSprint(Long projectId, SprintCreateDto activeSprintCreateDto){

        Sprint sprint = sprintRepository.save(convertSprintDtoToModelCreate(projectId, activeSprintCreateDto));
        return new SprintUpdateDto(sprint);
    }

    public SprintUpdateDto updateSprint(Long sprintId, SprintUpdateDto activeSprintUpdateDto){

        Sprint sprint = convertSprintDtoToModelUpdate(sprintId, activeSprintUpdateDto);
        return new SprintUpdateDto(sprint);
    }


    public void endSprint(Long sprintId){
        Sprint sprint = sprintRepository.getReferenceById(sprintId);
        sprint.completeSprint();//below code does not need updated info of the sprint. so save is not necessary
    }


    public void deleteSprint(Long sprintId){
        Sprint sprint =sprintRepository.getReferenceById(sprintId);

        Set<Issue> issues= issueRepository.findByCurrentSprintId(sprint.getId());
        issues= issues.stream().peek(issue -> issue.assignCurrentSprint(null)).collect(Collectors.toCollection(HashSet::new));
        sprintRepository.deleteById(sprint.getId());
    }


    public SprintUpdateDto getSprintById(Long sprintId){
        Sprint sprint = sprintRepository.getReferenceById(sprintId);
        return new SprintUpdateDto(sprint);
    }

    public Set<SprintUpdateDto> getActiveSprints(Long projectId){
        return sprintRepository.findByProjectIdAndArchivedFalse(projectId).stream().map(SprintUpdateDto::new).collect(Collectors.toCollection(HashSet::new));
    }

    public Set<SprintUpdateDto> getArchivedSprints(Long projectId){
        return sprintRepository.findByProjectIdAndArchivedTrue(projectId).stream().map(SprintUpdateDto::new).collect(Collectors.toCollection(HashSet::new));
    }

    private Sprint convertSprintDtoToModelCreate(Long projectId, SprintCreateDto activeSprintCreateDto){
        Project project = projectRepository.getReferenceById(projectId);

        String sprintName = activeSprintCreateDto.getName();
        String description = activeSprintCreateDto.getDescription();
        LocalDateTime startDate = activeSprintCreateDto.getStartDate();
        LocalDateTime endDate = activeSprintCreateDto.getEndDate();
        return new Sprint(project,sprintName,description,startDate,endDate);
    }

    private Sprint convertSprintDtoToModelUpdate(Long sprintId, SprintUpdateDto sprintUpdateDto){

        Sprint sprint = sprintRepository.getReferenceById(sprintId);
        sprint.updateFields(sprintUpdateDto.getName(), sprintUpdateDto.getDescription(),sprintUpdateDto.getStartDate(),sprintUpdateDto.getEndDate());
        return sprint;
    }



    public Set<SprintIssueHistoryDto> getSprintIssueHistory(Long sprintId){
        Optional<Sprint> sprintOptional= sprintRepository.findByIdAndArchivedTrue(sprintId);
        if(sprintOptional.isEmpty()){throw new IllegalArgumentException("the sprint is not archived");}

        return sprintIssueHistoryRepository.findById(sprintId).stream().map(SprintIssueHistoryDto::new).collect(Collectors.toSet());
    }







}
