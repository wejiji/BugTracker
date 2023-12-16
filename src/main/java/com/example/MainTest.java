//package com.example;
//
//import com.example.security2pro.domain.enums.IssuePriority;
//import com.example.security2pro.domain.enums.IssueStatus;
//import com.example.security2pro.domain.enums.IssueType;
//import com.example.security2pro.domain.enums.Role;
//import com.example.security2pro.domain.model.*;
//import com.example.security2pro.dto.*;
//import com.example.security2pro.dto.issue.IssueCreateDto;
//import com.example.security2pro.dto.issue.IssueUpdateDto;
//import com.example.security2pro.dto.project.ProjectCreateDto;
//import com.example.security2pro.repository.*;
//import com.example.security2pro.service.IssueService;
//import com.example.security2pro.service.ProjectService;
//import com.example.security2pro.service.SprintService;
//import jakarta.persistence.EntityManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.validation.BindException;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.lang.reflect.InvocationTargetException;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.time.temporal.ChronoUnit;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static java.time.ZoneOffset.ofHours;
//@RestController
//@Transactional
////@RequiredArgsConstructor
//public class MainTest {
//    @Autowired
//    private EntityManager em;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    @Autowired
//    private ProjectService projectService;
//    @Autowired
//    private IssueService issueService;
//    @Autowired
//    private SprintService sprintService;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private SprintRepository sprintRepository;
//    @Autowired
//    private SprintIssueHistoryRepository sprintIssueHistoryRepository;
//    @Autowired
//    private IssueRepository issueRepository;
//    @Autowired
//    private IssueRelationRepository issueRelationRepository;
//
//
//
//    @PostMapping("/issue-detail2")
//    public List<IssueUpdateDto> testIssueDto2() {
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
//        Issue issue1 = new Issue(1L, newProject, new HashSet<>(Arrays.asList(user1,user2,user3,user4,user5)) , "issue1", "complete this project", LocalDateTime.now().plus(14, ChronoUnit.DAYS)
//                , IssuePriority.HIGH, IssueStatus.TODO, IssueType.NEW_FEATURE, null);
//        Issue issue2 = new Issue(2L, newProject, new HashSet<>(Arrays.asList(user1)) , "issue2", "auth issue", LocalDateTime.now().plus(4, ChronoUnit.DAYS)
//                , IssuePriority.HIGHEST, IssueStatus.IN_PROGRESS, IssueType.BUG, null);
//        Issue issue3 = new Issue(3L, newProject, new HashSet<>(Arrays.asList(user1)), "issue3", "controller3 issue", LocalDateTime.now().plus(2, ChronoUnit.DAYS)
//                , IssuePriority.MEDIUM, IssueStatus.IN_PROGRESS, IssueType.BUG, null);
//
//        IssueRelation issueRelation1 = new IssueRelation(null,issue1, issue2, "issue2 has to be resolved fist to complete issue1");
//        IssueRelation issueRelation2 = new IssueRelation(null,issue1, issue3, " issue3 has to be also resolved to end this project!... (issue1)");
//
//
//        IssueCreateDto issueDto1 = new IssueCreateDto(issue1,Collections.emptySet(),Collections.emptySet());
//        IssueCreateDto issueDto2 = new IssueCreateDto(issue2,Collections.emptySet(),Collections.emptySet());
//        IssueCreateDto issueDto3 = new IssueCreateDto(issue3,Collections.emptySet(),Collections.emptySet());
//
//        IssueUpdateDto issueUpdateDto1=issueService.createIssueDetailFromDto(newProject.getId(), issueDto1);
//        IssueUpdateDto issueUpdateDto2=issueService.createIssueDetailFromDto(newProject.getId(), issueDto2);
//        IssueUpdateDto issueUpdateDto3=issueService.createIssueDetailFromDto(newProject.getId(), issueDto3);
//
//
//        //        IssueDto issueDto4 = new IssueDto(issue1,Collections.emptyList(),Set.of(issueRelation1,issueRelation2));
//
//        Sprint sprint1 = new Sprint(newProject, "sprint1", "the very first sprint. auth issue and other basic features to be completed",
//                LocalDateTime.now(), LocalDateTime.now().plus(40, ChronoUnit.SECONDS));
//
//        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
//        threadPoolTaskScheduler.initialize();
//        threadPoolTaskScheduler.schedule(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("amount of time set elapsed");
//            }
//        },sprint1.getEndDate().toInstant(ofHours(7)));
//
//
//        sprintRepository.save(sprint1);
//        // for this to work, issue1 has to be the right reference to the persisted one!!!!!!
//        em.flush();
//
//        issueDto1 = new IssueUpdateDto(issue1,Collections.emptySet(),Set.of(issueRelation1,issueRelation2));
//        issueDto1.setCurrentSprintId(sprint1.getId());
//        issueDto1= issueService.updateIssueFromDto(newProject.getId(), issueDto1);
//
////        Issue issue= issueService.getReferenceById(issue1.getId());
////        return new IssueDto(issue,new ArrayList<>(),new HashSet<>(Arrays.asList(issueRelation1,issueRelation2)));
//        return List.of(issueDto1, issueDto2, issueDto3);
//    }
//
//    @GetMapping("/issue-detail2")
//    public IssueDto testIssueDto22() throws InvocationTargetException, IllegalAccessException {
//        System.out.println(issueService.getReferenceById(1L).getStatus());
//
//        return issueService.getIssueWithDetails(1L);
//    }
//
//    @PostMapping("/issue-update2")
//    public IssueDto updateIssueDto2() throws InvocationTargetException, IllegalAccessException {
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
//
//
//
//
//    @PostMapping("/issue-detail")
//    public IssueDto testIssueDto(){
//        User user1 = new User("mj", passwordEncoder.encode("123"), "Mija", "Lee", "lmj@gmail.com"
//                , Set.of(Role.valueOf("ROLE_TEAM_LEAD")), true);
//
//        User user2 = new User("uu", passwordEncoder.encode("124"), "Ui", "joo", "ju@gmail.com"
//                , Set.of(Role.valueOf("ROLE_TEAM_MEMBER")), true);
//
//        User user3 = new User("oo", passwordEncoder.encode("124"), "OOkoko", "koko", "koko@gmail.com"
//                , Set.of(Role.valueOf("ROLE_TEAM_MEMBER")), true);
//
//        User user4 = new User("nn", passwordEncoder.encode("124"), "nn", "nn", "nn@gmail.com"
//                , Set.of(Role.valueOf("ROLE_TEAM_MEMBER")), true);
//
//        User user5 = new User("561o", passwordEncoder.encode("124"), "561o", "561o", "561o@gmail.com"
//                , Set.of(Role.valueOf("ROLE_TEAM_MEMBER")), true);
//
//
//        userRepository.save(user1);
//
//        ProjectCreationForm projectCreationForm1= new ProjectCreationForm("project3","third project to be created to test services");
//        Project newProject =null;
//        Long projectId = null;
//        if(user1.getAuthorities().contains(Role.ROLE_TEAM_LEAD)) {
//            newProject = projectService.startProject(projectCreationForm1, user1);
//            projectId = newProject.getId();
//            System.out.println(projectId + " is the generated project's id");
//            ProjectCreationForm projectCreationForm2 = new ProjectCreationForm(newProject);
//            System.out.println(projectCreationForm2);
//        } // new Project and ProjectMember are persisted(save)
//
//        em.flush();
//        //flush before querying DB
//        //below are all jpql query (will query DB not find from cache)
//
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
//
//
//        Issue issue1 = new Issue(1L, newProject, new HashSet<>(Arrays.asList(user1,user2,user3,user4,user5)) , "issue1", "complete this project", LocalDateTime.now().plus(14, ChronoUnit.DAYS)
//                , IssuePriority.HIGH, IssueStatus.TODO, IssueType.NEW_FEATURE, null);
//
//        Issue issue2 = new Issue(2L, newProject, new HashSet<>(Arrays.asList(user1)) , "issue2", "auth issue", LocalDateTime.now().plus(4, ChronoUnit.DAYS)
//                , IssuePriority.HIGHEST, IssueStatus.IN_PROGRESS, IssueType.BUG, null);
//
//        Issue issue3 = new Issue(3L, newProject, new HashSet<>(Arrays.asList(user1)), "issue3", "controller3 issue", LocalDateTime.now().plus(2, ChronoUnit.DAYS)
//                , IssuePriority.MEDIUM, IssueStatus.IN_PROGRESS, IssueType.BUG, null);
//
//
//
//        Sprint sprint1 = new Sprint(newProject, "sprint1", "the very first sprint. auth issue and other basic features to be completed",
//                LocalDateTime.now(), LocalDateTime.now().plus(20, ChronoUnit.SECONDS));
//
//
//
//        sprintRepository.save(sprint1);
//        issue1.assignCurrentSprint(sprint1);
//
//        issue1= issueRepository.save(issue1); // CRITICAL!!!!!! reference to issue1 above is not the one that's persisted omg
//        issueRepository.save(issue2);
//        issueRepository.save(issue3);
//
//        IssueRelation issueRelation1 = new IssueRelation(null,issue1, issue2, "issue2 has to be resolved fist to complete issue1");
//        IssueRelation issueRelation2 = new IssueRelation(null,issue1, issue3, " issue3 has to be also resolved to end this project!... (issue1)");
//
//        Set<IssueRelation> issueRelationSet=issueRelationRepository.saveAll(List.of(issueRelation1,issueRelation2)).stream().collect(Collectors.toCollection(HashSet::new));
//
////        issue1.getIssueRelations().add(issueRelation1);
////        issue1.getIssueRelations().add(issueRelation2);
////        // for this to work, issue1 has to be the right reference to the persisted one!!!!!!
////        issue1.getIssueRelations().stream().forEach(issueRelation -> System.out.println(issueRelation.getRelationDescription()));
//
//
//        em.flush();
//
//        Issue issue= issueService.getReferenceById(issue1.getId());
//        return new IssueDto(issue,Collections.emptySet(),issueRelationSet);
//
//    }
//
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
//
//
//
//
//    @Autowired
//    AuditRepository auditRepository;
//
//    @PostMapping("/issue1update")
//    @PreAuthorize("hasRole('ADMIN')")
//    public IssueDto updateTest(){
//        Issue issue1 = issueService.getReferenceById(1L);
//
//        System.out.println(issue1.getId()+" -(issue id) was found");
//
//        issue1.changeStatus(IssueStatus.IN_PROGRESS);
//
//        User user = new User("kiki",passwordEncoder.encode("123"),"kiki","kim","kk@gmail.com",true);
//
//        userRepository.save(user);
//        issue1.addAssignee(user);
//
//        em.flush();
//        return new IssueDto(issue1,Collections.emptySet(),new HashSet<>(issueRelationRepository.findByAffectedIssue(issue1.getId())));
//    }
//
//
//    @GetMapping("/audit")
//    @PreAuthorize("hasRole('ADMIN')")
//    public List<String> auditTest() throws Exception {
//        Issue issue1 = issueService.getReferenceById(1L);
//
//     //   auditRepository.getUpdatedData(issue1.getId(),"status");
////        Object[] resultArray=(Object[]) auditRepository.getUpdatedData(issue1.getId(),"status").get(1);
////        Arrays.stream(resultArray).sequential().forEach(System.out::println);
//
//        return auditRepository.getHistory(issue1.getClass(),issue1.getId());
//    }
//
//
//
//    @PreAuthorize("hasPermission(#projectId,'project','ROLE_PROJECT_LEAD') or hasPermission(#projectId,'project','ROLE_PROJECT_MEMBER') or hasPermission(#projectId,'project','ROLE_ADMIN')")
//    @GetMapping("/preauth-test/{projectId}")
//    public void authorizationTest(@PathVariable Long projectId){
//
//    }
//
//    @Autowired
//    private ProjectRepository projectRepository;
//
//
//    @GetMapping("/error-test")
//    public void errorTest( @Validated @RequestBody IssueSimpleDto issueSimpleDto, BindingResult bindingResult) throws BindException {
//
//            Project project=projectRepository.getReferenceById(5L);
//        System.out.println(project.getDescription());
//            System.out.printf("kk");
//
//
////        if(issueSimpleDto.getId()==null){
////            throw new IllegalArgumentException(" id not given");
////        }
////        if(issueSimpleDto.getStatus()==null){
////            bindingResult.rejectValue("id","notFound.id","id not found!");
////            throw new BindException(bindingResult);
////        }
//    }
//
//
//
//
//
//}