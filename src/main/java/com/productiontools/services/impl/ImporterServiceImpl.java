package com.productiontools.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.productiontools.entities.importer.JiraIssue;
import com.productiontools.entities.jira.*;
import com.productiontools.enums.Status;
import com.productiontools.enums.Type;
import com.productiontools.repositories.*;
import com.productiontools.services.ImporterService;
import com.productiontools.utils.JiraPayloadBuilder;
import com.productiontools.utils.JiraRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class ImporterServiceImpl implements ImporterService {

    Logger logger = LoggerFactory.getLogger(ImporterServiceImpl.class);

    @Autowired
    JiraUserRepository jiraUserRepository;

    @Autowired
    JiraComponentRepository jiraComponentRepository;

    @Autowired
    JiraVersionRepository jiraVersionRepository;

    @Autowired
    JiraSharedTeamRepository jirasharedTeamRepository;

    @Autowired
    JiraPayloadBuilder jiraPayloadBuilder;

    @Autowired
    JiraProjectsRepository jiraProjectsRepository;

    @Autowired
    JiraRequestHandler jiraRequestHandler;

    @Override
    @Transactional
    public List<JiraSharedTeam> getJiraUserSharedTeams(String jiraUserDisplayName) {
        return jiraUserRepository.findByDisplayName(jiraUserDisplayName).getJiraSharedTeams();
    }

    @Override
    @Transactional
    public List<JiraComponent> getJiraComponentsPerProject(String projectKey) {
        return jiraComponentRepository.findByProjectKey(projectKey);
    }

    @Override
    @Transactional
    public List<JiraVersion> getJiraVersionsPerProject(String projectKey) {
        return jiraVersionRepository.findByProjectKey(projectKey);
    }

    @Override
    @Transactional
    public String createJiraIssue(JsonNode payload) throws UnirestException {
        JiraProject project = getJiraIssueProject(payload.get("project").asText());
        Status status = getJiraIssueStatus(payload.get("status").asText());
        String typeId = getJiraIssueType(payload.get("type").asText(),project.getKey());

        String summary = payload.get("summary").asText();

        JiraUser assignee = getJiraUser(payload.get("assignee").asText());
        JiraUser approver = getJiraUser(payload.get("approver").asText());
        JiraSharedTeam sharedTeam = getJiraSharedTeam(payload.get("sharedTeam").asText());

        Optional <JiraComponent> component = getJiraComponents(payload.get("components").asText(), project);
        Optional <JiraVersion> version = getJiraVersion(payload.get("version").asText(), project);

        JiraIssue jiraIssue = JiraIssue.builder()
                .project(project)
                .status(status)
                .typeId(typeId)
                .summary(summary)
                .assignee(assignee)
                .approver(approver)
                .sharedTeam(sharedTeam)
                .component(component)
                .version(version)
                .build();

        ObjectNode jiraNewIssuePayload = jiraPayloadBuilder.buildJiraIssue(jiraIssue);
        HttpResponse<com.mashape.unirest.http.JsonNode> response = jiraRequestHandler.createJiraIssue(jiraNewIssuePayload);

        if(response.getStatus() == 201){
            return response.getBody().getObject().getString("key");
        }
        else {
            return "Error! " + response.getBody().getObject();
        }
    }

    private Optional <JiraComponent> getJiraComponents(String componentName, JiraProject project) {
        if(componentName.isEmpty()) {
            return Optional.empty();
        }
        return jiraComponentRepository.findByNameAndProjectKey(componentName, project.getKey());
    }

    private Optional <JiraVersion> getJiraVersion(String versionName, JiraProject project) {
        if(versionName.isEmpty()) {
            return Optional.empty();
        }
        return jiraVersionRepository.findByNameAndProjectKey(versionName, project.getKey());
    }

    private JiraProject getJiraIssueProject(String projectKey){
        return jiraProjectsRepository.findByKey(projectKey);
    }

    private Status getJiraIssueStatus(String statusKey){
        return Status.getStatus(statusKey);
    }

    private String getJiraIssueType(String typeKey, String projectKey) throws UnirestException {
        return jiraRequestHandler.getJiraIssueTypeByName(projectKey, typeKey);
    }

    private JiraUser getJiraUser(String displayName){
        return jiraUserRepository.findByDisplayName(displayName);
    }

    private JiraSharedTeam getJiraSharedTeam(String name){
        return jirasharedTeamRepository.findByName(name);
    }


}
