package com.productiontools.repositories;

import com.productiontools.entities.jira.JiraGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JiraGroupRepository extends JpaRepository<JiraGroup, String> {
}
