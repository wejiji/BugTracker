package com.example.security2pro.repository.repository_impls;

import com.example.security2pro.domain.model.Activity;
import com.example.security2pro.dto.issue.onetomany.ActivityPageDto;
import com.example.security2pro.repository.jpa_repository.ActivityJpaRepository;
import com.example.security2pro.repository.repository_interfaces.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.example.security2pro.dto.issue.onetomany.ActivityDto;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ActivityRepositoryImpl implements ActivityRepository {


    private final ActivityJpaRepository activityJpaRepository;

    @Override
    public ActivityPageDto findAllByIssueId(Long issueId, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset,limit);
        return new ActivityPageDto(activityJpaRepository.findAllByIssueId(issueId, pageRequest).map(ActivityDto::new));
    }

    @Override
    public void deleteById(Long id) {
        activityJpaRepository.deleteById(id);
    }

    @Override
    public Activity save(Activity activity) {
        return activityJpaRepository.save(activity);
    }

    @Override
    public Optional<Activity> findById(Long targetId) {
        return activityJpaRepository.findById(targetId);
    }

    @Override
    public List<Activity> findByIssueIdIn(Set<Long> issueIds) {
        return activityJpaRepository.findByIssueIdIn(issueIds);
    }

    @Override
    public void deleteAllByIdInBatch(Set<Long> idsToBeDeleted) {
        activityJpaRepository.deleteAllByIdInBatch(idsToBeDeleted);
    }


}
