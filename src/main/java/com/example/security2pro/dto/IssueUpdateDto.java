package com.example.security2pro.dto;

import com.example.security2pro.domain.model.Issue;
import com.example.security2pro.domain.model.ProjectMember;
import com.example.security2pro.domain.model.Sprint;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Getter
@Setter
public class IssueUpdateDto {
    @Valid
    private IssueDto issueDto;
    @Valid
    private ProjectDto projectDto;

    public IssueUpdateDto(){}
    public IssueUpdateDto(IssueDto issueDto, ProjectDto projectDto) {
        this.issueDto = issueDto;
        this.projectDto = projectDto;
    }

}
