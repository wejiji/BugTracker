package com.example.bugtracker.service;

import com.example.bugtracker.exception.directmessageconcretes.InvalidSprintArgumentException;
import com.example.bugtracker.domain.model.*;
import com.example.bugtracker.domain.model.issue.Issue;
import com.example.bugtracker.dto.sprint.SprintCreateDto;
import com.example.bugtracker.dto.sprint.SprintUpdateDto;
import com.example.bugtracker.dto.sprinthistory.SprintIssueHistoryDto;
import com.example.bugtracker.repository.repository_interfaces.IssueRepository;
import com.example.bugtracker.repository.repository_interfaces.ProjectRepository;
import com.example.bugtracker.repository.repository_interfaces.SprintIssueHistoryRepository;
import com.example.bugtracker.repository.repository_interfaces.SprintRepository;
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

    /*
     * Because there is no JPA bidirectional relationship between the 'Sprint' entity and 'Issue' entity,
     * separate queries will be executed for retrieving and deleting 'Issues' from their repository.
     *
     * While calling the 'save' methods are sometimes unnecessary when the entity is already in the cache,
     * as dirty checking can automatically update modified fields, they are called nevertheless for explicitness.
     */

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;

    private final ProjectRepository projectRepository;

    private final SprintIssueHistoryRepository sprintIssueHistoryRepository;


    /**
     * Creates and saves a 'Sprint' object for the specified project.
     * Returns a 'SprintUpdateDto' with an auto-generated id.
     *
     * @param projectId The id of the project for which the sprint is created.
     *                  Expected to be verified for existence beforehand.
     * @param sprintCreateDto The data required for creating a sprint.
     * @return A 'SprintUpdateDto' with the auto-generated id.
     */
    public SprintUpdateDto createSprintFromDto(Long projectId, SprintCreateDto sprintCreateDto) {
        Sprint sprint
                = sprintRepository.save(
                convertSprintDtoToModelCreate(
                        projectId, sprintCreateDto));
        return new SprintUpdateDto(sprint);
    }

    /**
     * Updates a 'Sprint' object based on the provided data in the 'SprintUpdateDto'.
     *
     * @param sprintId The id of the sprint to be updated.
     *                 Expected to be verified for existence within the project beforehand.
     * @param sprintUpdateDto The data used for updating the sprint.
     * @return A 'SprintUpdateDto' representing the updated sprint.
     */
    public SprintUpdateDto updateSprintFromDto(Long sprintId, SprintUpdateDto sprintUpdateDto) {
        Sprint sprint = convertSprintDtoToModelUpdate(
                sprintId, sprintUpdateDto);
        return new SprintUpdateDto(sprint);
    }

    /**
     * Deletes the 'Sprint' identified by the provided id.
     * Unassigns all issues assigned to this sprint by setting their 'currentSprint' to null.
     * Issues are handled by their own repository since there is no JPA bidirectional relationship.
     *
     * @param sprintId The id of the sprint to be deleted.
     */
    public void deleteSprint(Long sprintId) {
        Set<Issue> issues = issueRepository.findByCurrentSprintId(sprintId);
        issues.forEach(issue -> issue.assignCurrentSprint(null));
        issueRepository.saveAll(issues);
        sprintRepository.deleteById(sprintId);
    }

    /**
     * Retrieves a 'SprintUpdateDto' for the 'Sprint' identified by the provided id.
     *
     * @param sprintId The id of the sprint to be retrieved.
     *                 Expected to be verified for existence within the project beforehand.
     * @return A 'SprintUpdateDto' for the specified sprint.
     */
    public SprintUpdateDto getSprintById(Long sprintId) {
        Sprint sprint = sprintRepository.getReferenceById(sprintId);
        return new SprintUpdateDto(sprint);
    }

    /**
     * Retrieves a set of 'SprintUpdateDto' for all non-archived sprints associated with the specified project.
     *
     * @param projectId The id of the project for which non-archived sprints are to be retrieved.
     *                  Expected to be verified for existence beforehand.
     * @return A set of 'SprintUpdateDto' representing all non-archived sprints associated with the specified project.
     */
    public Set<SprintUpdateDto> getActiveSprints(Long projectId) {
        return sprintRepository.findByProjectIdAndArchivedFalse(projectId)
                .stream().map(SprintUpdateDto::new)
                .collect(Collectors.toCollection(HashSet::new));
    }


    /**
     * Converts a 'SprintCreateDto' object to a 'Sprint' object during the creation process of a 'Sprint'.
     * Note that a null id argument is passed to 'createSprint' as the repository will auto-generate the id.
     *
     * @param projectId The id of the project to which the sprint belongs.
     *                  Expected to be verified for existence beforehand.
     * @param sprintCreateDto The 'SprintCreateDto' containing sprint data.
     * @return A 'Sprint' object.
     */
    private Sprint convertSprintDtoToModelCreate(Long projectId, SprintCreateDto sprintCreateDto) {

        Project project = projectRepository.getReferenceById(projectId);

        String sprintName = sprintCreateDto.getName();
        String description = sprintCreateDto.getDescription();
        LocalDateTime startDate = sprintCreateDto.getStartDate();
        LocalDateTime endDate = sprintCreateDto.getEndDate();
        return Sprint.createSprint(null, project, sprintName, description, startDate, endDate);
    }

    /**
     * Converts a 'SprintUpdateDto' object to a 'Sprint' object during the update process of a 'Sprint'.
     *
     * @param sprintId The id of the sprint to be updated.
     *                 Expected to be verified for its existence within the project beforehand.
     * @param sprintUpdateDto The 'SprintUpdateDto' containing updated sprint data.
     * @return An updated 'Sprint' object.
     */
    private Sprint convertSprintDtoToModelUpdate(Long sprintId, SprintUpdateDto sprintUpdateDto) {
        Sprint sprint = sprintRepository.getReferenceById(sprintId);
        sprint.update(sprintUpdateDto.getName()
                , sprintUpdateDto.getDescription()
                , sprintUpdateDto.getStartDate()
                , sprintUpdateDto.getEndDate());
        return sprint;
    }

    /**
     * Fetches all the archived sprints associated with the 'Project' identified by the provided id.
     *
     * @param projectId The id of the project for which archived sprints are to be retrieved.
     *                  Expected to be verified for existence beforehand.
     * @return A set of 'SprintUpdateDto' representing the archived sprints.
     */
    public Set<SprintUpdateDto> getArchivedSprints(Long projectId) {
        return sprintRepository.findByProjectIdAndArchivedTrue(projectId).stream()
                .map(SprintUpdateDto::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Fetches the issue history of an archived 'Sprint' identified by the provided id.
     *
     * @param sprintId The id of the archived sprint for which the issue history is to be retrieved.
     *                 Expected to be verified for existence within the project beforehand.
     * @return A set of 'SprintIssueHistoryDto' representing the issue history of the archived sprint.
     * @throws InvalidSprintArgumentException If the specified sprint is not archived.
     */
    public Set<SprintIssueHistoryDto> getSprintIssueHistory(Long sprintId) {
        Optional<Sprint> sprintOptional = sprintRepository.findByIdAndArchivedTrue(sprintId);
        if (sprintOptional.isEmpty()) {
            throw new InvalidSprintArgumentException("the sprint is not archived");
        }
        return sprintIssueHistoryRepository
                .findAllByArchivedSprintId(sprintId).stream()
                .map(SprintIssueHistoryDto::new)
                .collect(Collectors.toSet());
    }


}
