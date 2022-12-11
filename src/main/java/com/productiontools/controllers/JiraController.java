package com.productiontools.controllers;

import com.productiontools.entities.jira.*;
import com.productiontools.services.JiraService;
import com.fasterxml.jackson.databind.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;


@RestController
@RequestMapping("/jira/api/feature")
public class JiraController {

	@Autowired
	JiraService jiraService;

	@GetMapping(path = "/updateJiraDatabase")
	public void updateJiraDatabase() throws UnirestException, FileNotFoundException, ParseException {
		jiraService.updateJiraDatabase();
	}

	@GetMapping(path = "/removeJiraDatabase")
	public void removeJiraDatabase(){
		jiraService.removeJiraDatabase();
	}

	@GetMapping(path = "/updateJiraGroupsDatabase")
	public void updateJiraGroupsDatabase() throws UnirestException {
		jiraService.updateJiraGroupsDatabase();
	}

	@GetMapping(path = "/getGroups")
	public List<JiraGroup> getJiraGroups(){
		return jiraService.getJiraGroups();
	}

	@GetMapping(path = "/updateJiraSharedTeams")
	public void updateJiraSharedTeams() throws FileNotFoundException, ParseException {
		jiraService.updateJiraSharedTeams();
	}

	@GetMapping(path = "/getSharedTeams")
	public List<JiraSharedTeam> getJiraSharedTeams(){
		return jiraService.getJiraSharedTeams();
	}

	@GetMapping(path = "/updateJiraUsersDatabase")
	public void updateJiraUsersDatabase() throws UnirestException, FileNotFoundException, ParseException {
		jiraService.updateJiraUsersDatabase();
	}

	@GetMapping(path = "/getUsers")
	public List<JiraUser> getJiraUsers() {
		return jiraService.getJiraUsers();
	}

	@GetMapping(path = "/updateJiraComponents")
	public void updateJiraComponents() throws UnirestException {
		jiraService.updateJiraComponents();
	}

	@GetMapping(path = "/updateJiraProjectsDatabase")
	public void updateJiraProjects() throws UnirestException {
		jiraService.updateJiraProjectsDatabase();
	}

	@GetMapping(path = "/getProjects")
	public List<JiraProject> getJiraProjects(){
		return jiraService.getJiraProjects();
	}

	@GetMapping(path = "/updateJiraVersionsDatabase")
	public void updateJiraVersionsDatabase() throws UnirestException {
		jiraService.updateJiraVersionsDatabase();
	}

	@GetMapping(path = "/getVersions")
	public List<JiraVersion> getJiraVersions(){
		return jiraService.getJiraVersions();
	}

	@RequestMapping(path = "/updateRemainingTime")
	public void updateRemainingTime(@RequestBody JsonNode issuePayload) {
		jiraService.clearRemainingTime(issuePayload);
	}

	@RequestMapping(path = "/pingAssigneeTodo")
	public void pingAssigneeTodo(@RequestBody JsonNode issuePayload){
		jiraService.pingAssigneeIssueToDo(issuePayload);
	}

	@RequestMapping(path = "/copyApproversFromParentToChild")
	public void copyApproversFromParentToChild(@RequestBody JsonNode issuePayload) throws UnirestException {
		jiraService.copyApproversFromParentToChild(issuePayload);
	}

	@RequestMapping(path = "/valueChangeFixVersions")
	public void valueChangeFixVersions(@RequestBody JsonNode issuePayload) {
		jiraService.valueChangeFixVersions(issuePayload);
	}

	@RequestMapping(path = "/valueChangeSprintVersions")
	public void valueChangeSprintVersions(@RequestBody JsonNode issuePayload) {
		jiraService.valueChangeSprintVersions(issuePayload);
	}

	@RequestMapping(path = "/changeIssueStatus")
	public void changeIssueStatus(@RequestBody JsonNode issuePayload) throws UnirestException {
		jiraService.changeIssueStatus(issuePayload);
	}

	@RequestMapping(path = "/setStartDate")
	public void setDueDate(@RequestBody JsonNode issuePayload) {
		jiraService.setStartDate(issuePayload);
	}

	@RequestMapping(path = "/assignSharedTeamToIssue")
	public void assignSharedTeamToIssue(@RequestBody JsonNode issuePayload){
		jiraService.assignSharedTeamToIssue(issuePayload);
	}
}
