package com.example.security2pro;

import com.example.security2pro.repository.*;
import com.example.security2pro.service.IssueService;
import com.example.security2pro.service.ProjectService;
import com.example.security2pro.service.SprintService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@Transactional
//@RequiredArgsConstructor
public class MainTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private IssueService issueService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SprintRepository sprintRepository;
    @Autowired
    private SprintIssueHistoryRepository sprintIssueHistoryRepository;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private IssueRelationRepository issueRelationRepository;



//    @PostMapping("/setup")
//    public List<Object> testIssueDto2() {
//        User user1 = new User("mj", passwordEncoder.encode("123"), "Mija", "Lee", "lmj@gmail.com"
//                , Set.of(Role.valueOf("ROLE_TEAM_LEAD")), true);
//        User user2 = new User("uu", passwordEncoder.encode("124"), "Ui", "joo", "ju@gmail.com"
//                , Set.of(Role.valueOf("ROLE_TEAM_MEMBER")), true);
//        User user3 = new User("oo", passwordEncoder.encode("124"), "OOkoko", "koko", "koko@gmail.com"
//                , Set.of(Role.valueOf("ROLE_TEAM_MEMBER")), true);
//        User user4 = new User("nn", passwordEncoder.encode("124"), "nn", "nn", "nn@gmail.com"
//                , Set.of(Role.valueOf("ROLE_TEAM_MEMBER")), true);
//        User user5 = new User("561o", passwordEncoder.encode("124"), "561o", "561o", "561o@gmail.com"
//                , Set.of(Role.valueOf("ROLE_TEAM_MEMBER")), true);
//
//        userRepository.save(user1);
//
//        ProjectCreateDto projectCreationForm1= new ProjectCreateDto("project3","third project to be created to test services");
//        Project newProject =null;
//        Long projectId = null;
//        if(user1.getAuthorities().contains(Role.ROLE_TEAM_LEAD)) {
//            newProject = projectService.startProject(projectCreationForm1, user1);
//            projectId = newProject.getId();
//            System.out.println(projectId + " is the generated project's id");
//            ProjectCreateDto projectCreationForm2 = new ProjectCreateDto(newProject);
//            System.out.println(projectCreationForm2);
//        } // new Project and ProjectMember are persisted(save)
//
//        em.flush();
//        //flush before querying DB
//        //below are all jpql query (will query DB not find from cache)
//
//        ProjectMember projectMember2 = new ProjectMember(newProject,user2,new HashSet<>(List.of(Role.ROLE_PROJECT_MEMBER)));
//        ProjectMember projectMember3 = new ProjectMember(newProject,user3,new HashSet<>(List.of(Role.ROLE_PROJECT_MEMBER)));
//        ProjectMember projectMember4 = new ProjectMember(newProject,user4,new HashSet<>(List.of(Role.ROLE_PROJECT_MEMBER)));
//        ProjectMember projectMember5 = new ProjectMember(newProject,user5,new HashSet<>(List.of(Role.ROLE_PROJECT_MEMBER)));
//
//        em.persist(user2);
//        em.persist(user3);
//        em.persist(user4);
//        em.persist(user5);
//        em.persist(projectMember2);
//        em.persist(projectMember3);
//        em.persist(projectMember4);
//        em.persist(projectMember5);
//
//        Issue issue1 = new Issue(null, newProject, new HashSet<>(Arrays.asList(user1,user2,user3,user4,user5)) , "issue1", "complete this project", LocalDateTime.now().plus(14, ChronoUnit.DAYS)
//                , IssuePriority.HIGH, IssueStatus.TODO, IssueType.NEW_FEATURE, null);
//        Issue issue2 = new Issue(null, newProject, new HashSet<>(Arrays.asList(user1)) , "issue2", "auth issue", LocalDateTime.now().plus(4, ChronoUnit.DAYS)
//                , IssuePriority.HIGHEST, IssueStatus.IN_PROGRESS, IssueType.BUG, null);
//        Issue issue3 = new Issue(null, newProject, new HashSet<>(Arrays.asList(user1)), "issue3", "controller3 issue", LocalDateTime.now().plus(2, ChronoUnit.DAYS)
//                , IssuePriority.MEDIUM, IssueStatus.IN_PROGRESS, IssueType.BUG, null);
//
//
//        IssueCreateDto issueDto1 = new IssueCreateDto(issue1.getProject().getId(),issue1.getTitle(), issue1.getDescription(),issue1.getAssigneesNames(), issue1.getCompleteDate(), issue1.getPriority(), issue1.getStatus(), issue1.getType(), issue1.getCurrentSprint().isEmpty()? null : issue1.getCurrentSprint().get().getId(), Collections.emptySet());
//        IssueCreateDto issueDto2 = new IssueCreateDto(issue2.getProject().getId(),issue2.getTitle(), issue2.getDescription(),issue2.getAssigneesNames(), issue2.getCompleteDate(), issue2.getPriority(), issue2.getStatus(), issue2.getType(), issue2.getCurrentSprint().isEmpty()? null : issue2.getCurrentSprint().get().getId(), Collections.emptySet());
//        IssueCreateDto issueDto3 = new IssueCreateDto(issue3.getProject().getId(),issue3.getTitle(), issue3.getDescription(),issue3.getAssigneesNames(), issue3.getCompleteDate(), issue3.getPriority(), issue3.getStatus(), issue3.getType(), issue3.getCurrentSprint().isEmpty()? null : issue3.getCurrentSprint().get().getId(), Collections.emptySet());
//
//        IssueUpdateResponseDto issueUpdateDto1=issueService.createIssueDetailFromDto( issueDto1);
//        IssueUpdateResponseDto issueUpdateDto2=issueService.createIssueDetailFromDto( issueDto2);
//        IssueUpdateResponseDto issueUpdateDto3=issueService.createIssueDetailFromDto( issueDto3);
//
//
//        //        IssueDto issueDto4 = new IssueDto(issue1,Collections.emptyList(),Set.of(issueRelation1,issueRelation2));
//
//        Sprint sprint1 = new Sprint(newProject, "sprint1", "the very first sprint. auth issue and other basic features to be completed",
//                LocalDateTime.now(), LocalDateTime.now().plus(40, ChronoUnit.SECONDS));
//
//
//        sprintRepository.save(sprint1);
//        // for this to work, issue1 has to be the right reference to the persisted one!!!!!!
//        em.flush();
//
//        IssueRelation issueRelation = new IssueRelation(null,issue2,issue1, "issue1 is too big object");
//        IssueRelation issueRelation2 = new IssueRelation(null,issue2,issue1, " issue1 is too big object ...!");
//
//        IssueUpdateDto issueUpdateDto1Request = new IssueUpdateDto(issueRepository.getReferenceById(issueUpdateDto1.getIssueId()), Collections.emptySet(), new HashSet<>(List.of()));
//        IssueUpdateDto issueUpdateDto2Request = new IssueUpdateDto(issueRepository.getReferenceById(issueUpdateDto2.getIssueId()),Collections.emptySet(), new HashSet<>(List.of(issueRelation)));
//        IssueUpdateDto issueUpdateDto3Request = new IssueUpdateDto(issueRepository.getReferenceById(issueUpdateDto3.getIssueId()),Collections.emptySet(), new HashSet<>(List.of(issueRelation2)));
//
//
//        issueUpdateDto1 = issueService.getIssueWithDetails(issueUpdateDto1.getIssueId());
//        issueUpdateDto1.setCurrentSprintId(sprint1.getId());
//        IssueUpdateResponseDto issueUpdateResponseDto1= issueService.updateIssueDetailFromDto(issueUpdateDto1Request);
//        IssueUpdateResponseDto issueUpdateResponseDto2 = issueService.updateIssueDetailFromDto(issueUpdateDto2Request);
//        IssueUpdateResponseDto issueUpdateResponseDto3 = issueService.updateIssueDetailFromDto(issueUpdateDto3Request);
//
//
////        issueRelationRepository.saveAllAndFlush(Set.of(issueRelation1,issueRelation2));
////        Issue issue= issueService.getReferenceById(issue1.getId());
////        return new IssueDto(issue,new ArrayList<>(),new HashSet<>(Arrays.asList(issueRelation1,issueRelation2)));
//        return List.of( issueUpdateDto2, issueUpdateDto3);
//    }
//
//    @GetMapping("/issue-detail2")
//    public IssueUpdateResponseDto testIssueDto22()  {
//        System.out.println(issueService.getIssueWithDetails(1L));
//
//        return issueService.getIssueWithDetails(1L);
//    }
//



