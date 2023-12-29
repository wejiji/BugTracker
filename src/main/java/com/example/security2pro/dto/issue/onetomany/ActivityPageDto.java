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

    List<ActivityDto> activityDtos = new ArrayList<>();
    int totalPages;
    long totalElements;
    int pageSize;
    int currentPageNumber;

    public ActivityPageDto(Page<ActivityDto> activityPage){
        activityDtos = activityPage.getContent();
        totalPages = activityPage.getTotalPages();
        totalElements = activityPage.getTotalElements();
        pageSize = activityPage.getSize();
        currentPageNumber = activityPage.getNumber();
    }


}
