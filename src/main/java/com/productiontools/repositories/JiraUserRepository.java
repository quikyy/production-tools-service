package com.productiontools.repositories;

import com.productiontools.entities.jira.JiraUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JiraUserRepository extends JpaRepository<JiraUser, String> {
	JiraUser findByDisplayName(String displayName);
	JiraUser findByAccountId(String accountId);





}
