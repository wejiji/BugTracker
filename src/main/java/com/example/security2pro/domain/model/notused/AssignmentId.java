//package com.example.security2pro.domain.model;
//
//import java.io.Serializable;
//import java.util.Objects;
//
//public class AssignmentId implements Serializable {
//
//    private Long issue;
//    private Long user;
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        AssignmentId that = (AssignmentId) o;
//        return Objects.equals(issue, that.issue) && Objects.equals(user, that.user);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(issue, user);
//    }
//
//
//    public AssignmentId(){}
//}
