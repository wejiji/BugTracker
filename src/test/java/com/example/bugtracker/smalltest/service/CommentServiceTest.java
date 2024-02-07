package com.example.bugtracker.smalltest.service;

import com.example.bugtracker.databuilders.IssueTestDataBuilder;
import com.example.bugtracker.domain.model.issue.Comment;
import com.example.bugtracker.domain.model.issue.Issue;
import com.example.bugtracker.dto.issue.onetomany.CommentCreateDto;
import com.example.bugtracker.dto.issue.onetomany.CommentDto;
import com.example.bugtracker.dto.issue.onetomany.CommentPageDto;
import com.example.bugtracker.exception.directmessageconcretes.NotExistException;
import com.example.bugtracker.fake.repository.CommentRepositoryFake;
import com.example.bugtracker.fake.repository.IssueRepositoryFake;
import com.example.bugtracker.repository.repository_interfaces.CommentRepository;
import com.example.bugtracker.repository.repository_interfaces.IssueRepository;
import com.example.bugtracker.service.CommentService;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentServiceTest {

    private final IssueRepository issueRepository = new IssueRepositoryFake();

    private final CommentRepository commentRepository = new CommentRepositoryFake();

    private final CommentService commentService = new CommentService(commentRepository,issueRepository);
    @Test
    void commentCreateDto_createsAndReturnsCommentCreateDto_givenFieldValues(){
        // Execution
        CommentCreateDto commentCreateDto
                = new CommentCreateDto("comment description", 3L);
        // Assertions
        assertEquals("comment description", commentCreateDto.getDescription());
        assertEquals(3L, commentCreateDto.getParentId());
    }

    @Test
    void commentDto_createsAndReturnsComment_givenFieldValues(){
        //Execution
        CommentDto commentDto = new CommentDto(1L,"comment description",3L);

        assertEquals(1L,commentDto.getId());
        assertEquals("comment description",commentDto.getDescription());
        assertEquals(3L, commentDto.getParentId());
    }

    @Test
    void commentDto_createsAndReturnsComment_givenCommentObject(){
        //Setup
        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        issue =issueRepository.save(issue);

        Comment parent = new Comment(3L, issue, "parent comment description", null);
        Comment comment = new Comment(1L,issue,"comment description",parent);
        CommentDto commentDto = new CommentDto(comment);

        assertEquals(1L,commentDto.getId());
        assertEquals("comment description",commentDto.getDescription());
        assertEquals(3L,commentDto.getParentId());
    }

    @Test
    void createComment_createsCommentAndReturnsCommentCreateDto_givenNullParent(){
        //Setup
        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        issue =issueRepository.save(issue);
        assertEquals(0,issue.getCommentList().size());

        CommentCreateDto commentCreateDto
                = new CommentCreateDto("comment on the issue",null);

        //Execution
        CommentCreateDto commentCreateDtoReturned =
                commentService.createComment(issue.getId(),commentCreateDto);

        //Assertions
        assertThat(commentCreateDtoReturned)
                .usingRecursiveComparison()
                .isEqualTo(commentCreateDto);

        Issue issueFound = issueRepository.findById(issue.getId()).get();
        assertEquals(1,issueFound.getCommentList().size());
        Comment savedComment = issueFound.getCommentList().get(0);
        assertEquals("comment on the issue",savedComment.getDescription());
    }

    @Test
    void createComment_createsCommentAndReturnsCommentCreateDto_givenValidNonNullParent(){
        //Setup

        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        Comment parent = new Comment(3L,issue,"parent comment descriptino", null);
        issue.addComment(parent);

        issue =issueRepository.save(issue);
        assertEquals(1,issue.getCommentList().size());

        CommentCreateDto commentCreateDto
                = new CommentCreateDto("comment on the issue",parent.getId());

        //Execution
        CommentCreateDto commentCreateDtoReturned =
                commentService.createComment(issue.getId(),commentCreateDto);

        //Assertions
        assertThat(commentCreateDtoReturned)
                .usingRecursiveComparison()
                .isEqualTo(commentCreateDto);

        Issue issueFound = issueRepository.findById(issue.getId()).get();
        assertEquals(2,issueFound.getCommentList().size());
        Comment savedComment = issueFound.getCommentList().get(1);
        assertEquals("comment on the issue",savedComment.getDescription());
        assertEquals(3L,savedComment.getParent().getId());
    }

    @Test
    void createComment_throwsException_givenInvalidNonNullParent(){
        //Setup

        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        Comment parent = new Comment(3L,issue,"parent comment descriptino", null);
        issue.addComment(parent);
        Long nonExistentParentId = 2L;

        issue =issueRepository.save(issue);
        assertEquals(1,issue.getCommentList().size());

        CommentCreateDto commentCreateDto
                = new CommentCreateDto("comment on the issue",nonExistentParentId);

        //Execution
        Issue finalIssue = issue;
        assertThrows(NotExistException.class ,()->commentService.createComment(finalIssue.getId(),commentCreateDto));

    }





    @Test
    void findAllByIssueId_findsAndReturnsPaginatedComments_givenIssueId(){
        //Setup
        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        Comment comment1 = new Comment(1L,issue,"comment 1",null);
        Comment comment2 = new Comment(2L,issue,"comment 2",null);
        Comment comment3 = new Comment(3L,issue,"comment 3",null);
        Comment comment4 = new Comment(4L,issue,"comment 4",null);
        Comment comment5 = new Comment(5L,issue,"comment 5",null);
        Comment comment6 = new Comment(6L,issue,"comment 6",null);
        Comment comment7 = new Comment(7L,issue,"comment 7",null);

        issue.addComment(comment1);
        issue.addComment(comment2);
        issue.addComment(comment3);
        issue.addComment(comment4);
        issue.addComment(comment5);
        issue.addComment(comment6);
        issue.addComment(comment7);

        issue = issueRepository.save(issue);
        comment1 = commentRepository.save(comment1);
        comment2 = commentRepository.save(comment2);
        comment3 = commentRepository.save(comment3);
        comment4 = commentRepository.save(comment4);
        comment5 = commentRepository.save(comment5);
        comment6 = commentRepository.save(comment6);
        comment7 = commentRepository.save(comment7);

        List<CommentDto> expectedDtoList1 = Stream.of(comment1,comment2).map(CommentDto::new).toList();
        List<CommentDto> expectedDtoList2 = Stream.of(comment3,comment4).map(CommentDto::new).toList();
        List<CommentDto> expectedDtoList3 = Stream.of(comment5,comment6).map(CommentDto::new).toList();
        List<CommentDto> expectedDtoList4 = Stream.of(comment7).map(CommentDto::new).toList();


        //Execution &Assertions
        CommentPageDto commentPageDto1 = commentService.findAllByIssueId(issue.getId(), 0,2);
        assertThat(commentPageDto1.getCommentDtos()).usingRecursiveComparison().isEqualTo(expectedDtoList1);
        assertEquals(2,commentPageDto1.getPageSize());
        assertEquals(4,commentPageDto1.getTotalPages());
        assertEquals(1,commentPageDto1.getCurrentPageNumber());
        assertEquals(7,commentPageDto1.getTotalElements());

        CommentPageDto commentPageDto2 = commentService.findAllByIssueId(issue.getId(), 1,2);
        assertThat(commentPageDto2.getCommentDtos()).usingRecursiveComparison().isEqualTo(expectedDtoList2);
        assertEquals(2,commentPageDto1.getPageSize());
        assertEquals(4,commentPageDto2.getTotalPages());
        assertEquals(2,commentPageDto2.getCurrentPageNumber());
        assertEquals(7,commentPageDto2.getTotalElements());

        CommentPageDto commentPageDto3 = commentService.findAllByIssueId(issue.getId(), 2,2);
        assertThat(commentPageDto3.getCommentDtos()).usingRecursiveComparison().isEqualTo(expectedDtoList3);
        assertEquals(2,commentPageDto3.getPageSize());
        assertEquals(4,commentPageDto3.getTotalPages());
        assertEquals(3,commentPageDto3.getCurrentPageNumber());
        assertEquals(7,commentPageDto3.getTotalElements());

        CommentPageDto commentPageDto4 = commentService.findAllByIssueId(issue.getId(), 3,2);
        assertThat(commentPageDto4.getCommentDtos()).usingRecursiveComparison().isEqualTo(expectedDtoList4);
        assertEquals(2,commentPageDto4.getPageSize());
        assertEquals(4,commentPageDto4.getTotalPages() );
        assertEquals(4,commentPageDto4.getCurrentPageNumber());
        assertEquals(7,commentPageDto4.getTotalElements());
    }


    @Test
    void deleteComment_DeletesComments_givenCommentIdWithChildComment(){
        // 'Comment' is a child entity of 'Issue', managed through collection fields.

        //Setup
        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        Comment parent = new Comment(9L, issue, "parent", null);
        Comment comment1 = new Comment(1L,issue,"comment 1",parent);
        Comment comment2 = new Comment(2L,issue,"comment 2",null);
        Comment comment3 = new Comment(3L,issue,"comment 3",parent);

        issue.addComment(parent);
        issue.addComment(comment1);
        issue.addComment(comment2);
        issue.addComment(comment3);

        issue = issueRepository.save(issue);

        //Execution
        commentService.deleteComment(issue.getId(), parent.getId());
        issue = issueRepository.getReferenceById(issue.getId());

        //Assertions
        assertEquals(1,issue.getCommentList().size());
        Comment commentLeft = issue.getCommentList().get(0);
        assertEquals(comment2.getId(),commentLeft.getId());
        assertEquals(comment2.getDescription(),commentLeft.getDescription());
    }




}
