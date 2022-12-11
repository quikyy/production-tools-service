package com.productiontools.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.productiontools.entities.jira.JiraComponent;
import com.productiontools.entities.jira.JiraSharedTeam;
import com.productiontools.entities.jira.JiraVersion;
import com.productiontools.services.ImporterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/importer")
public class ImporterController {

    @Autowired
    ImporterService importerService;

    @RequestMapping(path = "/jiraUser/sharedTeams/{jiraUserDisplayName}")
    public List<JiraSharedTeam> getJiraUserSharedTeams(@PathVariable String jiraUserDisplayName){
        return importerService.getJiraUserSharedTeams(jiraUserDisplayName);
    }

    @RequestMapping(path = "/jiraComponents/{projectKey}")
    public List <JiraComponent> getJiraComponentsPerProject(@PathVariable String projectKey){
        return importerService.getJiraComponentsPerProject(projectKey);
    }

    @RequestMapping(path = "/jiraVersions/{projectKey}")
    public List <JiraVersion> getJiraVersionsPerProject(@PathVariable String projectKey){
        return importerService.getJiraVersionsPerProject(projectKey);
    }

    @RequestMapping(path = "/createIssue")
    public String createJiraIssue(@RequestBody JsonNode payload) throws UnirestException {
        return importerService.createJiraIssue(payload);
    }

}
