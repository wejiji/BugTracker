package com.example.bugtracker.repository.repository_impls;

import com.example.bugtracker.domain.model.issue.Comment;
import com.example.bugtracker.dto.issue.onetomany.CommentPageDto;
import com.example.bugtracker.repository.jpa_repository.CommentJpaRepository;
import com.example.bugtracker.repository.repository_interfaces.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import com.example.bugtracker.dto.issue.onetomany.CommentDto;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CommentRepositoryImpl implements CommentRepository {


    private final CommentJpaRepository commentJpaRepository;

    @Override
    public CommentPageDto findAllByIssueId(Long issueId, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit);
        Page<CommentDto> commentPage
                = commentJpaRepository.findAllByIssueId(issueId, pageRequest).map(CommentDto::new);

        return new CommentPageDto(commentPage.getContent()
                , commentPage.getTotalPages()
                , commentPage.getTotalElements()
                , commentPage.getSize(), commentPage.getNumber());
    }

    @Override
    public void deleteById(Long id) {
        commentJpaRepository.deleteById(id);
    }

    @Override
    public Comment save(Comment comment) {
        return commentJpaRepository.save(comment);
    }

    @Override
    public Optional<Comment> findById(Long targetId) {
        return commentJpaRepository.findById(targetId);
    }


}
