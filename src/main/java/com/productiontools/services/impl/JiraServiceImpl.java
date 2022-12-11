package com.productiontools.services.impl;
import com.productiontools.entities.jira.*;
import com.productiontools.enums.*;
import com.productiontools.repositories.*;
import com.productiontools.services.JiraService;
import com.productiontools.utils.JiraJsonParser;
import com.productiontools.utils.JiraPayloadBuilder;
import com.productiontools.utils.JiraRequestHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.*;

@Service
@EnableTransactionManagement
public class JiraServiceImpl implements JiraService {

	Logger logger = LoggerFactory.getLogger(JiraServiceImpl.class);

	@Autowired
	JiraUserRepository jiraUserRepository;

	@Autowired
	JiraProjectsRepository jiraProjectsRepository;

	@Autowired
	JiraVersionRepository jiraVersionRepository;

	@Autowired
	JiraGroupRepository jiraGroupRepository;

	@Autowired
	JiraSharedTeamRepository jiraSharedTeamRepository;

	@Autowired
	JiraComponentRepository jiraComponentRepository;

	@Autowired
	JiraRequestHandler jiraRequestHandler;

	@Autowired
	JiraPayloadBuilder jiraPayloadBuilder;

	@Autowired
	JiraJsonParser jiraJsonParser;

	@Override
	@Transactional
	public void updateJiraDatabase() throws UnirestException, FileNotFoundException, ParseException {
		removeJiraDatabase();
		updateJiraProjectsDatabase();
		updateJiraComponents();
		updateJiraVersionsDatabase();
		updateJiraGroupsDatabase();
		updateJiraSharedTeams();
		updateJiraUsersDatabase();
	}
	@Override
	@Transactional
	public void removeJiraDatabase() {
		jiraVersionRepository.deleteAll();
		jiraProjectsRepository.deleteAll();
		jiraComponentRepository.deleteAll();
		jiraUserRepository.deleteAll();
		jiraSharedTeamRepository.deleteAll();
		jiraGroupRepository.deleteAll();
	}

	@Override
	@Transactional
	public void updateJiraGroupsDatabase() throws UnirestException {
		List<JiraGroup> jiraGroups = new ArrayList<>();
		JSONArray jiraGroupsPayload = jiraRequestHandler.getJiraGroups();

		if(Objects.nonNull(jiraGroupsPayload)){
			for(int i = 0; i < jiraGroupsPayload.length(); i++){
				JSONObject jiraGroupDAO = jiraGroupsPayload.getJSONObject(i);

				jiraGroups.add(
						JiraGroup.builder()
							.groupId(jiraGroupDAO.getString("groupId"))
							.name(jiraGroupDAO.getString("name"))
							.build());
			}
		}
		jiraGroupRepository.saveAll(jiraGroups);
	}

	@Override
	@Transactional
	public List<JiraGroup> getJiraGroups() {
		return jiraGroupRepository.findAll();
	}

	@Override
	@Transactional
	public void updateJiraSharedTeams() throws FileNotFoundException, ParseException {
		List<JiraSharedTeam> jiraSharedTeams = new ArrayList<>();
		JSONArray sharedTeamsPayload = jiraJsonParser.getJiraSharedTeamsJSON("teams");

		for(int i = 0; i < sharedTeamsPayload.length(); i++) {
			JSONObject sharedTeamDAO = sharedTeamsPayload.getJSONObject(i);

			jiraSharedTeams.add(
					JiraSharedTeam.builder()
							.id(sharedTeamDAO.getInt("id"))
							.name(sharedTeamDAO.getString("title"))
							.build());
		}
		jiraSharedTeamRepository.saveAll(jiraSharedTeams);
	}

	@Override
	public List<JiraSharedTeam> getJiraSharedTeams() {
		return jiraSharedTeamRepository.findAll();
	}

