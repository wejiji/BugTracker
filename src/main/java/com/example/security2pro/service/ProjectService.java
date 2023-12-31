package com.example.security2pro.service;

import com.example.security2pro.domain.model.*;
import com.example.security2pro.dto.project.ProjectCreateDto;
import com.example.security2pro.dto.project.ProjectDto;
import com.example.security2pro.dto.project.ProjectSimpleUpdateDto;
import com.example.security2pro.repository.jpa_repository.IssueJpaRepository;
import com.example.security2pro.repository.jpa_repository.ProjectMemberJpaRepository;
import com.example.security2pro.repository.jpa_repository.ProjectJpaRepository;
import com.example.security2pro.repository.jpa_repository.SprintJpaRepository;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectMemberRepository;
import com.example.security2pro.repository.repository_interfaces.ProjectRepository;
import com.example.security2pro.repository.repository_interfaces.SprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.example.security2pro.domain.enums.Role.ROLE_PROJECT_LEAD;
import static java.util.stream.Collectors.groupingBy;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;

    public ProjectSimpleUpdateDto startProject(ProjectCreateDto projectCreateDto, User user){
        Project newProject= Project.createProject(projectCreateDto);
        ProjectMember projectMember = ProjectMember.createProjectMember(newProject, user, Set.of(ROLE_PROJECT_LEAD));
        projectMemberRepository.save(projectMember);
        return new ProjectSimpleUpdateDto(projectRepository.save(newProject));
        // creates new project with the project member who created it.
    }

    public ProjectDto getProjectDetails(Long projectId){
        Project project = projectRepository.getReferenceById(projectId);

        Set<ProjectMember> projectMembers = projectMemberRepository.findAllMemberByProjectIdWithUser(projectId);
        Set<Sprint> sprints = sprintRepository.findByProjectIdAndArchivedFalse(projectId);
        Set<Issue> projectIssues = issueRepository.findByProjectIdAndArchivedFalse(projectId);

        return new ProjectDto(project, projectMembers, sprints, projectIssues);
    }

    public Project getReferenceById(Long projectId){
        return projectRepository.getReferenceById(projectId);
    }


    public Set<ProjectMember> findAllMemberByProjectIdWithUser(Long projectId){
        return projectMemberRepository.findAllMemberByProjectIdWithUser(projectId);
        //return all the projectMembers within the project (join fetch) - when user info is needed
    }

    public ProjectSimpleUpdateDto updateProject(Long projectId, ProjectSimpleUpdateDto projectSimpleUpdateDto){
        Project project = projectRepository.getReferenceById(projectId);
        project.updateProject(projectSimpleUpdateDto.getName(), projectSimpleUpdateDto.getDescription());
        return new ProjectSimpleUpdateDto(project);
    }

    public void deleteProject(Long projectId){


        projectRepository.deleteById(projectId);
    }



}
