package com.example.security2pro.dto.issue;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class IssueHistoryDto {

    private int revisionId;

    private String modifiedField;

    private String description;

    private String modificationResult;

    private String lastModifiedBy;

    private LocalDateTime lastModifiedAt;

    public IssueHistoryDto(int revisionId, String modifiedField, String description, String modificationResult, String lastModifiedBy, LocalDateTime lastModifiedAt) {
        this.revisionId = revisionId;
        this.modifiedField = modifiedField;
        this.description = description;
        this.modificationResult = modificationResult;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedAt = lastModifiedAt;
    }


}
