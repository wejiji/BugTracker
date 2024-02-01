package com.example.bugtracker.dto.issue.onetomany;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentPageDto {

    private final List<CommentDto> commentDtos;

    private final int totalPages;

    private final long totalElements;

    private final int pageSize;

    private int currentPageNumber;

    public CommentPageDto(List<CommentDto> commentDtos
            , int totalPages
            , long totalElements
            , int pageSize
            , int currentPageNumber) {

        this.commentDtos = commentDtos;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageSize = pageSize;
        this.currentPageNumber = currentPageNumber;

    }


}
