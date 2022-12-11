package com.productiontools.repositories;

import com.productiontools.entities.jira.JiraProject;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JiraProjectsRepository extends JpaRepository<JiraProject, Integer> {
    JiraProject findByKey(String key);

}
