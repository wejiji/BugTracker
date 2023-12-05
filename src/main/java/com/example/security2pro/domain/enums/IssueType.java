package com.example.security2pro.domain.enums;

public enum IssueType {
    BUG, NEW_FEATURE, IMPROVEMENT, EPIC
    //EPIC은 다른 IssueType들을 가질수 있음. 다른것들은 EPIC을 가질수 없음.
}