//    @PostMapping("/issue-update2")
//    public IssueDto updateIssueDto2() {
//
//        Issue issue1 = issueService.getReferenceById(1L);
//        Issue issue2 = issueService.getReferenceById(2L);
//        Issue issue3 = issueService.getReferenceById(3L);
//
//        IssueRelation issueRelation1 = new IssueRelation(null,issue1, issue2, "issue2 has to be resolved fist to complete issue1");
//        IssueRelation issueRelation2 = new IssueRelation(null,issue1, issue3, " issue3 has to be also resolved to end this project!... (issue1)");
////        IssueRelationDto issueRelationDto1 = new IssueRelationDto(issueRelation1);
////        IssueRelationDto issueRelationDto2 = new IssueRelationDto(issueRelation2);
//
//        IssueDto issueDtoPrev = issueService.getIssueWithDetails(1L);
//
//        System.out.println("==================test");
//        issueDtoPrev.getActivityDtoList().stream().forEach(System.out::println);
//        System.out.println("==================test");
//
//
//        IssueDto issueDto = new IssueDto(issue1,Collections.emptySet(),new HashSet<>(Arrays.asList(issueRelation1,issueRelation2)));
//        issueDto.setActivityDtoList(issueDtoPrev.getActivityDtoList());
//
//
//        return issueService.updateIssueFromDto(issue1.getProject().getId(),issueDto);
//    }



