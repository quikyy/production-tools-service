package com.productiontools.utils;

import com.productiontools.entities.importer.JiraIssue;
import com.productiontools.entities.jira.JiraUser;
import com.productiontools.entities.jira.JiraVersion;
import com.productiontools.enums.Customfield;
import com.productiontools.repositories.JiraUserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class JiraPayloadBuilder {

    Logger logger = LoggerFactory.getLogger(JiraPayloadBuilder.class);

    @Autowired
    JiraUserRepository jiraUserRepository;

    public ObjectNode buildTimetrackingPayload(JsonNode timeTrackingPayload){
        ObjectNode payload = getEmptyPayload();
        ObjectNode fields = payload.putObject("fields");
        ObjectNode timetracking = fields.putObject("timetracking");
        {
            timetracking.put("remainingEstimate", "0h");
            timetracking.put("originalEstimate", timeTrackingPayload.get("originalEstimate").asText());
        }
        return payload;
    }

    public ObjectNode buildPingPayload(JsonNode changelogItemsPayload, String assigneeIdPayload){
        String previousIssueStatus = changelogItemsPayload.get(0).get("fromString").asText();
        String currentIssueStatus = changelogItemsPayload.get(0).get("toString").asText();
        JiraUser jiraUser = jiraUserRepository.findByAccountId(assigneeIdPayload);

        ObjectNode payload = getEmptyPayload();
        ObjectNode body = payload.putObject("body");
        {
            body.put("type", "doc");
            body.put("version", 1);
        }

        ArrayNode contentPayload = body.putArray("content");
        ObjectNode contentPayload0 = contentPayload.addObject();
        {
            contentPayload0.put("type", "paragraph");
            ArrayNode contentArray = contentPayload0.putArray("content");

            ObjectNode contentMentionObject = contentArray.addObject();
            contentMentionObject.put("type", "mention");
            ObjectNode attrs = contentMentionObject.putObject("attrs");
            {
                attrs.put("id", jiraUser.getAccountId());
                attrs.put("text", "@" + jiraUser.getDisplayName());
                attrs.put("accessLevel", "");
            }

            ObjectNode contentTextObject = contentArray.addObject();
            {
                contentTextObject.put("type", "text");
                contentTextObject.put("text", " status has been changed: " + previousIssueStatus + "  ➝ " + currentIssueStatus);
            }
        }
        return payload;
    }

    public ObjectNode buildApproversPayload(JSONArray approversPayload){
        ObjectNode payload = getEmptyPayload();
        ObjectNode fields = payload.putObject("fields");
        ArrayNode approvers = fields.putArray(Customfield.APPROVERS.getFieldName());

        for(int i = 0; i < approversPayload.length(); i ++) {
            String accountId = approversPayload.getJSONObject(i).get("accountId").toString();
            approvers.addObject().put("accountId", accountId);
        }
        return payload;
    }

    public ObjectNode buildFixVersionPayload(JsonNode fixVersionsPayload, JsonNode sprintPayload){
        int lastCharacterSprintName = 5;
        ObjectNode payload = getEmptyPayload();
        ObjectNode fields = payload.putObject("fields");

        if(fixVersionsPayload.size() == 0) {
            fields.put(Customfield.SPRINT.getFieldName(), (String) null);
        }
        else if(!sprintPayload.isNull() && fixVersionsPayload.size() == 1) {
            String sprintName = sprintPayload.get(0).get("name").asText().substring(0, lastCharacterSprintName);
            String fixVersionName = fixVersionsPayload.get(0).get("name").asText().substring(0, lastCharacterSprintName);

            if(!sprintName.equals(fixVersionName)){
                fields.put(Customfield.SPRINT.getFieldName(), (String) null);
            }
        }
        else if(fixVersionsPayload.size() > 1) {
            String correctVersionId = fixVersionsPayload.get(0).get("id").asText();
            ArrayNode fixVersions = fields.putArray("fixVersions");
            {
                ObjectNode fixVersions0 = fixVersions.addObject();
                fixVersions0.put("id", correctVersionId);
            }
        }
        return payload;
    }

    public ObjectNode buildSprintVersionPayload(JiraVersion jiraVersion) {
        ObjectNode payload = getEmptyPayload();
        ObjectNode fields = payload.putObject("fields");
        ArrayNode fixVersions = fields.putArray("fixVersions");
        {
            ObjectNode fixVersions0 = fixVersions.addObject();
            fixVersions0.put("id", jiraVersion.getId());
        }
        return payload;
    }

    public ObjectNode changeIssueStatus(String transitionId){
        ObjectNode payload = getEmptyPayload();
        ObjectNode transition = payload.putObject("transition");
        {
            transition.put("id", transitionId);
        }
        return payload;
    }

    public ObjectNode setStartDate(){
        ObjectNode payload = getEmptyPayload();
        ObjectNode fields = payload.putObject("fields");
        {
            fields.put("customfield_10015", String.valueOf(LocalDate.now()));
        }
        return payload;
    }

    public ObjectNode buildSharedTeamPayload(int sharedTeamId) {
        int clearSharedTeam = 0;
        ObjectNode payload = getEmptyPayload();
        ObjectNode fields = payload.putObject("fields");

        if(sharedTeamId == clearSharedTeam){
            fields.put(Customfield.TEAM.getFieldName(), (String) null);
        }else {
            fields.put(Customfield.TEAM.getFieldName(), String.valueOf(sharedTeamId));
        }
        return payload;
    }

    public ObjectNode buildJiraIssue(JiraIssue jiraIssue){
        ObjectNode payload = getEmptyPayload();
        ObjectNode fields = payload.putObject("fields");

        ObjectNode project = fields.putObject("project");
        project.put("id", jiraIssue.getProject().getId());

        ObjectNode issuetype = fields.putObject("issuetype");
        issuetype.put("id", jiraIssue.getTypeId());

        ObjectNode assignee = fields.putObject("assignee");
        assignee.put("accountId", jiraIssue.getAssignee().getAccountId());

        ArrayNode approver = fields.putArray(Customfield.APPROVERS.getFieldName());
        approver.addObject().put("accountId", jiraIssue.getApprover().getAccountId());

        ObjectNode reporter = fields.putObject("reporter");
        reporter.put("id", "616d2600e79ff6006f108acd");

        ObjectNode summary = fields.put("summary", jiraIssue.getSummary());

        if(jiraIssue.getComponent().isPresent()) {
            ArrayNode components = fields.putArray("components");
            components.addObject().put("id", jiraIssue.getComponent().get().getComponentId());
        }

        if(jiraIssue.getVersion().isPresent()) {
            ArrayNode fixVersions = fields.putArray("fixVersions");
            fixVersions.addObject().put("id", jiraIssue.getVersion().get().getId());
        }

        return payload;
    }

    private ObjectNode getEmptyPayload(){
        JsonNodeFactory jnf = JsonNodeFactory.instance;
        return jnf.objectNode();
    }


}
