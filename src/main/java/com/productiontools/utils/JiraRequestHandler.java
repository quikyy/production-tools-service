package com.productiontools.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class JiraRequestHandler  {

    @Value("${jira.username}")
    private String username;
    @Value("${jira.password}")
    private String password;

    @Value("${jira.issueUrl}")
    private String jiraIssueUrl;
    @Value("${jira.restApi2}")
    private String jiraRest2;
    @Value("${jira.restApi3}")
    private String jiraRest3;

    @Autowired
    JiraObjectMapper jiraObjectMapper;

    Logger logger = LoggerFactory.getLogger(JiraRequestHandler.class);

    @Transactional
    public JsonNode getJiraIssue(String issueKey) throws UnirestException {
        return Unirest.get(jiraIssueUrl + issueKey)
                .headers(setHeaders())
                .asJson()
                .getBody();
    }

    @Transactional
    public JsonNode getJiraIssue(String issueKey, String pathVariable) throws UnirestException {
        return Unirest.get(jiraIssueUrl + issueKey + "/" + pathVariable)
                .headers(setHeaders())
                .asJson()
                .getBody();
    }

    @Transactional
    public void postJiraIssue(String issueKey, ObjectNode payload){
        try {
            logger.info(issueKey + " -- " + payload );
            Unirest.setObjectMapper(jiraObjectMapper);
            HttpResponse<JsonNode> response = Unirest.post(jiraIssueUrl + issueKey)
                    .headers(setHeaders())
                    .body(payload)
                    .asJson();
            logger.info(issueKey + " -- " + response.getStatus());
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void postJiraIssue(String issueKey, String pathVariable, ObjectNode payload){
        try {
            logger.info(issueKey + " -- " + payload );
            Unirest.setObjectMapper(jiraObjectMapper);
            HttpResponse<JsonNode> response = Unirest.post(jiraIssueUrl + issueKey + "/" + pathVariable)
                    .headers(setHeaders())
                    .body(payload)
                    .asJson();
            logger.info(issueKey + " -- " + response.getStatus());
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public void putJiraIssue(String issueKey, ObjectNode payload)  {
        try {
            logger.info(issueKey + " -- " + payload );
            Unirest.setObjectMapper(jiraObjectMapper);
            HttpResponse<JsonNode> response = Unirest.put(jiraIssueUrl + issueKey)
                    .headers(setHeaders())
                    .body(payload)
                    .asJson();
            logger.info(issueKey + " -- " + response.getStatus());
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void putJiraIssue(String issueKey, String pathVariable, ObjectNode payload){
        try {
            logger.info(issueKey + " -- " + payload );
            Unirest.setObjectMapper(jiraObjectMapper);
            HttpResponse<JsonNode> response = Unirest.put(jiraIssueUrl + issueKey + "/" + pathVariable)
                    .headers(setHeaders())
                    .body(payload)
                    .asJson();
            logger.info(issueKey + " -- " + response.getStatus());
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONArray getJiraUsers() throws UnirestException {
        return Unirest.get(jiraRest2 + "users/search?maxResults=250")
                .headers(setHeaders())
                .asJson()
                .getBody()
                .getArray();
    }

    public JSONArray getJiraProjects() throws UnirestException {
        return Unirest.get(jiraRest3 + "project/search")
                .headers(setHeaders())
                .asJson()
                .getBody()
                .getObject()
                .getJSONArray("values");
    }

    public JSONArray getJiraGroups() throws UnirestException {
        return Unirest.get(jiraRest3 + "group/bulk")
                .headers(setHeaders())
                .asJson()
                .getBody()
                .getObject()
                .getJSONArray("values");
    }

    public JsonNode getJiraUserGroups(String accountId) throws UnirestException {
        return Unirest.get(jiraRest3 + "user/groups?accountId=" + accountId)
                .headers(setHeaders())
                .asJson()
                .getBody();
    }

    public JSONArray getJiraVersions(String projectKey) throws UnirestException {
        return Unirest.get(jiraRest3 + "project/" + projectKey + "/versions")
                .headers(setHeaders())
                .asJson()
                .getBody()
                .getArray();
    }

    public JSONArray getJiraComponents(String projectKey) throws UnirestException {
        return Unirest.get(jiraRest3 + "project/" + projectKey + "/components")
                .headers(setHeaders())
                .asJson()
                .getBody()
                .getArray();
    }

    public JSONArray getJiraProjectsIssueTypes(String projectKey) throws UnirestException {
        return Unirest.get(jiraRest2 + "project/" + projectKey)
                .headers(setHeaders())
                .asJson()
                .getBody()
                .getObject()
                .getJSONArray("issueTypes");
    }

    public String getJiraIssueTypeByName(String projectKey, String issueTypeNameDAO) throws UnirestException {
        JSONArray issueTypesPayload = getJiraProjectsIssueTypes(projectKey);

        for(int i = 0; i < issueTypesPayload.length(); i++){
            String issueTypeName = issueTypesPayload.getJSONObject(i).getString("name");

            if(issueTypeName.equalsIgnoreCase(issueTypeNameDAO)){
                return  issueTypesPayload.getJSONObject(i).getString("id");
            }
        }
        throw new IllegalArgumentException("No issue type with name: " + issueTypesPayload);
    }

    public HttpResponse<JsonNode> createJiraIssue(ObjectNode newJiraIssuePayload) throws UnirestException {
        Unirest.setObjectMapper(jiraObjectMapper);
        return Unirest.post(jiraRest2 + "issue")
                .headers(setHeaders())
                .body(newJiraIssuePayload)
                .asJson();
    }


    private Map<String, String> setHeaders(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", getBase64BasicAuth());
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("X-Force-Accept-Language", "true");
        headers.put("Accept-Language", "en");
        return headers;
    }

    private String getBase64BasicAuth(){
        String toEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(toEncode.getBytes());
    }

}
