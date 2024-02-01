package com.example.bugtracker.dto.issue.authorization;

import java.util.Optional;

public interface CreateDtoWithProjectId {
    Optional<Long> getProjectId();
}
