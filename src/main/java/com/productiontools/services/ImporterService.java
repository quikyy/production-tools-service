package com.productiontools.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.productiontools.entities.jira.JiraComponent;
import com.productiontools.entities.jira.JiraSharedTeam;
import com.productiontools.entities.jira.JiraVersion;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ImporterService {
    List <JiraSharedTeam> getJiraUserSharedTeams(String jiraUserDisplayName);

    List<JiraComponent> getJiraComponentsPerProject(String projectKey);

    List<JiraVersion> getJiraVersionsPerProject(String projectKey);

    String createJiraIssue(JsonNode payload) throws UnirestException;
}
