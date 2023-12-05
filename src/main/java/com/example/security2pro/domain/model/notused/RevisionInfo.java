//package com.example.security2pro.domain.model;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Id;
//import jakarta.persistence.OneToMany;
//
//public class RevisionInfo {
//
//    @Id
//    @Column(name = "revision_id")
//    private Long id;
//    @Column(nullable = false)
//    private Long rev_timestamp;
//
//    @OneToMany(mappedBy = "revisionInfo")
//    @Column(nullable = false)
//    private Issue issue;
//
//
//}
