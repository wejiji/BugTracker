package com.example.security2pro.repository.jpa_repository;

import com.example.security2pro.domain.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectJpaRepository extends JpaRepository<Project,Long> {

}
