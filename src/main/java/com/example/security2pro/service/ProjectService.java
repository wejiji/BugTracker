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
    /*
     * Because there is no JPA bidirectional relationship between the 'Project' entity and other entities,
     * separate queries will be executed for retrieving and deleting
     * any entity with a many-to-one relationship with 'Project' from their respective repositories.
     *
     * While calling the 'save' method is unnecessary when the entity is already in the cache,
     * as dirty checking can automatically update modified fields, it is called nevertheless for explicitness.
     */
    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;

    private final SprintRepository sprintRepository;

    private final IssueRepository issueRepository;

    private final UserRepository userRepository;

    /**
     * Creates a new 'Project' with the 'ProjectMember' who initiated it and then saves both.
     * The first 'ProjectMember' of the created 'Project' is assigned the 'ROLE_PROJECT_LEAD' role.
     *
     * Note that only an authenticated 'User'
     * with either 'ROLE_ADMIN' or 'ROLE_TEAM_LEAD' is allowed to create a 'Project'.
     *
     * @param projectCreateDto Data required to create a new 'Project'.
     * @param username         The username of the currently authenticated 'User'.
     *                         Expected to be verified for existence beforehand.
     * @return A 'ProjectSimpleUpdateDto' with an auto-generated id assigned by the repository.
     */
    public ProjectSimpleUpdateDto startProject(ProjectCreateDto projectCreateDto, String username) {

        Project newProject
                = projectRepository.save(
                Project.createProject(
                        null, projectCreateDto.getName(), projectCreateDto.getDescription()));

        User user = userRepository.findUserByUsername(username).get();

        projectMemberRepository.save(
                ProjectMember.createProjectMember(
                        null, newProject, user, Set.of(ROLE_PROJECT_LEAD)));

        return new ProjectSimpleUpdateDto(
                projectRepository.save(newProject));
    }

    public ProjectDto getProjectDetails(Long projectId) {
        /* Fetches and returns details for the 'Project' with the provided id,
         * including non-archived 'Sprints' and non-archived 'Issues' that belong to the project.
         */

        Project project = projectRepository.getReferenceById(projectId);

        Set<ProjectMember> projectMembers
                = projectMemberRepository.findAllMemberByProjectIdWithAuthorities(projectId);

        Set<Sprint> sprints
                = sprintRepository.findByProjectIdAndArchivedFalse(projectId);

        Set<Issue> projectIssues
                = issueRepository.findByProjectIdAndArchivedFalse(projectId);

        return new ProjectDto(project, projectMembers, sprints, projectIssues);
    }

    public Project getReferenceById(Long projectId) {
        return projectRepository.getReferenceById(projectId);
    }

    public Set<ProjectMember> findAllMemberByProjectIdWithAuthorities(Long projectId) {
        return projectMemberRepository.findAllMemberByProjectIdWithAuthorities(projectId);
    }

    public ProjectSimpleUpdateDto updateProject(Long projectId, ProjectSimpleUpdateDto projectSimpleUpdateDto) {
        Project project = projectRepository.getReferenceById(projectId);
        project.updateProject(projectSimpleUpdateDto.getName(), projectSimpleUpdateDto.getDescription());
        return new ProjectSimpleUpdateDto(project);
    }

    public void deleteProject(Long projectId) {
        // Deletes a 'Project' along with 'Sprints' and 'Issues' that belong to the 'Project'.
        issueRepository.deleteAllByIdInBatch(
                issueRepository.findAllByProjectId(projectId).
                        stream()
                        .map(Issue::getId)
                        .collect(Collectors.toSet()));

        sprintRepository.deleteAllByIdInBatch(
                sprintRepository.findAllByProjectId(projectId)
                        .stream()
                        .map(Sprint::getId)
                        .collect(Collectors.toSet()));

        projectMemberRepository.deleteAllByIdInBatch(
                projectMemberRepository.findAllByProjectId(projectId)
                        .stream()
                        .map(ProjectMember::getId)
                        .collect(Collectors.toSet()));

        projectRepository.deleteById(projectId);
    }


}
