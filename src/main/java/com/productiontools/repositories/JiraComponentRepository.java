package com.productiontools.repositories;

import com.productiontools.entities.jira.JiraComponent;
import com.productiontools.entities.jira.JiraVersion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JiraComponentRepository extends CrudRepository<JiraComponent, Integer> {
    List<JiraComponent> findByProjectKey(String projectKey);

    Optional<JiraComponent> findByNameAndProjectKey(String name, String projectKey);

}
