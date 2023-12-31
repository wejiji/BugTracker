package com.example.security2pro.repository.repository_interfaces;


import com.example.security2pro.domain.model.Activity;
import com.example.security2pro.dto.issue.onetomany.ActivityPageDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ActivityRepository {

    ActivityPageDto findAllByIssueId(Long issueId, int offset, int limit);

    void deleteById(Long id);

    Activity save(Activity activity);

    Optional<Activity> findById(Long targetId);

    List<Activity> findByIssueIdIn(Set<Long> issueIds);

    void deleteAllByIdInBatch(Set<Long> idsToBeDeleted);

}
