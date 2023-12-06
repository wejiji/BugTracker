package com.example.security2pro.service;
import com.example.security2pro.domain.enums.ActivityType;
import com.example.security2pro.domain.enums.IssuePriority;
import com.example.security2pro.domain.enums.IssueStatus;
import com.example.security2pro.domain.enums.IssueType;
import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.*;
import com.example.security2pro.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class IssueService {

    private final IssueRepository issueRepository;

    private final ActivityRepository activityRepository;

    private final IssueRelationRepository issueRelationRepository;

    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;

    private final SprintRepository sprintRepository;

    private final UserRepository userRepository;


    private final ProjectService projectService;

    public Set<Issue> findIssuesByCurrentSprintId(Long sprintId){
        return issueRepository.findIssuesByCurrentSprintId(sprintId);
        //return issues that are not archived/ belong to the given sprint
        // the sprint should not be archived as well.  (current sprint cannot be the archived one)
    }

    public Issue getReferenceById(Long issueId){
        return issueRepository.getReferenceById(issueId);
        //throws exception when issue has to exist
    }

    public void deleteById(Long issueId){
        //activities need to be deleted first
        Set<Long> idsToBeDeleted= activityRepository.findByIssueId(issueId).stream().map(activity -> activity.getId()).collect(Collectors.toCollection(HashSet::new));
        activityRepository.deleteAllByIdInBatch(idsToBeDeleted);
        Set<Long> relations1 =issueRelationRepository.findAllByAffectedIssueId(issueId).stream().map(IssueRelation::getId).collect(Collectors.toCollection(HashSet::new));
        Set<Long> relations2 =issueRelationRepository.findAllByCauseIssueId(issueId).stream().map(IssueRelation::getId).collect(Collectors.toCollection(HashSet::new));
        relations1.addAll(relations2);
        issueRelationRepository.deleteAllByIdInBatch(relations1);
        issueRepository.deleteById(issueId);
    }

    public Set<IssueDto> getUserIssues(Long userId){
        User user = userRepository.getReferenceById(userId);
        log.info("getting user with the username" + user.getUsername());

        return issueRepository.findActiveIssueByAssignee(userId).stream().map(issue -> new IssueDto(issue,Collections.emptySet(),Collections.emptySet())).collect(Collectors.toSet());
    }

    public ProjectDto getProjectDataToCreateIssue(Long projectId){
        return projectService.getProjectDetails(projectId);
    }

    public IssueUpdateDto getIssueAndProjectDataToUpdate(Long projectId, Long issueId){

        Optional<Issue> issueOptional = issueRepository.findIssueWithAssignees(issueId);
        if(issueOptional.isEmpty()){
            throw new IllegalArgumentException("issue with id"+ issueId +" does not exist");
        }
        Issue issue = issueOptional.get();
        ProjectDto projectDto = projectService.getProjectDetails(projectId);

        Set<IssueRelation> issueRelations= issueRelationRepository.findByAffectedIssue(issue.getId());
        Set<Activity> activities = activityRepository.findByIssueId(issue.getId());
        IssueDto issueDto = new IssueDto(issue,activities,issueRelations);

        return new IssueUpdateDto(issueDto, projectDto);
    }

    public IssueDto getIssueWithDetails(Long issueId) throws InvocationTargetException, IllegalAccessException {
        Optional<Issue> foundIssue = issueRepository.findIssueWithAssignees(issueId); //relations and assignees join fetch
        if(foundIssue.isEmpty()){
            throw new IllegalArgumentException("issue with id "+ issueId +" not found" );
        }
        Issue issue = foundIssue.get();
        Set<IssueRelation> issueRelations= issueRelationRepository.findByAffectedIssue(issue.getId());
        Set<Activity> activities = activityRepository.findByIssueId(issue.getId());
        return new IssueDto(issue,activities,issueRelations);
    }

    public IssueDto updateIssueFromDto(Long projectId, IssueDto issueDto) throws InvocationTargetException, IllegalAccessException {
        //IssueDto with Id -> Issue
        Issue issue = convertToIssueModelToUpdate(projectId,issueDto);
        Issue updatedIssue= issueRepository.save(issue);

        //the ones that were not passed need to be removed....
        Set<IssueRelation> previousIssueRelationList= issueRelationRepository.findByAffectedIssue(issue.getId()); // all previous
        Set<Long> previousIds =previousIssueRelationList.stream().map(issueRelation -> issueRelation.getId()).collect(Collectors.toCollection(HashSet::new));
        Set<IssueRelationDto> passedIssueRelationList =issueDto.getIssueRelationDtoList();
        Set<Long> passedIssueRelationIds= passedIssueRelationList.stream().map(issueRelationDto -> issueRelationDto.getId()).collect(Collectors.toCollection(HashSet::new));
        previousIds.removeAll(passedIssueRelationIds);
        issueRelationRepository.deleteAllByIdInBatch(previousIds);

        Set<IssueRelation> issueRelationList = convertToIssueRelationModel(issue,passedIssueRelationList);
        issueRelationList =issueRelationRepository.saveAll(issueRelationList).stream().collect(Collectors.toSet());

        Set<Activity> activityList = convertToActivityModel(updatedIssue, issueDto.getActivityDtoList());
        activityList=activityRepository.saveAll(activityList).stream().collect(Collectors.toCollection(HashSet::new)); // activities that are not histories
        Set<Activity> issueHistoryActivityList=activityRepository.findIssueHistoryByIssueId(updatedIssue.getId());
        activityList.addAll(issueHistoryActivityList);
        return new IssueDto(updatedIssue,activityList,issueRelationList);
    }

    public IssueDto createIssueFromDto(Long projectId, IssueDto issueDto) throws InvocationTargetException, IllegalAccessException {
        //IssueDto with no Id -> Issue
        if(issueDto.getIssueId()!=null && issueRepository.findById(issueDto.getIssueId()).isPresent()){throw new IllegalArgumentException("issue id "+ issueDto.getIssueId()+" already exists");}
        Issue issue = convertToIssueModelToCreate(projectId,issueDto);

        Set<IssueRelation> issueRelationList = convertToIssueRelationModel(issue,issueDto.getIssueRelationDtoList());
        issueRelationList =issueRelationRepository.saveAll(issueRelationList).stream().collect(Collectors.toSet());
       Issue newIssue= issueRepository.save(issue);

        return new IssueDto(newIssue,Collections.emptySet(),issueRelationList);
    }
    private Issue convertToIssueModelToCreate(Long projectId, IssueDto issueDto)  {
        //assignee detail update is not possible. - can be moved to or out of an issue

        Issue newIssue = convertToIssueModelCommon(projectId,issueDto);

        return new Issue(null,newIssue.getProject(),newIssue.getAssignees(),newIssue.getTitle(), newIssue.getDescription(), newIssue.getCompleteDate(),newIssue.getPriority(),newIssue.getStatus(),newIssue.getType(),newIssue.getCurrentSprint());
   }

    private Issue convertToIssueModelToUpdate(Long projectId, IssueDto issueDto) {
        Issue issue =issueRepository.getReferenceById(issueDto.getIssueId());
        log.info("updating issue :" + issue.getTitle());

        if(!issue.getProject().getId().equals(projectId)){throw new IllegalArgumentException("issue does not belong to the project with id " +projectId);}

        issue.changeStatus(issueDto.getStatus()); // set complete date if status becomes DONE from other statuses
        Issue newIssue =convertToIssueModelCommon(projectId,issueDto);

        return new Issue(issue.getId(),newIssue.getProject(),newIssue.getAssignees(),newIssue.getTitle(), newIssue.getDescription(), issue.getCompleteDate(),newIssue.getPriority(),issue.getStatus(),newIssue.getType(),newIssue.getCurrentSprint());
    }


    private Issue convertToIssueModelCommon(Long projectId, IssueDto issueDto) {

        Project project = projectRepository.getReferenceById(projectId);
        log.info("updating issue of the project: "+project.getName());

        Set<String> passedAssigneesUsernames= issueDto.getAssignees();
        Set<User> foundAssigneeUsers =projectMemberRepository
                .findAllByIdAndProjectIdWithUser(passedAssigneesUsernames,project.getId())
                .stream().map(ProjectMember::getUser).collect(Collectors.toCollection(HashSet::new));

        if(passedAssigneesUsernames.size()!=foundAssigneeUsers.size()){
            throw new IllegalArgumentException("some passed assignees do not exist for this issue");
        }

        Sprint sprint = null;
        Long currentSprintId = issueDto.getCurrentSprintId();
        if(issueDto.getCurrentSprintId()!=null){
            sprint = sprintRepository.getReferenceById(currentSprintId);
        }

        String title = issueDto.getTitle();
        String description = issueDto.getDescription();
        LocalDateTime completeDate = issueDto.getCompleteDate();
        IssuePriority priority = issueDto.getPriority();
        IssueStatus status = issueDto.getStatus();
        IssueType type = issueDto.getType();

        if(status.equals(IssueStatus.DONE) && !completeDate.isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("complete date cannot be set to future for the issues with 'DONE' status");
        }

        return new Issue(null,project,foundAssigneeUsers, title, description, completeDate, priority, status, type, sprint);
    }


    private Set<Activity> convertToActivityModel(Issue issue, Set<ActivityDto> activityDtos){

        Set<Long> passedActivityIds =activityDtos.stream().map(ActivityDto::getId).filter(Objects::nonNull).collect(Collectors.toCollection(HashSet::new));
        List<Activity> foundActivities = activityRepository.findAllById(passedActivityIds);
        if(passedActivityIds.size() != foundActivities.size()){
            throw new IllegalArgumentException("some passed activities do not exist for this issue");
        }
        // not used for now?..
        return activityDtos.stream()
                .filter(activityDto -> !activityDto.getType().equals(ActivityType.ISSUE_HISTORY))
                .map(activityDto -> new Activity(activityDto.getId(),issue.getProject(),issue,activityDto.getType(),activityDto.getDescription()))
                .collect(Collectors.toSet());
    }

    private Set<IssueRelation> convertToIssueRelationModel(Issue issue, Set<IssueRelationDto> issueRelationDtos) {
        //converts IssueRelationDto to IssueRelations
        Set<Long> passedIssueRelationIds=issueRelationDtos.stream().map(IssueRelationDto::getId).filter(Objects::nonNull).collect(Collectors.toCollection(HashSet::new));
        Set<IssueRelation> foundIssueRelations = issueRelationRepository.findAllByIdAndAffectedIssueId(passedIssueRelationIds,issue.getId());
        Set<Long> passedCauseIssueIds=issueRelationDtos.stream().map(IssueRelationDto::getCauseIssueId).collect(Collectors.toCollection(HashSet::new));
        List<Issue> foundCauseIssues = issueRepository.findAllById(passedCauseIssueIds);

        if(passedCauseIssueIds.size()!=foundCauseIssues.size()){
            throw new IllegalArgumentException("some cause issues not found within the project");
        }
        //get all the issue relations regarding the issue and compare-
        Map<Long,List<IssueRelation>> issueRelationsMap = foundIssueRelations.stream().collect(groupingBy(IssueRelation::getId));// check if all the ids are included here
        Map<Long,List<Issue>> causeIssuesMap = foundCauseIssues.stream().collect(groupingBy(Issue::getId));//check if all the cause issue ids are included here. only active issues can be cause issue

        return issueRelationDtos.stream()
                .map(issueRelationDto -> {
                    Long issueRelationId= issueRelationDto.getId(); // if id is not null -> update only description, if id is null, update everything
                    Long causeIssueId = issueRelationDto.getCauseIssueId();
                    if(causeIssueId.equals(issue.getId())){throw new IllegalArgumentException("an issue cannot have itself as a related issue");}
                    if(issueRelationId!=null){
                        if(!issueRelationsMap.containsKey(issueRelationId)){
                            throw new IllegalArgumentException("issue relationship not found for this issue");}
                        if(!causeIssueId.equals(issueRelationsMap.get(issueRelationId).get(0).getCauseIssue().getId())){
                            throw new IllegalArgumentException("cause issue id is not updatable");
                        }
                    } else if(causeIssuesMap.get(causeIssueId).get(0).getStatus().equals(IssueStatus.DONE)){
                        throw new IllegalArgumentException("already finished issue cannot be newly added as a cause issue");}
                    //only existing issueRelations can have a cause issue that has 'DONE' status
                    return new IssueRelation(issueRelationId, issue, causeIssuesMap.get(causeIssueId).get(0), issueRelationDto.getRelationDescription());}
                ).collect(Collectors.toSet());
    }
}
