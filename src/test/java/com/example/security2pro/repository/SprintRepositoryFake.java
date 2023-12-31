package com.example.security2pro.repository;

import com.example.security2pro.domain.model.BaseEntity;
import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.repository.repository_interfaces.SprintRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class SprintRepositoryFake implements SprintRepository {
    Map<Long, Sprint> sprintMap = new HashMap<>();


    private Long generatedId = Long.valueOf(0);

    @Override
    public Optional<Sprint> findById(Long targetId) {
        return Optional.of(sprintMap.get(targetId));
    }

    @Override
    public Set<Sprint> findByProjectIdAndArchivedFalse(Long projectId) {
        return sprintMap.values().stream()
                .filter(sprint -> sprint.getProject().getId().equals(projectId))
                .filter(sprint -> !sprint.isArchived())
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Sprint save(Sprint newSprint) {
        if(newSprint.getId()==null){
            generatedId++;
            Sprint sprint = Sprint.builder()
                    .id(generatedId)
                    .name(newSprint.getName())
                    .description(newSprint.getDescription())
                    .startDate(newSprint.getStartDate())
                    .endDate(newSprint.getEndDate())
                    .build();
            sprintMap.put(newSprint.getId(),sprint);
        }
        sprintMap.put(newSprint.getId(),newSprint);
        return null;
    }

    @Override
    public Sprint getReferenceById(Long sprintId) {
        try {
            return sprintMap.get(sprintId);
        } catch (NullPointerException e){
            throw new EntityNotFoundException ("sprint with id"+ sprintId +" not fount");
        }
    }

    @Override
    public void deleteById(Long id) {
        sprintMap.remove(id);
    }

    @Override
    public Set<Sprint> findByProjectIdAndArchivedTrue(Long projectId) {
        return sprintMap.values().stream()
                .filter(sprint -> sprint.getProject().getId().equals(projectId))
                .filter(BaseEntity::isArchived)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Optional<Sprint> findByIdAndArchivedTrue(Long sprintId) {
        try{
            Sprint sprint = sprintMap.get(sprintId);
            if(sprint.isArchived()) {
                return Optional.of(sprint);
            }
        } catch (NullPointerException e){
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Sprint> findByIdAndProjectIdAndArchivedFalse(Long sprintId, Long projectId) {


        return Optional.empty();
    }

    @Override
    public Set<Sprint> findActiveSprintsByIdAndProjectId(Set<Long> sprintIds, Long projectId) {
        return null;
    }

    @Override
    public Optional<Sprint> findByIdAndArchivedFalse(Long sprintId) {
        return Optional.empty();
    }

    @Override
    public Optional<Sprint> getNext(Long id) {

        //List might be better....

        return Optional.empty();
    }
}
