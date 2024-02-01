package com.example.bugtracker.repository.jpa_repository;

import com.example.bugtracker.domain.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectJpaRepository extends JpaRepository<Project,Long> {

}
