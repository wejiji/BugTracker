package com.example.security2pro.service;

import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.project.ProjectCreateDto;
import com.example.security2pro.dto.project.ProjectDto;
import com.example.security2pro.dto.project.ProjectSimpleUpdateDto;
import com.example.security2pro.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.example.security2pro.domain.enums.Role.ROLE_PROJECT_LEAD;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;

    private final UserRepository userRepository;


    public Project startProject(ProjectCreateDto projectCreateDto, User user){
        Project newProject= Project.createProject(projectCreateDto);
        ProjectMember projectMember = ProjectMember.createProjectMember(newProject, user, Set.of(ROLE_PROJECT_LEAD));
        projectMemberRepository.save(projectMember);
        return projectRepository.save(newProject);
        // creates new project with the project member who created it.
    }

    public ProjectDto getProjectDetails(Long projectId){
        Project project = getReferenceById(projectId);
        log.info("getting details for project: "+project.getName());

        Set<ProjectMember> projectMembers = projectMemberRepository.findAllMemberByProjectIdWithUser(projectId);
        Set<Sprint> sprints = sprintRepository.findByProjectIdAndArchivedFalse(projectId);
        Set<Issue> projectIssues = issueRepository.findByProjectIdAndArchivedFalse(projectId);

        return new ProjectDto(project, projectMembers, sprints, projectIssues);
//        Set<Issue> issuesWithSprint = findActiveProjectIssuesWithSprint(projectId);
//        Set<Issue> issuesWithoutSprint= findActiveProjectIssuesWithoutSprint(projectId);
//        return new ProjectDto(project,projectMembers,sprints, issuesWithSprint ,issuesWithoutSprint);

    }

    public ProjectSimpleUpdateDto updateProject(Long projectId, ProjectSimpleUpdateDto projectSimpleUpdateDto){
        Project project = projectRepository.getReferenceById(projectId);
        project.updateProject(projectSimpleUpdateDto.getName(), projectSimpleUpdateDto.getDescription());
        return new ProjectSimpleUpdateDto(project);
    }



    public Project getReferenceById(Long projectId){
        return projectRepository.getReferenceById(projectId);
        //return project - used when exception has to be thrown in case the id does not exist
    }


    public Set<ProjectMember> findAllMemberByProjectIdWithUser(Long projectId){
        return projectMemberRepository.findAllMemberByProjectIdWithUser(projectId);
        //return all the projectMembers within the project (join fetch) - when user info is needed
    }



}
