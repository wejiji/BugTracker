package com.example.security2pro.domain.model.issue;


import com.example.security2pro.domain.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="activity_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="issue_id")//하나의 이슈당 여러 액티비티 존재. 하나의 액티비티당 하나의 이슈만 존재.null가능 이슈코멘트/이슈히스토리/ 프로젝트 액티비티..- 프로젝트 생성, 스프린트시작 종료등
    private Issue issue;

    @Lob
    private String description;



    public Comment(Long id, Issue issue, String description) {
        this.id = id;
        this.issue = issue;
        this.description = description;
    }
    protected Comment(Long id, Issue issue, String description, String creatorUsername) {
        this.id = id;
        this.issue = issue;
        this.description = description;
        createdBy = creatorUsername;
    }

    public void assignIssue(Issue issue){
        this.issue = issue;
    }

    public static Comment createCommentWithCreatorSet(Long id, Issue issue, String description, String creatorUsername){
        return new Comment(id,issue,description,creatorUsername);
    }



}
