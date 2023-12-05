package com.example.security2pro.domain.model;

import com.example.security2pro.domain.model.Issue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueRelation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="issue_relation_id")
    private Long id;

    //아래의 관계에 대해서 잘 모르겠다. 일단 중간테이블 + 추가컬럼 필요함
    //(어떤식의 관계가 있는지 적어야 하기 때문에 필드 필요.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="affected_issue_id", referencedColumnName = "issue_id", updatable = false )
    private Issue affectedIssue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cause_issue_id",  referencedColumnName = "issue_id", updatable = false)//이부분 잘 모르겠다.
    private Issue causeIssue;

    private String relationDescription;


    public IssueRelation(Long id,Issue affectedIssue, Issue causeIssue, String relationDescription) {
        this.id = id;
        this.affectedIssue = affectedIssue;
        this.causeIssue = causeIssue;
        this.relationDescription = relationDescription;
    }

    public void createIssueRelation(Long id,Issue affectedIssue, Issue causeIssue, String relationDescription){
        // make sure affectedIssue ! = causeIssue

    }
    public void updateIssueRelation(){
        //

    }

}
