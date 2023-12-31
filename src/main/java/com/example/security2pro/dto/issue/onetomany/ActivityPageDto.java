package com.example.security2pro.dto.issue.onetomany;

import com.example.security2pro.domain.model.Activity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ActivityPageDto {

    private final List<ActivityDto> activityDtos;
    private final int totalPages;
    private final long totalElements;
    private final int pageSize;
    private int currentPageNumber;

    public ActivityPageDto(Page<ActivityDto> activityPage){
        activityDtos = activityPage.getContent();
        totalPages = activityPage.getTotalPages();
        totalElements = activityPage.getTotalElements();
        pageSize = activityPage.getSize();
        currentPageNumber = activityPage.getNumber();
    }


}
