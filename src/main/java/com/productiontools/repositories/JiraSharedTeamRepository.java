package com.productiontools.repositories;

import com.productiontools.entities.jira.JiraSharedTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JiraSharedTeamRepository extends JpaRepository<JiraSharedTeam, Integer> {

    JiraSharedTeam findByName(String name);


}
