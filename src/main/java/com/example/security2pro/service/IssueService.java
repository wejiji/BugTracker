package com.example.security2pro.service;

import com.example.security2pro.exception.directmessageconcretes.InvalidSprintArgumentException;
import com.example.security2pro.exception.directmessageconcretes.NotExistException;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.domain.model.issue.IssueRelation;
import com.example.security2pro.dto.issue.*;
import com.example.security2pro.dto.issue.onetomany.*;
import com.example.security2pro.repository.repository_interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class IssueService {
    /*
     * 'Issue' has two JPA bidirectional relationships: 'IssueRelation' and 'Comment' as child entities.
     * Cascade type is CascadeType.ALL, and orphanRemoval is set to true.
     * Since the life cycle of the two child entities depends entirely on 'Issue',
     * creation, update, and deletion are managed by adding, updating, and deleting from the collection field of an 'Issue'.
     *
     * 'IssueService' includes methods for processing 'IssueRelation,' including fetching the 'IssueRelation' set of an 'Issue'.
     * Methods dealing with 'Comment' are managed within the 'CommentService' class.
     */


    private final IssueRepository issueRepository;

    private final SprintRepository sprintRepository;

    private final UserRepository userRepository;

    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;


    public Set<IssueSimpleDto> getUserIssues(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(
                        () -> new NotExistException(
                                "user with username" + username + " does not exist"));
        return issueRepository
                .findActiveIssueByUsername(user.getUsername())
                .stream()
                .map(IssueSimpleDto::new)
                .collect(Collectors.toSet());
    }


    /**
     * Retrieves non-archived issues for the specified project.
     * The 'projectId' is expected to be verified for existence beforehand.
     *
     * @param projectId The id of the project for which non-archived issues are to be retrieved.
     * @return A set of 'IssueSimpleDto' representing non-archived issues in the specified project.
     */
    public Set<IssueSimpleDto> getActiveIssues(Long projectId) {
        return issueRepository.findByProjectIdAndArchivedFalse(projectId)
                .stream()
                .map(IssueSimpleDto::new)
                .collect(Collectors.toSet());
    }


    /**
     * Retrieves active issues associated with the specified sprint.
     * The 'sprintId' is expected to be verified for existence within the project beforehand.
     *
     * @param sprintId The id of the sprint for which non-archived issues are to be retrieved.
     * @return A set of 'IssueSimpleDto' representing non-archived issues associated with the specified sprint.
     * @throws InvalidSprintArgumentException If the specified sprint is archived.
     */
    public Set<IssueSimpleDto> getActiveIssuesBySprintId(Long sprintId) {
        Optional<Sprint> sprintOptional = sprintRepository.findByIdAndArchivedFalse(sprintId);
        if (sprintOptional.isEmpty()) {
            throw new InvalidSprintArgumentException("the sprint is not active");
        }
        return issueRepository.findByCurrentSprintId(sprintId)
                .stream()
                .map(IssueSimpleDto::new)
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves the 'IssueUpdatedDto' for the specified 'issueId'.
     *
     * @param issueId The id of the 'Issue' for which the data is requested.
     *                Expected to be verified for existence within the project beforehand.
     * @return 'IssueUpdatedDto' with the found 'Issue' data.
     */
    public IssueUpdateDto getIssueSimple(Long issueId) {
        Optional<Issue> foundIssue = issueRepository.findById(issueId);
        return new IssueUpdateDto(foundIssue.get());
    }


    /**
     * Creates a new 'Issue' based on the provided 'IssueCreateDto'.
     * The project id in the 'IssueCreateDto' is expected to be verified for existence beforehand.
     *
     * @param issueCreateDto The data for creating the 'Issue'.
     * @return An 'IssueUpdateDto' representing the created 'Issue' with its updated information.
     */
    public IssueUpdateDto createIssueFromSimpleDto(IssueCreateDto issueCreateDto) {
        Issue issue = convertToIssueModelToCreate(issueCreateDto);
        Issue newIssue = issueRepository.save(issue);

        return new IssueUpdateDto(newIssue);
    }


    /**
     * Updates an existing 'Issue' based on the provided 'IssueUpdateDto'.
     * The 'issueId' in the 'IssueUpdateDto' is expected to be verified for existence within the project beforehand.
     *
     * @param issueUpdateDto The data for updating the 'Issue'.
     * @return An 'IssueUpdateDto' representing the updated 'Issue' with its modified information.
     */
    public IssueUpdateDto updateIssueFromSimpleDto(IssueUpdateDto issueUpdateDto) {
        Issue issue = convertToIssueModelToUpdate(issueUpdateDto);
        Issue updatedIssue = issueRepository.save(issue);

        return new IssueUpdateDto(updatedIssue);
    }

    public void deleteByIdsInBulk(Set<Long> issueIds) {
        issueRepository.deleteAllByIdInBatch(issueIds);
    }

    public void deleteById(Long issueId) {
        issueRepository.deleteById(issueId);
    }

    /**
     * Creates or updates an 'IssueRelation' by modifying the collection field of its parent entity ('Issue').
     * Throws an exception when the causeIssue's id does not exist within the project.
     *
     * @param issueId          The id of the 'Issue' for which the relation is created or updated.
     *                         Expected to be verified for existence within the project beforehand.
     * @param issueRelationDto The data for creating or updating the 'IssueRelation'.
     * @return The 'IssueRelationDto' representing the created or updated relation.
     */
    public IssueRelationDto createOrUpdateIssueRelation(Long issueId, IssueRelationDto issueRelationDto) {
        Issue issue = issueRepository.findByIdWithIssueRelationSet(issueId).get();
        Issue causeIssue
                = issueRepository.getReferenceById(
                issueRelationDto.getCauseIssueId());
        if (!causeIssue.getProject().getId().equals(issue.getProject().getId())) {
            throw new NotExistException(
                    "The cause issue does not exist within the project with id" + causeIssue.getProject().getId());
        }
        issue.addIssueRelation(
                IssueRelation.createIssueRelation(
                        issue, causeIssue, issueRelationDto.getRelationDescription()));
        return issueRelationDto;
    }


    /**
     * Deletes the 'IssueRelation' between the 'Issue' with the provided issueId and the 'causeIssue' with the provided causeIssueId.
     *
     * @param issueId      The id of the 'Issue' for which the relation is deleted.
     * @param causeIssueId The id of the 'causeIssue' in the relation.
     */
    public void deleteIssueRelation(Long issueId, Long causeIssueId) {
        Issue issue = issueRepository.findByIdWithIssueRelationSet(issueId).get();
        issue.deleteIssueRelation(causeIssueId);
    }


    /**
     * Fetches and returns all the 'IssueRelations' of the 'Issue' with the provided id.
     *
     * @param affectedIssueId The id of the affected 'Issue' expected to be verified for existence within the project beforehand.
     * @return 'IssueRelations' of the 'Issue' with the provided id as a Set of 'IssueRelationDto'.
     */
    public Set<IssueRelationDto> findAllByAffectedIssueId(Long affectedIssueId) {

        Issue issue = issueRepository.findByIdWithIssueRelationSet(affectedIssueId).get();
        return issue.getIssueRelationSet()
                .stream()
                .map(IssueRelationDto::new)
                .collect(Collectors.toSet());
    }

    /**
     * Converts 'IssueCreateDto' to a new 'Issue' and returns the created 'Issue' instance with null id.
     *
     * @param issueCreateDto The project id of 'IssueCreateDto' is expected to be verified for existence beforehand.
     * @return A created 'Issue' instance.
     */
    private Issue convertToIssueModelToCreate(IssueCreateDto issueCreateDto) {
        Project project = projectRepository.getReferenceById(
                issueCreateDto.getProjectId().get());

        Sprint sprint = null;
        if (issueCreateDto.getCurrentSprintId() != null) {
            sprint = getValidatedSprint(issueCreateDto.getCurrentSprintId(), project.getId());
        }
        Set<User> foundAssigneeUsers = getValidatedUsers(issueCreateDto.getAssignees(), project.getId());

        return Issue.createIssue(null
                , project
                , foundAssigneeUsers
                , issueCreateDto.getTitle()
                , issueCreateDto.getDescription()
                , issueCreateDto.getPriority()
                , issueCreateDto.getStatus()
                , issueCreateDto.getType()
                , sprint);
    }

    /**
     * Converts 'IssueUpdateDto' to an updated 'Issue' and returns the updated 'Issue' instance.
     *
     * @param issueUpdateDto The 'issueId' of 'IssueUpdateDto' is expected to be verified for existence within the project beforehand.
     * @return An updated 'Issue' instance.
     */
    private Issue convertToIssueModelToUpdate(IssueUpdateDto issueUpdateDto) {
        Issue issue = issueRepository.getReferenceById(issueUpdateDto.getIssueId());
        Project project = issue.getProject();
        Sprint sprint = null;
        if (issueUpdateDto.getCurrentSprintId() != null) {
            sprint = getValidatedSprint(issueUpdateDto.getCurrentSprintId(), project.getId());
        }

        Set<User> foundAssigneeUsers
                = getValidatedUsers(issueUpdateDto.getAssignees(), project.getId());

        return issue.detailUpdate(
                issueUpdateDto.getTitle()
                , issueUpdateDto.getDescription()
                , issueUpdateDto.getPriority()
                , issueUpdateDto.getStatus()
                , issueUpdateDto.getType()
                , sprint
                , foundAssigneeUsers);
    }


    /**
     * Retrieves and validates the 'Sprint' based on the provided sprintId and projectId.
     *
     * @param sprintId  The id of the sprint in which the new issue is created.
     * @param projectId The id of the project in which the new issue is created.
     * @return The 'Sprint' verified for existence and being non-archived within the project.
     * @throws InvalidSprintArgumentException If the sprint does not exist within the specified project or is archived.
     */
    private Sprint getValidatedSprint(Long sprintId, Long projectId) {
        return sprintRepository.findByIdAndProjectIdAndArchivedFalse(sprintId, projectId)
                .orElseThrow(
                        () -> new InvalidSprintArgumentException(
                                " the sprint does not exist within the project with id" + projectId +
                                " or not active anymore"));
    }

    /**
     * Fetches and returns 'Users' verified for existence identified by the provided usernames.
     * Throws an exception when non-existent usernames or users who are not members of the project are passed.
     *
     * @param passedAssigneesUsernames The usernames to be verified for existence and for their membership of the project.
     * @param projectId                The id of the project in which the new issue is created.
     * @return 'Users' verified for existence and membership in the project.
     * @throws NotExistException If some passed assignees do not exist or are not members of the project.
     */
    private Set<User> getValidatedUsers(Set<String> passedAssigneesUsernames, Long projectId) {

        if (passedAssigneesUsernames == null || passedAssigneesUsernames.isEmpty()) {
            return Collections.emptySet();
        }
        Set<User> foundAssigneeUsers = projectMemberRepository
                .findAllByUsernameAndProjectIdWithUser(
                        passedAssigneesUsernames, projectId)
                .stream().map(ProjectMember::getUser)
                .collect(Collectors.toCollection(HashSet::new));

        log.info("passed assignees = " + passedAssigneesUsernames);
        log.info("found assignees= " + foundAssigneeUsers.stream().map(User::getUsername).collect(Collectors.toSet()));

        if (passedAssigneesUsernames.size() != foundAssigneeUsers.size()) {
            throw new NotExistException("some passed assignees are not the member of this project");
        }
        return foundAssigneeUsers;
    }


}
