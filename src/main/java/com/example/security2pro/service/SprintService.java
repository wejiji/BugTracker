package com.example.security2pro.service;

import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.sprint.ActiveSprintUpdateDto;
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

    public ActiveSprintUpdateDto createSprint(Long projectId, ActiveSprintUpdateDto activeSprintUpdateDto){

        Sprint sprint = sprintRepository.save(convertSprintDtoToModel(projectId, activeSprintUpdateDto));
        return new ActiveSprintUpdateDto(sprint);
    }

    public ActiveSprintUpdateDto updateSprint(Long projectId, ActiveSprintUpdateDto activeSprintUpdateDto){

        Sprint sprint =sprintRepository.findByIdAndProjectId(activeSprintUpdateDto.getId(), projectId)
                .orElseThrow(()-> new IllegalArgumentException(
                        "sprint with id"+ activeSprintUpdateDto.getId()+" does not exist within the project with id"+projectId));

        sprint = convertSprintDtoToModel(projectId, activeSprintUpdateDto);
        return new ActiveSprintUpdateDto(sprint);

        //        Sprint sprint = sprintRepository.getReferenceById(activeSprintUpdateDto.getId());
//        if(!projectId.equals(sprint.getProject().getId())){
//            throw new IllegalArgumentException("sprint with id "+ sprint.getId() +" does not belong to the project with id"+ projectId);
//        }
//        Sprint newSprint = convertSprintDtoToModel(projectId, activeSprintUpdateDto);
//        newSprint = sprintRepository.save(new Sprint(activeSprintUpdateDto.getId(),newSprint)); // sprint does not have issues field
//        return new ActiveSprintUpdateDto(newSprint);
    }


    public void endSprint(Long projectId,Long sprintId){
        Sprint sprint = sprintRepository.getReferenceById(sprintId);
        if(!sprint.getProject().getId().equals(projectId)){throw new IllegalArgumentException("sprint with id" +sprint.getId() +" does not belong to the project with id"+ projectId);}

        sprint.completeSprint();//below code does not need updated info of the sprint. so save is not necessary
    }


    public void deleteSprint(Long projectId,Long sprintId){
//        Sprint sprint = sprintRepository.getReferenceById(sprintId);
//        if(!projectId.equals(sprint.getProject().getId())){
//            throw new IllegalArgumentException("sprint with id "+ sprintId+" does not belong to the project with id"+ projectId);
//        }
        Sprint sprint =sprintRepository.findByIdAndProjectId(sprintId, projectId)
                .orElseThrow(()-> new IllegalArgumentException(
                        "sprint with id"+ sprintId+" does not exist within the project with id"+projectId));

        Set<Issue> issues= issueRepository.findByCurrentSprintId(sprint.getId());
        issues= issues.stream().peek(issue -> issue.assignCurrentSprint(null)).collect(Collectors.toCollection(HashSet::new));
        sprintRepository.deleteById(sprint.getId());
    }


    public ActiveSprintUpdateDto getSprintById(Long sprintId){
        Sprint sprint = sprintRepository.getReferenceById(sprintId);
        return new ActiveSprintUpdateDto(sprint);
    }

    public Set<ActiveSprintUpdateDto> getActiveSprints(Long projectId){
        return sprintRepository.findByProjectIdAndArchivedFalse(projectId).stream().map(ActiveSprintUpdateDto::new).collect(Collectors.toCollection(HashSet::new));
    }

    public Set<ActiveSprintUpdateDto> getArchivedSprints(Long projectId){
        return sprintRepository.findByProjectIdAndArchivedTrue(projectId).stream().map(ActiveSprintUpdateDto::new).collect(Collectors.toCollection(HashSet::new));
    }

    private Sprint convertSprintDtoToModel(Long projectId, ActiveSprintUpdateDto activeSprintUpdateDto){
        Project project = projectRepository.getReferenceById(projectId);

        String sprintName = activeSprintUpdateDto.getName();
        String description = activeSprintUpdateDto.getDescription();
        LocalDateTime startDate = activeSprintUpdateDto.getStartDate();
        LocalDateTime endDate = activeSprintUpdateDto.getEndDate();
        return new Sprint(project,sprintName,description,startDate,endDate);
    }














}
