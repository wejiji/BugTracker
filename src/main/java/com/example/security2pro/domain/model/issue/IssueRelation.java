package com.example.security2pro.domain.model.issue;

import com.example.security2pro.domain.enums.IssueStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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


    protected IssueRelation(Issue affectedIssue, Issue causeIssue, String relationDescription) {
        this.affectedIssue = affectedIssue;
        this.causeIssue = causeIssue;
        this.relationDescription = relationDescription;
    }

    public static IssueRelation createIssueRelation(Issue affectedIssue, Issue causeIssue, String relationDescription){
        if(affectedIssue.getId().equals(causeIssue.getId())
                || causeIssue.getStatus().equals(IssueStatus.DONE)){
            throw new IllegalArgumentException( "invalid issue relation. " +
                    "cause issue cannot be the same as the affected issue. " +
                    "cause Issue with 'DONE' state cannot be newly added as a cause issue. ");
        }
        return new IssueRelation(affectedIssue, causeIssue, relationDescription);
    }

    public IssueRelation update(String relationDescription){
        this.relationDescription = relationDescription;
        return this;
    }

    public void assignAffectedIssue(Issue affectedIssue){
        this.affectedIssue = affectedIssue;
    }



}
