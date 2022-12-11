package com.productiontools.utils;

import com.google.gson.Gson;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.FileNotFoundException;
import java.io.FileReader;


@Component
public class JiraJsonParser {

    @Value("${sharedTeams.JsonPath}")
    private String sharedTeamsJsonPath;

    public JSONArray getJiraSharedTeamsJSON(String userRequest) throws FileNotFoundException, ParseException {
        FileReader sharedTeamJson = new FileReader(sharedTeamsJsonPath);
        Object sharedTeamPOJO = new JSONParser(sharedTeamJson).parse();
        String sharedTeamString = new Gson().toJson(sharedTeamPOJO);
        return new JSONObject(sharedTeamString).getJSONArray(userRequest);
    }
}
