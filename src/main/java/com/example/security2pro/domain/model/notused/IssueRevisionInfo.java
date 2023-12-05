//package com.example.security2pro.domain.model;
//
//
//import jakarta.persistence.*;
//
//@IdClass(IssueRevisionInfoId.class)
//public class IssueRevisionInfo {
//
//    @Id
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "issue_id")
//    private Issue issue;
//
//
//    @Id
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "revision_id")
//    private RevisionInfo revisionInfo;
//
//    private int revisionType;
//
//
//}
