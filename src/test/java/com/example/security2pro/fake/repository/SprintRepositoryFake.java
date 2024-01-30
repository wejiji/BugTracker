package com.example.security2pro.fake.repository;

import com.example.security2pro.databuilders.SprintTestDataBuilder;
import com.example.security2pro.domain.model.Sprint;
import com.example.security2pro.repository.repository_interfaces.SprintRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class SprintRepositoryFake implements SprintRepository {

    List<Sprint> sprintList =new ArrayList<>();

    private Long generatedId = 0L;

    @Override
    public Optional<Sprint> findById(Long targetId) {
        return sprintList.stream().filter(sprint -> sprint.getId().equals(targetId)).findAny();
    }
    @Override
    public Set<Sprint> findByProjectIdAndArchivedFalse(Long projectId) {
        return sprintList.stream().filter(sprint-> sprint.getProject().getId().equals(projectId) && !sprint.isArchived())
                .collect(Collectors.toCollection(HashSet::new));
    }
    @Override
    public Sprint save(Sprint newSprint) {
        if(newSprint.getId()==null){
            System.out.println("id was null..");
            generatedId++;

            System.out.println(generatedId +" was generated Id");
            Sprint sprint = new SprintTestDataBuilder()
                    .withId(generatedId)
                    .withProject(newSprint.getProject())
                    .withName(newSprint.getName())
                    .withDescription(newSprint.getDescription())
                    .withStartDate(newSprint.getStartDate())
                    .withEndDate(newSprint.getEndDate())
                    .build();

            sprintList.add(sprint);
            return sprint;
        }

       OptionalInt foundSprintIndex = IntStream.range(0,sprintList.size())
                        .filter(i->newSprint.getId().equals(sprintList.get(i).getId()))
                                .findFirst();
        if(foundSprintIndex.isPresent()){
            sprintList.remove(foundSprintIndex.getAsInt());
        }
        sprintList.add(newSprint);
        return newSprint;
    }

    @Override
    public Sprint getReferenceById(Long sprintId) {
        return sprintList.stream().filter(sprint -> sprint.getId().equals(sprintId)).findAny()
                .orElseThrow(()->new EntityNotFoundException ("sprint with id"+ sprintId +" not fount"));
    }

    @Override
    public void deleteById(Long sprintId) {
        OptionalInt foundSprintIndex = IntStream.range(0,sprintList.size())
                .filter(i->sprintId.equals(sprintList.get(i).getId()))
                .findFirst();
        sprintList.remove(foundSprintIndex.getAsInt());
    }

    @Override
    public Set<Sprint> findByProjectIdAndArchivedTrue(Long projectId) {
        return sprintList.stream().filter(sprint -> sprint.getProject().getId().equals(projectId) && sprint.isArchived())
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Optional<Sprint> findByIdAndArchivedTrue(Long sprintId) {
        return sprintList.stream().filter(sprint -> sprint.getId().equals(sprintId) && sprint.isArchived()).findAny();
    }

    @Override
    public Optional<Sprint> findByIdAndProjectIdAndArchivedFalse(Long sprintId, Long projectId) {
        return sprintList.stream().filter(sprint -> sprint.getId().equals(sprintId) && sprint.getProject().getId().equals(projectId) && !sprint.isArchived()).findAny();
    }

    @Override
    public Set<Sprint> findActiveSprintsByIdAndProjectId(Set<Long> sprintIds, Long projectId) {
        return sprintList.stream().filter(sprint -> sprintIds.contains(sprint.getId()) && sprint.getProject().getId().equals(projectId) && !sprint.isArchived()).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Optional<Sprint> findByIdAndArchivedFalse(Long sprintId) {
        return sprintList.stream().filter(sprint -> sprint.getId().equals(sprintId) && !sprint.isArchived()).findAny();
    }

    @Override
    public Optional<Sprint> getNext(Long previousSprintId) {
        // comparator
        return sprintList.stream().filter(sprint -> sprint.getId() > previousSprintId && !sprint.isArchived()).min(new Comparator<Sprint>() {
            @Override
            public int compare(Sprint sprint1, Sprint sprint2) {
                if (sprint1.getId() > sprint2.getId()) {
                    return 1;
                } else if (sprint1.getId().equals(sprint2.getId())) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
    }
    @Override
    public void deleteAllByIdInBatch(Set<Long> sprintIds) {
        sprintList = sprintList.stream().filter(sprint -> !sprintIds.contains(sprint.getId())).collect(Collectors.toCollection(ArrayList::new));
    }
    @Override
    public Set<Sprint> findAllByProjectId(Long projectId) {
        return sprintList.stream().filter(sprint->sprint.getProject().getId().equals(projectId)).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<Sprint> saveAll(Set<Sprint> sprints) {
        return sprints.stream().map(this::save).collect(Collectors.toSet());
    }

}
