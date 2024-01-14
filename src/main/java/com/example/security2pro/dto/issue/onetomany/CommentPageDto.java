package com.example.security2pro.dto.issue.onetomany;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class CommentPageDto {

    private final List<CommentDto> commentDtos;
    private final int totalPages;
    private final long totalElements;
    private final int pageSize;
    private int currentPageNumber;

//    public CommentPageDto(Page<CommentDto> commentPage){
//        commentDtos = commentPage.getContent();
//        totalPages = commentPage.getTotalPages();
//        totalElements = commentPage.getTotalElements();
//        pageSize = commentPage.getSize();
//        currentPageNumber = commentPage.getNumber();
//    }

    public CommentPageDto(List<CommentDto> commentDtos, int totalPages, long totalElements, int pageSize ,int currentPageNumber){
        this.commentDtos = commentDtos;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageSize = pageSize;
        this.currentPageNumber = currentPageNumber;

    }


}
