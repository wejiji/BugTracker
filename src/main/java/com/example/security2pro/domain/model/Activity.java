package com.example.security2pro.domain.model;


import com.example.security2pro.domain.enums.ActivityType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Activity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="activity_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="issue_id")//하나의 이슈당 여러 액티비티 존재. 하나의 액티비티당 하나의 이슈만 존재.null가능 이슈코멘트/이슈히스토리/ 프로젝트 액티비티..- 프로젝트 생성, 스프린트시작 종료등
    private Issue issue;

    @Enumerated(EnumType.STRING)
    private ActivityType type;

    @Lob
    private String description;



    public Activity(Long id, Issue issue, ActivityType type, String description) {
        this.id = id;
        this.issue = issue;
        this.type = type;
        this.description = description;
    }


}
