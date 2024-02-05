package com.example.bugtracker.service;

import com.example.bugtracker.domain.model.issue.Comment;
import com.example.bugtracker.domain.model.issue.Issue;
import com.example.bugtracker.dto.issue.onetomany.CommentCreateDto;
import com.example.bugtracker.dto.issue.onetomany.CommentPageDto;
import com.example.bugtracker.exception.directmessageconcretes.NotExistException;
import com.example.bugtracker.repository.repository_interfaces.CommentRepository;
import com.example.bugtracker.repository.repository_interfaces.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    /*
     * 'Comment' is a child entity of 'Issue', managed through collection fields.
     * The life cycle depends entirely on 'Issue', with creation and deletion handled by adding and removing from the collection.
     * Updating 'Comment' is not supported currently.
     *
     * Avoid N+1 problems when fetching 'Comments' through 'Issue'
     * due to the many-to-one relationship with 'User' and Lazy fetch type.
     * The method for paginated comment fetching directly uses 'commentRepository' without involving the parent entity 'Issue'.
     */


    private final CommentRepository commentRepository;

    private final IssueRepository issueRepository;


    /**
     * Creates and saves 'Comment' and returns 'CommentCreateDto' with an auto-generated id from the database.
     *
     * @param commentCreateDto a DTO with an id of the 'Issue' it belongs to.
     *                         The 'issueId' is expected to be verified for existence beforehand.
     * @return a 'CommentCreateDto' with an auto-generated id from the database.
     */
    public CommentCreateDto createComment(Long issueId, CommentCreateDto commentCreateDto){

        Optional<Issue> issueOptional = Optional.empty();
        Comment parent = null;

        if(commentCreateDto.getParentId()!=null){
            issueOptional = issueRepository.findByIdWithCommentListWithParent(
                    issueId, commentCreateDto.getParentId());
            if(issueOptional.isEmpty()){
                throw new NotExistException("parent comment with id "+commentCreateDto.getParentId()+ "does not exist");
            }
            parent= issueOptional.get().getComment(commentCreateDto.getParentId()).get();

        } else {
            issueOptional = issueRepository.findByIdWithCommentList(issueId);
        }

        Issue issue = issueOptional.get();

        Comment commentCreated = new Comment(null, issue, commentCreateDto.getDescription(),parent);
        issue.addComment(commentCreated);
        issueRepository.save(issue);
        return new CommentCreateDto(commentCreated);
    }


    /**
     * Fetches paginated 'Comments' associated with the given 'Issue'.
     *
     * @param issueId The id of the 'Issue' to which the comment belongs.
     *                Expected to be verified for existence beforehand.
     * @param offset  The offset for paginated results.
     * @param limit   The maximum number of comments to retrieve per page.
     * @return A 'CommentPageDto' containing paginated comments.
     */
    public CommentPageDto findAllByIssueId(Long issueId, int offset, int limit){
        return commentRepository.findAllByIssueIdWithParent(issueId, offset, limit);
    }


    /**
     * Deletes a 'Comment' associated with the given 'Issue' and 'Comment' id.
     *
     * @param issueId    The id of the 'Issue' to which the comment belongs.
     *                   Expected to be verified for existence beforehand.
     * @param commentId  The id of the comment to be deleted.
     *                   Expected to be verified for existence beforehand.
     */
//    public void deleteComment(Long issueId, Long commentId){
//        Issue issue = issueRepository.findByIdWithCommentList(issueId).get();
//        issue.deleteComment(commentId);
//
//        //maybe above two lines are not necessary!!!! then remove the method as well??
//        commentRepository.deleteById(commentId);
//    }

    public void deleteComment(Long issueId, Long commentId){
        Issue issue = issueRepository.findByIdWithCommentList(issueId).get();
        issue.deleteComment(commentId);
        issueRepository.save(issue);
    }


}
