package com.example.bugtracker.domain.model.issue;


import com.example.bugtracker.domain.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Comment extends BaseEntity {


    // As a child entity of 'Issue', its lifecycle depends entirely on its parent 'Issue'.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @Lob
    private final String description;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(referencedColumnName = "comment_id", name="parent_id")
    private final Comment parent;



    public Comment(Long id, Issue issue, String description, Comment parent) {
        this.id = id;
        this.issue = issue;
        this.description = description;
        this.parent = parent;
    }

    private Comment(Long id
            , Issue issue
            , String description
            , Comment parent
            , String creatorUsername
    ) {

        // 'createdBy' can be set through this constructor
        this.id = id;
        this.issue = issue;
        this.description = description;
        this.parent = parent;
        createdBy = creatorUsername;
    }

    public void assignIssue(Issue issue) {
        this.issue = issue;
    }


    public static Comment createCommentWithCreatorSet(
            Long id
            , Issue issue
            , String description
            , Comment parent
            , String creatorUsername) {

        return new Comment(id, issue, description, parent, creatorUsername);
    }


}
