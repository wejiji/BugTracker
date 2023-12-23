package com.example.security2pro.service;

import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.domain.model.IssueRelation;
import com.example.security2pro.dto.issue.IssueRelationCreateDto;
import com.example.security2pro.dto.issue.IssueUpdateDto;
import com.example.security2pro.repository.IssueRelationRepository;
import com.example.security2pro.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RequiredArgsConstructor
@Service
@Transactional
public class IssueRelationConverter {

    private final IssueRelationRepository issueRelationRepository;

    private final IssueRepository issueRepository;

    private final Supplier<IllegalArgumentException> causeIssueException = ()-> new IllegalArgumentException("invalid issue relation. " +
            "cause issue cannot be the same as the affected issue. " +
            "cause Issue with 'DONE' state cannot be newly added as a cause issue. ");

    public Set<IssueRelation> convertToIssueRelationListToUpdate(Issue issue, IssueUpdateDto issueUpdateDto){

        deleteInvalidIssueRelations(issueUpdateDto);
        return convertToIssueRelationModelToUpdate(issue,issueUpdateDto.getIssueRelationDtoList());
    }

    public Set<IssueRelation> convertToIssueRelationModelToCreate(Issue issue, Set<IssueRelationCreateDto> issueRelationCreateDtos) {
        Map<Long,List<Issue>> causeIssuesMap = getValidatedCauseIssuesMap(issueRelationCreateDtos.stream().map(IssueRelationCreateDto::getCauseIssueId).collect(Collectors.toCollection(HashSet::new)));

        return issueRelationCreateDtos.stream()
                .map(issueRelationDto -> {
                    Issue causeIssue = causeIssuesMap.get(issueRelationDto.getCauseIssueId()).get(0);
                    return IssueRelation.createIssueRelation(issue,causeIssue,issueRelationDto.getRelationDescription())
                            .orElseThrow(causeIssueException);
                }).collect(Collectors.toSet());
    }

    private Set<IssueRelation> convertToIssueRelationModelToUpdate(Issue issue, Set<IssueRelationCreateDto> issueRelationCreateDtos) {

        Map<Long, List<Issue>> passedCauseIssuesMap = getValidatedCauseIssuesMap(issueRelationCreateDtos.stream().map(IssueRelationCreateDto::getCauseIssueId).collect(Collectors.toCollection(HashSet::new)));
        Map<Long,List<IssueRelation>> foundIssueRelations = issueRelationRepository.findAllByAffectedIssueIdAndCauseIssueIds( issue.getId(),passedCauseIssuesMap.keySet()).stream().collect(groupingBy(issueRelation -> issueRelation.getCauseIssue().getId()));

        return issueRelationCreateDtos.stream()
                .map(issueRelationDto -> {
                    Issue causeIssue = passedCauseIssuesMap.get(issueRelationDto.getCauseIssueId()).get(0);

                    if(foundIssueRelations.containsKey(causeIssue.getId())){//if relationship with the same cause issue Id already exists
                        IssueRelation issueRelation= foundIssueRelations.get(causeIssue.getId()).get(0);
                        return IssueRelation.getUpdatedIssueRelation(issueRelation.getId(),issue,causeIssue,issueRelationDto.getRelationDescription());
                    }
                    return IssueRelation.createIssueRelation(issue,causeIssue,issueRelationDto.getRelationDescription())
                            .orElseThrow(causeIssueException);
                }).collect(Collectors.toSet());
    }



        private void deleteInvalidIssueRelations(IssueUpdateDto issueUpdateDto) {
        //not fetching cause issues here. just relations (lazy loading)
        //not delete where... get ids first by cause issue Ids and delete in bulk
        Set<Long> previousCauseIssueIds = issueRelationRepository.findAllByAffectedIssueId(issueUpdateDto.getIssueId()).stream().map(issueRelation -> issueRelation.getCauseIssue().getId()).collect(Collectors.toCollection(HashSet::new));
        Set<Long> currentCauseIssueIds = issueUpdateDto.getIssueRelationDtoList().stream().map(IssueRelationCreateDto::getCauseIssueId).collect(toCollection(HashSet::new));
        previousCauseIssueIds.removeAll(currentCauseIssueIds);
        Set<Long> relationsToBeRemoved=issueRelationRepository.findAllByAffectedIssueIdAndCauseIssueIds(issueUpdateDto.getIssueId(),previousCauseIssueIds).stream().map(IssueRelation::getId).collect(toCollection(HashSet::new));
        issueRelationRepository.deleteAllByIdInBatch(relationsToBeRemoved);
    }


    private Map<Long,List<Issue>> getValidatedCauseIssuesMap(Set<Long> causeIssueIds){
        //fetch all the issues from db
        List<Issue> foundCauseIssues = issueRepository.findAllById(causeIssueIds);
        if(causeIssueIds.size()!=foundCauseIssues.size()){
            throw new IllegalArgumentException("some cause issues not found within the project");
        }
        return foundCauseIssues.stream().collect(groupingBy(Issue::getId));
    }
}
