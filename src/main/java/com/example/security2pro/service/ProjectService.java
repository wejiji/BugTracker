package com.example.security2pro.service;

import com.example.security2pro.domain.model.*;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.dto.project.ProjectCreateDto;
import com.example.security2pro.dto.project.ProjectDto;
import com.example.security2pro.dto.project.ProjectSimpleUpdateDto;
import com.example.security2pro.repository.repository_interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Set;
import java.util.stream.Collectors;

import static com.example.security2pro.domain.enums.ProjectMemberRole.ROLE_PROJECT_LEAD;


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

        Project newProject= projectRepository.save(Project.createProject(null, projectCreateDto.getName(), projectCreateDto.getDescription()));
        ProjectMember projectMember = projectMemberRepository.save(ProjectMember.createProjectMember(null,newProject, user, Set.of(ROLE_PROJECT_LEAD)));
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
        issueRepository.deleteAllByIdInBatch(issueRepository.findAllByProjectId(projectId).stream().map(Issue::getId).collect(Collectors.toSet()));
        sprintRepository.deleteAllByIdInBatch(sprintRepository.findAllByProjectId(projectId).stream().map(Sprint::getId).collect(Collectors.toSet()));
        projectMemberRepository.deleteAllByIdInBatch(projectMemberRepository.findAllByProjectId(projectId).stream().map(ProjectMember::getId).collect(Collectors.toSet()));
        projectRepository.deleteById(projectId);
    }





}