	@Override
	@Transactional
	public void updateJiraUsersDatabase() throws UnirestException, FileNotFoundException, ParseException {
		List<JiraUser> jiraUsers = new ArrayList<>();
		JSONArray jiraUsersPayload = jiraRequestHandler.getJiraUsers();

		JSONArray sharedTeamPersonsPayload = jiraJsonParser.getJiraSharedTeamsJSON("persons");
		JSONArray sharedTeamTeamsPayload = jiraJsonParser.getJiraSharedTeamsJSON("teams");

		if(Objects.nonNull(jiraUsersPayload)){
			for(int i = 0; i < jiraUsersPayload.length(); i++){
				JSONObject jiraUserDAO = jiraUsersPayload.getJSONObject(i);

				String accountType = jiraUserDAO.getString("accountType");
				boolean isActive = jiraUserDAO.getBoolean("active");

				if(accountType.equals(Account.ATLASSIAN.getAccountType()) && isActive) {
					String accountId = jiraUserDAO.getString("accountId");
					String displayName = jiraUserDAO.getString("displayName");
					int personId = assignPersonIdToJiraUser(displayName, sharedTeamPersonsPayload);

					jiraUsers.add(
							JiraUser.builder()
									.accountId(accountId)
									.displayName(displayName)
									.personId(personId)
									.jiraGroups(assignGroupsToJiraUser(accountId))
									.jiraSharedTeams(assignSharedTeamsToJiraUser(personId, sharedTeamTeamsPayload))
									.build());
				}
			}
			jiraUserRepository.saveAll(jiraUsers);
		}
	}

	@Transactional
	private int assignPersonIdToJiraUser(String displayName, JSONArray sharedTeamPersonsPayload){
		int personId = 0;
		for(int i = 0; i < sharedTeamPersonsPayload.length(); i++){
			JSONObject jiraUser = sharedTeamPersonsPayload.getJSONObject(i).getJSONObject("jiraUser");

			if(jiraUser.get("title").equals(displayName)){
				personId =  sharedTeamPersonsPayload.getJSONObject(i).getInt("personId");
			}
		}
		return personId;
	}

	@Transactional
	private List<JiraSharedTeam> assignSharedTeamsToJiraUser(int personId, JSONArray sharedTeamTeamsPayload) {
		List<JiraSharedTeam> jiraSharedTeams = new ArrayList<>();

		for(int i = 0; i < sharedTeamTeamsPayload.length(); i ++){
			JSONObject sharedTeam = sharedTeamTeamsPayload.getJSONObject(i);
			JSONArray usersInSharedTeam = sharedTeam.getJSONArray("resources");

			if(usersInSharedTeam.length() > 0){
				for(int x = 0; x < usersInSharedTeam.length(); x++) {
					int personIdInTeam = usersInSharedTeam.getJSONObject(x).getInt("personId");

					if(personIdInTeam == personId) {
						Optional<JiraSharedTeam> optionalJiraSharedTeam = jiraSharedTeamRepository.findById(sharedTeam.getInt("id"));
						optionalJiraSharedTeam.ifPresent(jiraSharedTeams::add);
					}
				}
			}
		}
		return jiraSharedTeams;
	}

	@Transactional
	private List<JiraGroup> assignGroupsToJiraUser(String accountId) throws UnirestException {
		List<JiraGroup> jiraGroups = new ArrayList<>();
		JSONArray userGroupPayload = jiraRequestHandler.getJiraUserGroups(accountId).getArray();

		for(int i = 0; i < userGroupPayload.length(); i++) {
			String userGroupId = userGroupPayload.getJSONObject(i).getString("groupId");
			Optional<JiraGroup> optionalGroup = jiraGroupRepository.findById(userGroupId);
			optionalGroup.ifPresent(jiraGroups::add);
		}
		return jiraGroups;
	}

	@Override
	@Transactional
	public List<JiraUser> getJiraUsers() {
		return jiraUserRepository.findAll();
	}

	@Override
	@Transactional
	public void updateJiraComponents() throws UnirestException {
		List<JiraProject> jiraProjects = jiraProjectsRepository.findAll();

		for(JiraProject jiraProject: jiraProjects){
			List<JiraComponent> jiraComponents = new ArrayList<>();
			JSONArray jiraComponentsPayload = jiraRequestHandler.getJiraComponents(jiraProject.getKey());

			if(jiraComponentsPayload.length() > 0){

				for(int i = 0; i < jiraComponentsPayload.length(); i++){
					JSONObject jiraComponentDAO = jiraComponentsPayload.getJSONObject(i);
					String idComponent = jiraComponentDAO.getString("id");
					String name = jiraComponentDAO.getString("name");
					JiraComponent jiraComponent =JiraComponent.builder()
							.componentId(idComponent)
							.name(name)
							.projectKey(jiraProject.getKey())
							.build();
					jiraComponents.add(jiraComponent);
				}

				jiraComponentRepository.saveAll(jiraComponents);
			}

		}
	}



