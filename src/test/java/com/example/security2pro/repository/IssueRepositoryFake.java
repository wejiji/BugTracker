package com.example.security2pro.repository;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IssueRepositoryFake implements IssueRepository {


    List<Issue> issueList = new ArrayList<>();
    private Long generatedId = Long.valueOf(0);

    @Override
    public Issue getReferenceById(Long issueId) {
        return issueList.stream().filter(issue -> issue.getId().equals(issueId)).findAny()
                .orElseThrow(()-> new EntityNotFoundException("issue with id"+ issueId +"not found"));
    }

    @Override
    public Set<Issue> findActiveIssueByUsername(String username) {
        return issueList.stream().filter(issue -> issue.getAssigneesNames().contains(username) && !issue.isArchived()).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<Issue> findByProjectIdAndArchivedFalse(Long projectId) {
        return issueList.stream().filter(issue-> issue.getProject().getId().equals(projectId) && !issue.isArchived()).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<Issue> findByCurrentSprintId(Long sprintId) {
        return issueList.stream().filter(issue -> issue.getCurrentSprint().isPresent() && issue.getCurrentSprint().get().getId().equals(sprintId))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Optional<Issue> findByIdWithAssignees(Long issueId) {
        return issueList.stream().filter(issue-> issue.getId().equals(issueId)).findFirst();
    }

    @Override
    public Issue save(Issue newIssue) {

        if(newIssue.getId()==null){
            generatedId++;
            IssueTestDataBuilder issueTestDataBuilder = new IssueTestDataBuilder()
                    .withId(generatedId)
                    .withProject(newIssue.getProject())
                    .withTitle(newIssue.getTitle())
                    .withDescription(newIssue.getDescription())
                    .withAssignees(newIssue.getAssignees())
                    .withStatus(newIssue.getStatus())
                    .withPriority(newIssue.getPriority())
                    .withType(newIssue.getType());

            if(newIssue.getCurrentSprint().isPresent()){
                newIssue = issueTestDataBuilder.withSprint(newIssue.getCurrentSprint().get()).build();

            }
            newIssue = issueTestDataBuilder.build();

            issueList.add(newIssue);
            return newIssue;
        }

        Issue finalNewIssue = newIssue;
        OptionalInt foundIssueIndex = IntStream.range(0,issueList.size())
                        .filter(i-> finalNewIssue.getId().equals(issueList.get(i).getId()))
                                .findFirst();
        if(foundIssueIndex.isPresent()){
            issueList.remove(foundIssueIndex.getAsInt());
        }
        issueList.add(newIssue);
        return newIssue;
    }

    @Override
    public Set<Issue> findAllByIdAndProjectIdAndArchivedFalse(Set<Long> issueDtoIds, Long projectId) {
        return issueList.stream().filter(issue-> issue.getProject().getId().equals(projectId) && issueDtoIds.contains(issue.getId()) && !issue.isArchived())
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<Issue> saveAll(Set<Issue> resultIssues) {
        return resultIssues.stream().map(this::save).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public void deleteAllByIdInBatch(Set<Long> issueIds) {
        issueList = issueList.stream().filter(issue->!issueIds.contains(issue.getId())).collect(Collectors.toCollection(ArrayList::new));

    }

    @Override
    public Set<Issue> findByCurrentSprintIdIn(Set<Long> sprintIds) {
        return issueList.stream().filter(issue-> issue.getCurrentSprint().isPresent() && sprintIds.contains(issue.getCurrentSprint().get().getId()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<Issue> findByProjectIdAndArchivedFalseAndCurrentSprintIsNull(Long projectId) {
        return issueList.stream().filter(issue -> issue.getProject().getId().equals(projectId) && !issue.isArchived() && issue.getCurrentSprint().isEmpty())
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<Issue> findAllById(Set<Long> issueIds) {
        return issueList.stream().filter(issue->issueIds.contains(issue.getId())).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Optional<Issue> findById(Long issueId) {
        return issueList.stream().filter(issue->issue.getId().equals(issueId)).findAny();
    }

    @Override
    public Optional<Issue> findByIdWithIssueRelationSet(Long issueId) {
        return issueList.stream().filter(issue->issue.getId().equals(issueId)).findAny();
    }

    @Override
    public Optional<Issue> findByIdWithCommentList(Long issueId) {
        return issueList.stream().filter(issue -> issue.getId().equals(issueId)).findAny();
    }

    @Override
    public Set<Issue> findAllByProjectId(Long projectId) {
        return issueList.stream().filter(issue->issue.getProject().getId().equals(projectId)).collect(Collectors.toCollection(HashSet::new));
    }


    public List<Issue> findAll(){
        return issueList;
    }

}
