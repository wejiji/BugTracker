package com.example.bugtracker.fake.repository;

import com.example.bugtracker.domain.model.issue.Comment;
import com.example.bugtracker.dto.issue.onetomany.CommentDto;
import com.example.bugtracker.dto.issue.onetomany.CommentPageDto;
import com.example.bugtracker.repository.repository_interfaces.CommentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommentRepositoryFake implements CommentRepository {

    private List<Comment> commentList = new ArrayList<>();
    private Long generatedId = 0L;
    @Override
    public CommentPageDto findAllByIssueIdWithParent(Long issueId, int offset, int limit) {

        List<Comment> foundComments = commentList.stream()
                .filter(comment -> comment.getIssue().getId().equals(issueId))
                .collect(Collectors.toCollection(ArrayList::new));

        return intoPageDto(foundComments, offset, limit);
    }

    private CommentPageDto intoPageDto(List<Comment> foundComments, int offset, int limit){

        foundComments.forEach(comment -> System.out.println(comment.getId()+" - comment, "));

        int totalElements = foundComments.size();
        int pageSize = limit;

        int endIndex;
        if(offset*(limit+1)>=totalElements){
            endIndex = totalElements;
        } else {
            endIndex =(offset*limit) + limit;
        }
        List<CommentDto> commentDtoListToReturn
                =foundComments
                .subList((offset*limit),endIndex)
                .stream()
                .map(CommentDto::new)
                .collect(Collectors.toCollection(ArrayList::new));

        commentDtoListToReturn.forEach(comment-> System.out.println("comment "+comment.getId()+","));

        int totalPages =0;
        if(foundComments.size()%limit==0){
            totalPages =foundComments.size()/limit;
        } else {
            totalPages = (foundComments.size()/limit)+1;
        }

        int currentPageNumber;
        if(endIndex%limit==0){
            currentPageNumber = endIndex/limit;
        } else {
            currentPageNumber = (endIndex / limit)+1;
        }

        return new CommentPageDto(commentDtoListToReturn,totalPages,totalElements,pageSize,currentPageNumber);
    }



    @Override
    public Optional<Comment> findById(Long commentId) {
        return commentList.stream().filter(comment -> comment.getId().equals(commentId)).findAny();
    }



    @Override
    public void deleteById(Long commentId) {
        OptionalInt foundCommentIndex = IntStream.range(0, commentList.size())
                .filter(i->commentId.equals(commentList.get(i).getId()))
                .findFirst();
        commentList.remove(foundCommentIndex.getAsInt());
    }

    @Override
    public Comment save(Comment newComment) {
        if(newComment.getId()==null){
            generatedId++;

            Comment comment = new Comment(
                    generatedId
                    ,newComment.getIssue()
                    ,newComment.getDescription()
                    ,newComment.getParent());

            commentList.add(comment);
            return comment;
        }
        OptionalInt foundCommentIndex = IntStream.range(0, commentList.size())
                .filter(i->newComment.getId().equals(commentList.get(i).getId()))
                .findFirst();

        if(foundCommentIndex.isPresent()){
            commentList.remove(foundCommentIndex.getAsInt());
        }
        commentList.add(newComment);
        return newComment;

    }



}