	@Override
	@Transactional
	public void updateJiraProjectsDatabase() throws UnirestException {
		List<JiraProject> jiraProjects = new ArrayList<>();
		JSONArray jiraProjectsPayload = jiraRequestHandler.getJiraProjects();

		if(Objects.nonNull(jiraProjectsPayload)) {
			for(int i = 0; i < jiraProjectsPayload.length(); i++){
				JSONObject jiraProjectDAO = jiraProjectsPayload.getJSONObject(i);

				jiraProjects.add(
						JiraProject.builder()
								.id(jiraProjectDAO.getInt("id"))
								.key(jiraProjectDAO.getString("key"))
								.name(jiraProjectDAO.getString("name"))
								.build()
				);
			}
			jiraProjectsRepository.saveAll(jiraProjects);
		}
	}

	@Override
	@Transactional
	public List<JiraProject> getJiraProjects() {
		return jiraProjectsRepository.findAll();
	}

	@Override
	@Transactional
	public void updateJiraVersionsDatabase() throws UnirestException {
		List<JiraVersion> jiraVersions = new ArrayList<>();
		List<JiraProject> jiraProjects = jiraProjectsRepository.findAll();

		for(JiraProject jiraProject: jiraProjects){
			JSONArray jiraVersionsPayload = jiraRequestHandler.getJiraVersions(jiraProject.getKey());

			for(int i = 0; i < jiraVersionsPayload.length(); i++){
				JSONObject jiraVersionDAO = jiraVersionsPayload.getJSONObject(i);
				jiraVersions.add(
						JiraVersion.builder()
								.id(jiraVersionDAO.getString("id"))
								.projectKey(jiraProject.getKey())
								.name(jiraVersionDAO.getString("name"))
								.build());
			}
			jiraVersionRepository.saveAll(jiraVersions);
		}
	}

	@Override
	@Transactional
	public List<JiraVersion> getJiraVersions() {
		return jiraVersionRepository.findAll();
	}

	@Override
	@Transactional
	public void clearRemainingTime(com.fasterxml.jackson.databind.JsonNode issuePayload) {
		String issueStatusId = issuePayload.get("issue").get("fields").get("status").get("id").asText();

		if (issueStatusId.equals(Status.DONE.getId()) || issueStatusId.equals(Status.CANCELLED.getId())) {
			com.fasterxml.jackson.databind.JsonNode timeTrackingPayload = issuePayload.get("issue").get("fields").get("timetracking");

			if(!timeTrackingPayload.get("originalEstimate").isNull()) {
				String issueKey = getJiraIssueKey(issuePayload);
				ObjectNode payload = jiraPayloadBuilder.buildTimetrackingPayload(timeTrackingPayload);

				jiraRequestHandler.putJiraIssue(issueKey, payload);
			}
		}
	}

	@Override
	@Transactional
	public void pingAssigneeIssueToDo(com.fasterxml.jackson.databind.JsonNode issuePayload) {
		if(issuePayload.get("issue").get("fields").get("assignee").isNull()) {
			return;
		}

		com.fasterxml.jackson.databind.JsonNode changelogItemsPayload = issuePayload.get("changelog").get("items");
		String itemsFieldPayload = changelogItemsPayload.get(0).get("field").asText();

		if(itemsFieldPayload.equalsIgnoreCase("status")) {
			String itemsChangeFromPayload = changelogItemsPayload.get(0).get("from").asText();
			String itemsChangeToPayload = changelogItemsPayload.get(0).get("to").asText();

			if (itemsChangeFromPayload.equals(Status.BLOCKED.getId()) || itemsChangeFromPayload.equals(Status.REVIEW.getId()) &&
					itemsChangeToPayload.equals(Status.TO_DO.getId())) {
				String assigneeIdPayload = issuePayload.get("issue").get("fields").get("assignee").get("accountId").asText();

				ObjectNode payload = jiraPayloadBuilder.buildPingPayload(changelogItemsPayload, assigneeIdPayload);
				String issueKey = getJiraIssueKey(issuePayload);

				jiraRequestHandler.postJiraIssue(issueKey, "comment", payload);
			}
		}
	}

