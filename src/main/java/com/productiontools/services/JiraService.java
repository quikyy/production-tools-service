package com.productiontools.services;

//import com.mashape.unirest.http.JsonNode;
import com.productiontools.entities.jira.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.List;

@Service
public interface JiraService {
	void updateJiraDatabase() throws UnirestException, FileNotFoundException, ParseException;

	void updateJiraGroupsDatabase() throws UnirestException;

	List<JiraGroup> getJiraGroups();

	void updateJiraSharedTeams() throws FileNotFoundException, ParseException;
	List<JiraSharedTeam> getJiraSharedTeams();

	void updateJiraUsersDatabase() throws UnirestException, FileNotFoundException, ParseException;

	List<JiraUser> getJiraUsers();

	void updateJiraProjectsDatabase() throws UnirestException;

	List<JiraProject> getJiraProjects();

	void updateJiraVersionsDatabase() throws UnirestException;

	List<JiraVersion> getJiraVersions();

    void clearRemainingTime(JsonNode issuePayload);

	void pingAssigneeIssueToDo(JsonNode issuePayload);

	void copyApproversFromParentToChild(JsonNode issuePayload) throws UnirestException;

	void valueChangeFixVersions(JsonNode issuePayload);

	void valueChangeSprintVersions(JsonNode issuePayload);

	void changeIssueStatus(JsonNode issuePayload) throws UnirestException;

    void setStartDate(JsonNode issuePayload);

	void removeJiraDatabase();

    void assignSharedTeamToIssue(JsonNode issuePayload);

	void updateJiraComponents() throws UnirestException;
}
