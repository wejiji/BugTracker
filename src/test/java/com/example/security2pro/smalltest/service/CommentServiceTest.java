package com.example.security2pro.smalltest.service;

import com.example.security2pro.databuilders.IssueTestDataBuilder;
import com.example.security2pro.domain.model.issue.Comment;
import com.example.security2pro.domain.model.issue.Issue;
import com.example.security2pro.dto.issue.onetomany.CommentCreateDto;
import com.example.security2pro.dto.issue.onetomany.CommentDto;
import com.example.security2pro.dto.issue.onetomany.CommentPageDto;
import com.example.security2pro.fake.repository.CommentRepositoryFake;
import com.example.security2pro.fake.repository.IssueRepositoryFake;
import com.example.security2pro.repository.repository_interfaces.CommentRepository;
import com.example.security2pro.repository.repository_interfaces.IssueRepository;
import com.example.security2pro.service.CommentService;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentServiceTest {

    private final IssueRepository issueRepository = new IssueRepositoryFake();

    private final CommentRepository commentRepository = new CommentRepositoryFake();

    private final CommentService commentService = new CommentService(commentRepository,issueRepository);
    @Test
    void commentCreateDto_createsAndReturnsCommentCreateDto_givenFieldValues(){
        // Execution
        CommentCreateDto commentCreateDto
                = new CommentCreateDto(1L,"comment description");
        // Assertions
        assertEquals(1L, commentCreateDto.getIssueId());
        assertEquals("comment description", commentCreateDto.getDescription());

    }

    @Test
    void commentDto_createsAndReturnsComment_givenFieldValues(){
        //Execution
        CommentDto commentDto = new CommentDto(1L,"comment description");

        assertEquals(1L,commentDto.getId());
        assertEquals("comment description",commentDto.getDescription());
    }

    @Test
    void commentDto_createsAndReturnsComment_givenCommentObject(){
        //Setup
        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        issue =issueRepository.save(issue);

        Comment comment = new Comment(1L,issue,"comment description");
        CommentDto commentDto = new CommentDto(comment);

        assertEquals(1L,commentDto.getId());
        assertEquals("comment description",commentDto.getDescription());
    }

    @Test
    void createComment_createsCommentAndReturnsCommentCreateDto(){
        //Setup
        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        issue =issueRepository.save(issue);
        assertEquals(0,issue.getCommentList().size());

        CommentCreateDto commentCreateDto
                = new CommentCreateDto(issue.getId(),"comment on the issue");

        //Execution
        CommentCreateDto commentCreateDtoReturned =
                commentService.createComment(commentCreateDto);

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
    void findAllByIssueId_findsAndReturnsPaginatedComments_givenIssueId(){
        //Setup
        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        Comment comment1 = new Comment(1L,issue,"comment 1");
        Comment comment2 = new Comment(2L,issue,"comment 2");
        Comment comment3 = new Comment(3L,issue,"comment 3");
        Comment comment4 = new Comment(4L,issue,"comment 4");
        Comment comment5 = new Comment(5L,issue,"comment 5");
        Comment comment6 = new Comment(6L,issue,"comment 6");
        Comment comment7 = new Comment(7L,issue,"comment 7");

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


        //Execution
        CommentPageDto commentPageDto1 = commentService.findAllByIssueId(issue.getId(), 0,2);

        //Assertions
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
    void deleteComment_DeletesComment_givenCommentId(){
        //Setup
        Issue issue = new IssueTestDataBuilder().withId(10L).build();
        Comment comment1 = new Comment(1L,issue,"comment 1");
        Comment comment2 = new Comment(2L,issue,"comment 2");

        issue.addComment(comment1);
        issue.addComment(comment2);

        issue = issueRepository.save(issue);
        comment1 = commentRepository.save(comment1);
        comment2 = commentRepository.save(comment2);

        //Execution
        issue.deleteComment(comment1.getId());
        commentRepository.deleteById(comment1.getId());
        Issue issueSaved = issueRepository.save(issue);

        //Assertions
        assertEquals(1,issue.getCommentList().size());
        Comment commentLeft = issue.getCommentList().get(0);
        assertEquals(comment2.getId(),commentLeft.getId());
        assertEquals(comment2.getDescription(),commentLeft.getDescription());

        assertThat(commentRepository.findById(comment1.getId())).isEmpty();
        assertEquals(1,issueSaved.getCommentList().size());
        commentLeft = issueSaved.getCommentList().get(0);
        assertEquals(comment2.getId(),commentLeft.getId());
        assertEquals(comment2.getDescription(),commentLeft.getDescription());
    }




}
