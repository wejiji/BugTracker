package com.example.security2pro.fake.repository;

import com.example.security2pro.domain.model.SprintIssueHistory;
import com.example.security2pro.repository.repository_interfaces.SprintIssueHistoryRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SprintIssueHistoryRepositoryFake implements SprintIssueHistoryRepository {

    private List<SprintIssueHistory> sprintIssueHistoryList = new ArrayList<>();

    private Long generatedId = 0L;

    @Override
    public Set<SprintIssueHistory> findAllByArchivedSprintId(Long sprintId) {
        sprintIssueHistoryList.forEach(System.out::println);
        return sprintIssueHistoryList.stream().filter(sprintIssueHistory -> sprintIssueHistory.getArchivedSprint().getId().equals(sprintId))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public void saveAll(List<SprintIssueHistory> sprintIssueHistoryList) {
        sprintIssueHistoryList.forEach(this::save);
    }

    public void save(SprintIssueHistory newSprintIssueHistory){
        if(newSprintIssueHistory.getId()==null){
            generatedId++;
            newSprintIssueHistory = SprintIssueHistory.createSprintIssueHistory(
                    generatedId
                    ,newSprintIssueHistory.getArchivedSprint()
                    ,newSprintIssueHistory.getIssue());

            sprintIssueHistoryList.add(newSprintIssueHistory);
            return;
            //return newSprintIssueHistory;
        }
        SprintIssueHistory finalNewSprintIssueHistory = newSprintIssueHistory;
        OptionalInt sprintIssueHistoryOptionalIndex= IntStream.range(0,sprintIssueHistoryList.size())
                        .filter(i->finalNewSprintIssueHistory.getId().equals(sprintIssueHistoryList.get(i).getId()))
                                .findFirst();
        if(sprintIssueHistoryOptionalIndex.isPresent()){
            sprintIssueHistoryList.remove(sprintIssueHistoryOptionalIndex.getAsInt());
        }
        sprintIssueHistoryList.add(newSprintIssueHistory);
        //return newSprintIssueHistory;
    }


}