//    @PostMapping("/assignee-test")
//    @PreAuthorize("hasRole('ADMIN')")
//    public void assigneeTest(){
//
//        Issue issue= issueRepository.getReferenceById(1L);
//
//        issue.getAssignees().stream().forEach(user-> System.out.println(","+user.getUsername()));
//
//        User user1 = userRepository.findUserByUsername("mj").get();
//        User user2 = userRepository.findUserByUsername("uu").get();
//        User user3 = userRepository.findUserByUsername("oo").get();
//        User user4 = userRepository.findUserByUsername("nn").get();
//        User user5 = userRepository.findUserByUsername("561o").get();
//
//        issue.setAssignees(new HashSet<>(List.of(user1,user2,user3)));
//
//        em.flush();
//
//
//        System.out.println("after -------- ");
//        issue = issueRepository.getReferenceById(1L);
//        issue.getAssignees().stream().forEach(user-> System.out.println(","+user.getUsername()));
//
//        Issue issue4 = new Issue(null, issue.getProject(), new HashSet<>(Arrays.asList(user1)), "issue4", "4th issue", LocalDateTime.now().plus(2, ChronoUnit.DAYS)
//                , IssuePriority.HIGH, IssueStatus.IN_REVIEW, IssueType.BUG, null);
//
//        em.persist(issue4);
//        IssueRelation issueRelation3 = new IssueRelation(null,issue, issue4, " 4 th issue  ! !!! !..");
//
//
//
//        //issue.setIssueRelations(new HashSet<>(List.of(issueRelation3)));
//
//        em.flush();
//        issue = issueRepository.getReferenceById(1L);
//
//    }




    @Autowired
    private ProjectRepository projectRepository;







}