package com.example.security2pro.service;

import com.example.security2pro.databuilders.UserTestDataBuilder;
import com.example.security2pro.domain.model.User;
import com.example.security2pro.repository.jpa_repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IssueServiceTests {

    @Mock
    private IssueJpaRepository issueRepository;
    @Mock
    private ActivityJpaRepository activityRepository;
    @Mock
    private IssueRelationJpaRepository issueRelationRepository;
    @Mock
    private ProjectJpaRepository projectRepository;
    @Mock
    private SprintJpaRepository sprintRepository;
    @Mock
    private UserJpaRepository userRepository;
    @Mock
    private SprintIssueHistoryJpaRepository sprintIssueHistoryRepository;
    @Mock
    private SimpleIssueConverter simpleIssueConverter;

    @InjectMocks
    IssueService issueService;


    @Test
    public void getUserIssues(){

        User user = new UserTestDataBuilder().build();

        when(userRepository.findUserByUsername(Mockito.any(String.class))).thenReturn(Optional.of(user));

        //when(issueRepository.findActiveIssueByUsername(user.getUsername())).thenReturn();


    }






}