	@Override
	@Transactional
	public void copyApproversFromParentToChild(com.fasterxml.jackson.databind.JsonNode issuePayload) throws UnirestException {
		String issueType = issuePayload.get("issue").get("fields").get("issuetype").get("name").asText();
		String issueKey = getJiraIssueKey(issuePayload);

		if(issueType.equalsIgnoreCase(Type.SUBTASK.getName())) {
				String parentKey = issuePayload.get("issue").get("fields").get("parent").get("key").asText();
				JsonNode parentPayload = jiraRequestHandler.getJiraIssue(parentKey);

				JSONArray approversPayload = parentPayload.getObject().getJSONObject("fields").getJSONArray(Customfield.APPROVERS.getFieldName());

				if(approversPayload.length() != 0) {
					ObjectNode payload = jiraPayloadBuilder.buildApproversPayload(approversPayload);
					jiraRequestHandler.putJiraIssue(issueKey, payload);
				}
		}
	}

	@Override
	@Transactional
	public void valueChangeFixVersions(com.fasterxml.jackson.databind.JsonNode issuePayload) {
		com.fasterxml.jackson.databind.JsonNode fixVersionsPayload = issuePayload.get("issue").get("fields").get("fixVersions");
		com.fasterxml.jackson.databind.JsonNode sprintPayload = issuePayload.get("issue").get("fields").get(Customfield.SPRINT.getFieldName());

		String issueKey = getJiraIssueKey(issuePayload);
		ObjectNode payload = jiraPayloadBuilder.buildFixVersionPayload(fixVersionsPayload, sprintPayload);

		if(!payload.isNull()) {
			jiraRequestHandler.putJiraIssue(issueKey, payload);
		}
	}

	@Override
	@Transactional
	public void valueChangeSprintVersions(com.fasterxml.jackson.databind.JsonNode issuePayload) {
		int lastCharacterSprintName = 5;
		int frameworkBoardId = 26;
		com.fasterxml.jackson.databind.JsonNode sprintPayload = issuePayload.get("issue").get("fields").get(Customfield.SPRINT.getFieldName());

		if(!sprintPayload.isNull()) {
			int boardId = Integer.parseInt(sprintPayload.get(0).get("boardId").asText());

			if(boardId == frameworkBoardId) {
				return;
			}

			String sprintName = "";
			if(sprintPayload.size() > 1) {
				for(int i = 0; i < sprintPayload.size(); i++){
					com.fasterxml.jackson.databind.JsonNode jiraSprint = sprintPayload.get(i);
					String sprintState = jiraSprint.get("state").asText();
					if(sprintState.equals("active")){
						sprintName = jiraSprint.get("name").asText().substring(0, lastCharacterSprintName);
						break;
					}
				}
			}else {
				sprintName = sprintPayload.get(0).get("name").asText().substring(0, lastCharacterSprintName);
			}

			logger.info("Sprint name:" + sprintName);

			String projectKey = issuePayload.get("issue").get("fields").get("project").get("key").asText();

			logger.info("Project key: " + projectKey);

			Optional<JiraVersion> jiraVersion = jiraVersionRepository.findByNameContainsAndProjectKey(sprintName, projectKey);

			if(jiraVersion.isPresent()) {
				String issueKey = getJiraIssueKey(issuePayload);
				ObjectNode payload = jiraPayloadBuilder.buildSprintVersionPayload(jiraVersion.get());

				jiraRequestHandler.putJiraIssue(issueKey, payload);
			}
		}
	}

	@Override
	@Transactional
	public void changeIssueStatus(com.fasterxml.jackson.databind.JsonNode issuePayload) throws UnirestException {
		String currentStatus = issuePayload.get("issue").get("fields").get("status").get("name").asText();
		com.fasterxml.jackson.databind.JsonNode sprintPayload = issuePayload.get("issue").get("fields").get(Customfield.SPRINT.getFieldName());
		String isSprintActive = sprintPayload.get(0).get("state").asText();

		if (!sprintPayload.isNull() && currentStatus.equalsIgnoreCase(Status.BACKLOG.getName()) && isSprintActive.equals(SprintState.ACTIVE.getState())) {
			String issueKey = getJiraIssueKey(issuePayload);
			String transitionId = getTransitionId(issueKey);
			ObjectNode payload = jiraPayloadBuilder.changeIssueStatus(transitionId);

			jiraRequestHandler.postJiraIssue(issueKey,"transitions", payload);
		}
	}

