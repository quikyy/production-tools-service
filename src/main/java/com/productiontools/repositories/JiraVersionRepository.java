package com.productiontools.repositories;

import com.productiontools.entities.jira.JiraVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JiraVersionRepository extends JpaRepository<JiraVersion, Integer> {
	Optional<JiraVersion> findByNameContainsAndProjectKey(String name, String projectKey);
	List<JiraVersion> findByProjectKey(String projectKey);

    Optional<JiraVersion> findByNameAndProjectKey(String versionName, String key);
}
