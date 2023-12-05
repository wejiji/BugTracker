package com.example.security2pro.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Attachment extends BaseEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name="attachment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="issue_id")
    private Issue issue;

    //베이스엔티티 확장하고 유저필드 여기 넣지 않기.(딱히 탐색이 필요하지 않으므로)
}
