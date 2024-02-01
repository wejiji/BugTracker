package com.example.bugtracker.fake.repository;

import com.example.bugtracker.databuilders.ProjectMemberTestDataBuilder;
import com.example.bugtracker.domain.model.ProjectMember;
import com.example.bugtracker.repository.repository_interfaces.ProjectMemberRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProjectMemberRepositoryFake implements ProjectMemberRepository {

    private List<ProjectMember> projectMemberList = new ArrayList<>();

    private Long generatedId = 0L;

    @Override
    public Optional<ProjectMember> findByUsernameAndProjectIdWithAuthorities(String username, Long projectId) {
        return projectMemberList.stream()
                .filter(projectMember -> projectMember.getUser().getUsername().equals(username)
                        && projectMember.getProject().getId().equals(projectId))
                .findAny();
    }

    @Override
    public Optional<ProjectMember> findById(Long projectMemberId) {
        return projectMemberList.stream().filter(projectMember-> projectMember.getId().equals(projectMemberId)).findAny();
    }

    @Override
    public Optional<ProjectMember> findByUsernameAndProjectId(String username, Long projectId) {
        return projectMemberList.stream()
                .filter(projectMember -> projectMember.getUser().getUsername().equals(username)
                        && projectMember.getProject().getId().equals(projectId))
                .findAny();
    }

    @Override
    public ProjectMember save(ProjectMember newProjectMember) {

        if(newProjectMember.getId()==null){
            generatedId++;
            ProjectMember projectMember =
                    new ProjectMemberTestDataBuilder()
                            .withId(generatedId)
                            .withProject(newProjectMember.getProject())
                            .withUser(newProjectMember.getUser())
                            .withAuthorities(newProjectMember.getAuthorities())
                            .build();

            projectMemberList.add(projectMember);
            return projectMember;
        }

        OptionalInt foundProjectMemberIndex = IntStream.range(0,projectMemberList.size())
                .filter(i->newProjectMember.getId().equals(projectMemberList.get(i).getId()))
                .findFirst();
        if(foundProjectMemberIndex.isPresent()){
            projectMemberList.remove(foundProjectMemberIndex.getAsInt());
        }
        projectMemberList.add(newProjectMember);
        return newProjectMember;
    }

    @Override
    public ProjectMember getReferenceById(Long projectMemberId) {
        return projectMemberList.stream()
                .filter(projectMember -> projectMember.getId().equals(projectMemberId))
                .findAny()
                .orElseThrow(()->new EntityNotFoundException("project member with id"+ projectMemberId +" not found"));
    }

    @Override
    public void deleteById(Long projectMemberId) {
        int foundProjectMemberIndex = IntStream.range(0,projectMemberList.size())
                .filter(i->projectMemberId.equals(projectMemberList.get(i).getId()))
                .findFirst().getAsInt();
        projectMemberList.remove(foundProjectMemberIndex);
    }

    @Override
    public Optional<ProjectMember> findByIdWithAuthorities(Long projectMemberId) {
        return projectMemberList.stream().filter(projectMember -> projectMember.getId().equals(projectMemberId)).findAny();
    }

    @Override
    public Set<ProjectMember> findAllMemberByProjectIdWithAuthorities(Long projectId) {
        return projectMemberList.stream().filter(projectMember->projectMember.getProject().getId().equals(projectId))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<ProjectMember> findAllByUsernameAndProjectIdWithUser(Set<String> passedAssigneesUsernames, Long projectId) {
        if(passedAssigneesUsernames==null || passedAssigneesUsernames.isEmpty()){
            return Collections.emptySet();
        }
        return projectMemberList.stream().filter(projectMember -> passedAssigneesUsernames.contains(projectMember.getUser().getUsername())
                && projectMember.getProject().getId().equals(projectId)).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<ProjectMember> findAllByProjectId(Long projectId) {
        return projectMemberList.stream().filter(projectMember -> projectMember.getProject().getId().equals(projectId)).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public void deleteAllByIdInBatch(Set<Long> projectMemberIds) {
        projectMemberList = projectMemberList.stream().filter(projectMember -> !projectMemberIds.contains(projectMember.getId())).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Set<ProjectMember> findAllByUsernameWithProjectMemberAuthorities(String username) {
        return projectMemberList.stream()
                .filter(projectMember -> projectMember.getUser().getUsername().equals(username))
                .collect(Collectors.toCollection(HashSet::new));
    }


}
