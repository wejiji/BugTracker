package com.example.security2pro.repository;

import com.example.security2pro.domain.model.Attachment;
import com.example.security2pro.domain.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment,Long> {

    public List<Attachment> findByIssue(Issue issue);

}