	@Transactional
	private String getTransitionId(String issueKey) throws UnirestException {
		String id = null;
		JsonNode response = jiraRequestHandler.getJiraIssue(issueKey,"transitions");
		JSONArray transitions = response.getObject().getJSONArray("transitions");

		for(int i = 0; i < transitions.length(); i++) {
			if(transitions.getJSONObject(i).get("name").equals(Status.TO_DO.getName())) {
				id = String.valueOf(transitions.getJSONObject(i).get("id"));
				break;
			}
		}
		return id;
	}

	@Override
	@Transactional
	public void setStartDate(com.fasterxml.jackson.databind.JsonNode issuePayload) {
		if(issuePayload.get("issue").get("fields").get(Customfield.START_DATE.getFieldName()).isNull()) {
			String issueKey = getJiraIssueKey(issuePayload);
			ObjectNode payload = jiraPayloadBuilder.setStartDate();
			jiraRequestHandler.putJiraIssue(issueKey, payload);
		}
	}

	@Override
	@Transactional
	public void assignSharedTeamToIssue(com.fasterxml.jackson.databind.JsonNode issuePayload) {
		String issueKey = getJiraIssueKey(issuePayload);
		com.fasterxml.jackson.databind.JsonNode shareTeamPayload = issuePayload.get("issue").get("fields").get(Customfield.TEAM.getFieldName());
		com.fasterxml.jackson.databind.JsonNode assigneePayload = issuePayload.get("issue").get("fields").get("assignee");

		if(assigneePayload.isNull()) {
			clearSharedTeamFromIssueIfPresent(issueKey);
			return;
		}

		String assigneeId = assigneePayload.get("accountId").asText();
		Optional<JiraUser> jiraUserOptional = jiraUserRepository.findById(assigneeId);

		if(jiraUserOptional.isPresent()) {
			JiraUser jiraUser = jiraUserOptional.get();

			if(shareTeamPayload.isNull()) {
				addShareTeamToIssue(issueKey, jiraUser);
			}else {
				checkIfSharedTeamIsCorrect(issueKey, jiraUser, shareTeamPayload);
			}
		}
	}

	@Transactional
	private void checkIfSharedTeamIsCorrect(String issueKey, JiraUser jiraUser, com.fasterxml.jackson.databind.JsonNode shareTeamPayload){
		if(!jiraUser.getJiraSharedTeams().isEmpty()){
			int issueSharedTeamId = shareTeamPayload.get("id").asInt();
			JiraSharedTeam issueSharedTeam = jiraSharedTeamRepository.findById(issueSharedTeamId).get();
			List<JiraSharedTeam> jiraUserSharedTeams = jiraUser.getJiraSharedTeams();

			if(!jiraUserSharedTeams.contains(issueSharedTeam)) {
				addShareTeamToIssue(issueKey, jiraUser);
			}
		}else {
			clearSharedTeamFromIssueIfPresent(issueKey);
		}
	}

	@Transactional
	private void clearSharedTeamFromIssueIfPresent(String issueKey){
		int clearSharedTeam = 0;
		ObjectNode payload = jiraPayloadBuilder.buildSharedTeamPayload(clearSharedTeam);
		jiraRequestHandler.putJiraIssue(issueKey, payload);
	}

	@Transactional
	private void addShareTeamToIssue(String issueKey, JiraUser jiraUser){
		if(!jiraUser.getJiraSharedTeams().isEmpty()) {
			int sharedTeamId = jiraUser.getJiraSharedTeams().get(0).getId();
			ObjectNode payload = jiraPayloadBuilder.buildSharedTeamPayload(sharedTeamId);
			jiraRequestHandler.putJiraIssue(issueKey, payload);
		}
	}


	private String getJiraIssueKey(com.fasterxml.jackson.databind.JsonNode issuePayload){
		return issuePayload.get("issue").get("key").asText();
	}
}
