package com.example.bugtracker.dto.issue.onetomany;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class IssueHistoryDto {

    private final int revisionId;

    private final String modifiedField;

    private final String description;

    private final String modificationResult;

    private final String lastModifiedBy;

    private final LocalDateTime lastModifiedAt;

    public IssueHistoryDto(
            int revisionId
            , String modifiedField
            , String description
            , String modificationResult
            , String lastModifiedBy
            , LocalDateTime lastModifiedAt) {

        this.revisionId = revisionId;
        this.modifiedField = modifiedField;
        this.description = description;
        this.modificationResult = modificationResult;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedAt = lastModifiedAt;
    }


}
